package com.example.kotlinweatherappmvp.view

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kotlinweatherappmvp.R
import com.example.kotlinweatherappmvp.adapter.WeatherRecViewAdapter
import com.example.kotlinweatherappmvp.databinding.ActivityMainBinding
import com.example.kotlinweatherappmvp.model.Weather
import com.example.kotlinweatherappmvp.presenter.MainPresenter

class MainActivity : AppCompatActivity(), IMainView {

    private lateinit var mainPresenter: MainPresenter

    private lateinit var weatherRecViewAdapter: WeatherRecViewAdapter

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        mainPresenter = MainPresenter(this)


        binding.btnSearch.setOnClickListener {
            if (binding.etCityname.text.toString().isEmpty()) {
                Toast.makeText(this, "This City Is Already Shown", Toast.LENGTH_SHORT).show()
            } else {
                mainPresenter.getLatLonByCityName(binding.etCityname.text.toString())

                //hide keyboard after pressing search button
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etCityname.windowToken, 0)
            }
        }

        binding.etCityname.setOnKeyListener { v, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                //hide keyboard
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etCityname.windowToken, 0)

                binding.etCityname.clearFocus()

                if (binding.etCityname.text.toString().isEmpty()) {
                    Toast.makeText(this, "That city is already shown", Toast.LENGTH_SHORT).show()
                } else {
                    mainPresenter.getLatLonByCityName(binding.etCityname.text.toString())
                }
            }
            false
        }

        requestLocationPermissions()
        mainPresenter.getLatLon(this)
    }

    override fun getWeatherLatLon(weather: Weather, iconLink: String) {
        val current = weather.current
        val weatherItem = current.weather[0]

        val description = weatherItem.description.replaceFirstChar {
            it.uppercase()
        }

        val indexOfCityName = weather.timezone.indexOf("/") + 1
        val cityName = weather.timezone.substring(indexOfCityName)
        val temperature = current.temp.toInt()

        binding.txtDescription.text = description
        binding.txtCityName.text = cityName
        binding.txtTemperature.text = temperature.toString() + "°C"

        val iconLink = "http://openweathermap.org/img/wn/$iconLink@2x.png"

        Glide.with(applicationContext)
            .load(iconLink)
            .into(binding.imgWeatherIcon)


        weatherRecViewAdapter = WeatherRecViewAdapter(weather.daily, this)
        binding.recViewWeather.adapter = weatherRecViewAdapter
        binding.recViewWeather.layoutManager =
            LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun getWeatherByCityName(lat: String, lon: String) {
        mainPresenter.getWeatherLatLon(lat, lon)
    }

    override fun onError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnGetCurrentLocation) {
            mainPresenter.getLatLon(this)
            binding.etCityname.text.clear()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mainPresenter.enableGps(this)
        }
    }

    override fun getLatLonFromGps(lat: String, lon: String) {
        mainPresenter.getWeatherLatLon(lat, lon)
    }
}