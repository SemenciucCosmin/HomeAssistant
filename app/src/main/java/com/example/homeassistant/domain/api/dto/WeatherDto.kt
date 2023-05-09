package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class WeatherDto(
   @Json(name = "main")
    val main: String? = null,
   @Json(name = "description")
    val description: String? = null
)
