package com.example.homeassistant.domain.api

import com.example.homeassistant.domain.settings.Location

data class City(
    val name: String,
    val location: Location,
    val sunriseTime: Long,
    val sunsetTime: Long
)
