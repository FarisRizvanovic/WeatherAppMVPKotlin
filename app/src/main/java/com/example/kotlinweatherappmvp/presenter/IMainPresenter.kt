package com.example.kotlinweatherappmvp.presenter

import android.content.Context

interface IMainPresenter {
    fun getWeatherLatLon(lat: String, lon: String)
    fun getLatLonByCityName(cityName: String)
    fun enableGps(context: Context)
    fun getLatLon(context: Context)
}