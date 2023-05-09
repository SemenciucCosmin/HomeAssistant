package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class MeasurementsDto(
   @Json(name = "temp")
    val temperature: Float? = null,
   @Json(name = "feels_like")
    val feelsLike: Float? = null,
   @Json(name = "temp_min")
    val minTemperature: Float? = null,
   @Json(name = "temp_max")
    val maxTemperature: Float? = null,
   @Json(name = "pressure")
    val pressure: Int? = null,
   @Json(name = "humidity")
    val humidity: Int? = null
)
