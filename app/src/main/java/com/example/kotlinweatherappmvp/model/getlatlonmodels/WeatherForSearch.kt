package com.example.kotlinweatherappmvp.model.getlatlonmodels


import com.google.gson.annotations.SerializedName

data class WeatherForSearch(
    @SerializedName("coord")
    val coord: Coord
)