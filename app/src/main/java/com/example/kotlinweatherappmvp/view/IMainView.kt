package com.example.kotlinweatherappmvp.view

import com.example.kotlinweatherappmvp.model.Weather

interface IMainView {
    fun getWeatherLatLon(weather: Weather, iconLink: String)
    fun getWeatherByCityName(lat: String, lon: String)
    fun getLatLonFromGps(lat: String, lon: String)
    fun onError(errorMessage: String)
    fun requestLocationPermissions()
}