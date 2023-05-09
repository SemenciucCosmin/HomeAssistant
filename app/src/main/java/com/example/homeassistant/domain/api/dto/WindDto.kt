package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class WindDto(
   @Json(name = "speed")
    val speed: Float? = null
)
