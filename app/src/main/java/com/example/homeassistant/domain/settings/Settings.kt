package com.example.homeassistant.domain.settings

data class Settings(
    val amPmHourFormat: Boolean,
    val temperatureUnit: String,
    val speedUnit: String,
    val pressureUnit: String,
    val showNotifications: Boolean,
    val location: Location
)
