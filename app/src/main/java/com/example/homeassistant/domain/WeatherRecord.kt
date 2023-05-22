package com.example.homeassistant.domain

import com.example.homeassistant.R

data class WeatherRecord(
    val weather: CurrentWeather,
    val temperatureValueStringId: Int = R.string.lbl_unit_value_celsius,
    val pressureValueStringId: Int = R.string.lbl_unit_value_hpa,
    val speedValueStringId: Int = R.string.lbl_unit_value_meters_per_second,
    val hourFormatStringId: Int = R.string.lbl_hour_format_am_pm
)
