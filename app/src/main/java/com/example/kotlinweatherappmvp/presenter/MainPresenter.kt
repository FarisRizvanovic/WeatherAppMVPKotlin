package com.example.kotlinweatherappmvp.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.example.kotlinweatherappmvp.fragment.MainFragment
import com.example.kotlinweatherappmvp.service.WeatherApiService
import com.example.kotlinweatherappmvp.view.IMainView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory

class MainPresenter(mainFragment: MainFragment) : IMainPresenter {

    private val iMainView = mainFragment as IMainView

    /**
     *Takes the latitude and longitude to make a Retrofit request
     * to retrieve weather data
     */
    override fun getWeatherLatLon(lat: String, lon: String) {

        GlobalScope.launch(IO) {
            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)
            try {
                val response =
                    api.getWeather(
                        lat, lon, EXCLUDE_LIST, API_KEY, UNITS
                    ).awaitResponse()

                if (response.isSuccessful) {
                    val weather = response.body()!!
                    val iconLink = weather.current.weather[0].icon

                    withContext(Main) {
                        iMainView.getWeatherLatLon(weather, iconLink)
                    }
                }
            } catch (e: Exception) {
                withContext(Main) {
                    iMainView.onError("Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Takes the city name to get it's latitude and longitude
     * via a Retrofit call
     */
    override fun getLatLonByCityName(cityName: String) {
        GlobalScope.launch(IO) {
            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApiService::class.java)

            try {
                val response = api.getCityLatLonByName(cityName, API_KEY).awaitResponse()

                withContext(Main) {
                    if (response.code() == 404) {
                        iMainView.onError("Please Input A Valid City Name!")
                    }
                }

                if (response.isSuccessful) {
                    val weatherForSearch = response.body()!!
                    val cords = weatherForSearch.coord
                    val lat = cords.lat.toString()
                    val lon = cords.lon.toString()

                    withContext(Main) {
                        iMainView.getWeatherByCityName(lat, lon)
                    }
                }
            } catch (e: Exception) {
                withContext(Main) {
                    iMainView.onError("Error: ${e.message}")
                }
            }
        }
    }

    /**
     * Enables GPS
     */
    override fun enableGps(context: Context) {
        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    /**
     * Uses the phone LOCATION SERVICES to get
     * the latitude and longitude
     */
    override fun getLatLon(context: Context) {
        val lat: String
        val lon: String

        /**
         * Re-checking the location permissions
         */
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val locationNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val locationPassive =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)

            /**
             * Checks for the available location data
             */
            when {
                locationGps != null -> {
                    lat = locationGps.latitude.toString()
                    lon = locationGps.longitude.toString()

                    iMainView.getLatLonFromGps(lat, lon)
                }
                locationNetwork != null -> {
                    lat = locationNetwork.latitude.toString()
                    lon = locationNetwork.longitude.toString()

                    iMainView.getLatLonFromGps(lat, lon)
                }
                locationPassive != null -> {
                    lat = locationPassive.latitude.toString()
                    lon = locationPassive.longitude.toString()

                    iMainView.getLatLonFromGps(lat, lon)
                }
                else -> {
                    iMainView.onError("Can't get your location!")
                }
            }
        }
    }

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val EXCLUDE_LIST = "minutely,hourly,alerts"
        private const val API_KEY = "ab58aeed2fb4ba6951b50d4aed1143c1"
        private const val UNITS = "metric"
    }


}