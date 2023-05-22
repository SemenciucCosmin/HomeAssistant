package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class AirQualityIndexDto(
    @Json(name = "aqi")
    val index: Int? = null,
)
