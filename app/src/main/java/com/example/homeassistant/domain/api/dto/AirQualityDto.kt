package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class AirQualityDto(
    @Json(name = "aqi")
    val index: Int? = null,
)
