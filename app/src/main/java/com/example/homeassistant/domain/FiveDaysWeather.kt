package com.example.homeassistant.domain

data class FiveDaysWeather(
    val forecasts: List<Forecast>,
    val city: City
)
