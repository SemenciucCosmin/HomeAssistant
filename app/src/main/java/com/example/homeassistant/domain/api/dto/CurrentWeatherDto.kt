package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class CurrentWeatherDto(
    @Json(name = "coord")
    val locationDto: LocationDto? = null,
    @Json(name = "weather")
    val weatherDto: List<WeatherDto>? = null,
    @Json(name = "main")
    val measurementsDto: MeasurementsDto? = null,
    @Json(name = "visibility")
    val visibility: Int? = null,
    @Json(name = "wind")
    val windDto: WindDto? = null,
    @Json(name = "clouds")
    val cloudsDto: CloudsDto? = null,
    @Json(name = "dt")
    val dateTime: Long? = null,
    @Json(name = "sys")
    val weatherInfoDto: WeatherInfoDto? = null,
    @Json(name = "name")
    val cityName: String? = null
)
