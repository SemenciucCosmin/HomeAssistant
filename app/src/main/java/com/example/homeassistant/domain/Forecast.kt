package com.example.homeassistant.domain

import com.example.homeassistant.R

data class Forecast(
    val dateTime: Long,
    val temperature: Double,
    val feelsLike: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val mainWeather: String,
    val description: String,
    val cloudiness: Int,
    val windSpeed: Double,
    val visibility: Int,
    val precipitation: Double,
    val temperatureValueStringId: Int = R.string.lbl_unit_value_celsius,
    val pressureValueStringId: Int = R.string.lbl_unit_value_hpa,
    val speedValueStringId: Int = R.string.lbl_unit_value_meters_per_second,
    val hourFormatStringId: Int = R.string.lbl_hour_format_am_pm,
    val isExpanded: Boolean = false
)
