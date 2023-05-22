package com.example.homeassistant.domain

import com.example.homeassistant.domain.settings.Location

data class CurrentWeather(
    val location: Location,
    val mainWeather: String,
    val description: String,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val windSpeed: Double,
    val cloudiness: Int,
    val dateTime: Long,
    val sunriseTime: Long,
    val sunsetTime: Long,
    val cityName: String
)
