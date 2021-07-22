package com.example.kotlinweatherappmvp.service

import com.example.kotlinweatherappmvp.model.Weather
import com.example.kotlinweatherappmvp.model.getlatlonmodels.WeatherForSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("onecall")
    fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") excludeList: String,
        @Query("appid") appId: String,
        @Query("units") units: String
    ): Call<Weather>

    @GET("weather")
    fun getCityLatLonByName(
        @Query("q") cityName: String,
        @Query("appid") appId: String
    ): Call<WeatherForSearch>


}