package com.example.kotlinweatherappmvp.fragment

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.kotlinweatherappmvp.R
import com.example.kotlinweatherappmvp.adapter.WeatherRecViewAdapter
import com.example.kotlinweatherappmvp.databinding.FragmentMainBinding
import com.example.kotlinweatherappmvp.model.Weather
import com.example.kotlinweatherappmvp.presenter.MainPresenter
import com.example.kotlinweatherappmvp.view.IMainView


class MainFragment : Fragment(), IMainView {

    private lateinit var binding: FragmentMainBinding

    private lateinit var mainPresenter: MainPresenter

    private lateinit var weatherRecViewAdapter: WeatherRecViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        mainPresenter = MainPresenter(this)

        /**
         * Handles the Search button click
         */
        binding.btnSearch.setOnClickListener {
            if (binding.etCityName.text.toString().isEmpty()) {
                Toast.makeText(context, "This City Is Already Shown", Toast.LENGTH_SHORT).show()
            } else {
                mainPresenter.getLatLonByCityName(binding.etCityName.text.toString())

                //hide keyboard after pressing search button
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etCityName.windowToken, 0)
            }
        }

        /**
         * Handles the user pressing the Enter key
         */
        binding.etCityName.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etCityName.windowToken, 0)

                binding.etCityName.clearFocus()

                if (binding.etCityName.text.toString().isEmpty()) {
                    Toast.makeText(context, "That city is already shown", Toast.LENGTH_SHORT).show()
                } else {
                    mainPresenter.getLatLonByCityName(binding.etCityName.text.toString())
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        requestLocationPermissions()
        context?.let { mainPresenter.getLatLon(it) }

        return binding.root
    }

    /**
     * Sets the data for the views
     */
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

        Glide.with(requireContext())
            .load(iconLink)
            .into(binding.imgWeatherIcon)


        weatherRecViewAdapter = context?.let { WeatherRecViewAdapter(weather.daily, it) }!!
        binding.recViewWeather.adapter = weatherRecViewAdapter
        binding.recViewWeather.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun getWeatherByCityName(lat: String, lon: String) {
        mainPresenter.getWeatherLatLon(lat, lon)
    }

    override fun onError(errorMessage: String) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_activity_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnGetCurrentLocation) {
            context?.let { mainPresenter.getLatLon(it) }
            binding.etCityName.text.clear()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Requests Location Permissions from the user and checks if the GPS is enabled
     */
    override fun requestLocationPermissions() {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            context?.let { mainPresenter.enableGps(it) }
        }
    }

    override fun getLatLonFromGps(lat: String, lon: String) {
        mainPresenter.getWeatherLatLon(lat, lon)
    }


}