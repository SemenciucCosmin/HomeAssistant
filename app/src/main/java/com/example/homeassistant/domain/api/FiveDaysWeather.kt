package com.example.homeassistant.domain.api

data class FiveDaysWeather(
    val forecasts: List<Forecast>,
    val city: City
)
