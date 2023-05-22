package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class AirQualityDetailsDto(
    @Json(name = "dt")
    val dateTime: Long? = null,
    @Json(name = "main")
    val airQualityIndexDto: AirQualityIndexDto? = null,
    @Json(name = "components")
    val componentsDto: ComponentsDto? = null
)
