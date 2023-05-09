package com.example.homeassistant.domain.api

data class Forecast(
    val dateTime: Long,
    val temperature: Float,
    val feelsLike: Float,
    val minTemperature: Float,
    val maxTemperature: Float,
    val pressure: Int,
    val humidity: Int,
    val mainWeather: String,
    val description: String,
    val cloudiness: Int,
    val windSpeed: Float,
    val visibility: Int,
    val precipitation: Float
)
