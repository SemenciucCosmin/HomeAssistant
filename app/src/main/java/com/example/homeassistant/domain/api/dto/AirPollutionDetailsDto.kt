package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class AirPollutionDetailsDto(
    @Json(name = "dt")
    val dateTime: Long? = null,
    @Json(name = "components")
    val componentsDto: ComponentsDto? = null
)
