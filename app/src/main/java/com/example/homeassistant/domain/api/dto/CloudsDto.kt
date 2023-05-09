package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class CloudsDto(
   @Json(name = "all")
    val cloudiness: Int? = null
)
