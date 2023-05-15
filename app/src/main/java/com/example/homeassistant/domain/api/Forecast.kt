package com.example.homeassistant.domain.api

data class Forecast(
    val dateTime: Long,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val mainWeather: String,
    val description: String,
    val cloudiness: Int,
    val windSpeed: Double,
    val visibility: Int,
    val precipitation: Double
)
