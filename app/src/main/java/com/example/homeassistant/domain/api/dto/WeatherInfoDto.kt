package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class WeatherInfoDto(
   @Json(name = "sunrise")
    val sunriseTime: Long? = null,
   @Json(name = "sunset")
    val sunsetTime: Long? = null
)
