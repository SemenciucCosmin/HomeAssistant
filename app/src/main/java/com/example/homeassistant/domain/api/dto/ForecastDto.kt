package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class ForecastDto(
    @Json(name = "dt")
    val dateTime: Long? = null,
    @Json(name = "main")
    val measurementsDto: MeasurementsDto? = null,
    @Json(name = "weather")
    val weatherDto: List<WeatherDto>? = null,
    @Json(name = "clouds")
    val cloudsDto: CloudsDto? = null,
    @Json(name = "wind")
    val windDto: WindDto? = null,
    @Json(name = "visibility")
    val visibility: Int? = null,
    @Json(name = "pop")
    val precipitation: Double? = null
)
