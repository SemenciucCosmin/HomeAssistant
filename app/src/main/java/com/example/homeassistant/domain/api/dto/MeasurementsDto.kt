package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class MeasurementsDto(
   @Json(name = "temp")
    val temperature: Double? = null,
   @Json(name = "feels_like")
    val feelsLike: Double? = null,
   @Json(name = "temp_min")
    val minTemperature: Double? = null,
   @Json(name = "temp_max")
    val maxTemperature: Double? = null,
   @Json(name = "pressure")
    val pressure: Int? = null,
   @Json(name = "humidity")
    val humidity: Int? = null
)
