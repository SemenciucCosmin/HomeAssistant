package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class FiveDaysWeatherDto(
    @Json(name = "list")
    val forecasts: List<ForecastDto>,
    @Json(name = "city")
    val cityDto: CityDto? = null
)
