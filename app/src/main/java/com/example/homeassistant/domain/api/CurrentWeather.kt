package com.example.homeassistant.domain.api

import com.example.homeassistant.domain.settings.Location

data class CurrentWeather(
    val location: Location,
    val mainWeather: String,
    val description: String,
    val temperature: Float,
    val feelsLike: Float,
    val minTemperature: Float,
    val maxTemperature: Float,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val windSpeed: Float,
    val cloudiness: Int,
    val dateTime: Long,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val cityName: String
)
