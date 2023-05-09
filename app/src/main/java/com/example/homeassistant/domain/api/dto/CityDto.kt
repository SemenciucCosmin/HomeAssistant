package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class CityDto(
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "coord")
    val locationDto: LocationDto? = null,
    @Json(name = "sunrise")
    val sunriseTime: Long? = null,
    @Json(name = "sunset")
    val sunsetTime: Long? = null
)
