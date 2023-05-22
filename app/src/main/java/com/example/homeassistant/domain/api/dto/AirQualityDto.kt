package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class AirQualityDto(
    @Json(name = "coord")
    val locationDto: LocationDto? = null,
    @Json(name = "list")
    val detailsDto: List<AirQualityDetailsDto>? = null
)
