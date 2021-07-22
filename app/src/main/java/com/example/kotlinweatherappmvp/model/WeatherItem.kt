package com.example.kotlinweatherappmvp.model


import com.google.gson.annotations.SerializedName

data class WeatherItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String
)