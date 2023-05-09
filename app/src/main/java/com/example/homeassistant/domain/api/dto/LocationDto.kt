package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class LocationDto (
   @Json(name = "lon")
    val longitude: Float? = null,
   @Json(name = "lat")
    val latitude: Float? = null
)
