package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class AirPollutionDto(
    @Json(name = "coord")
    val locationDto: LocationDto? = null,
    @Json(name = "list")
    val detailsDto: List<AirPollutionDetailsDto>? = null
)
