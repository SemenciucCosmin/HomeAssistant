package com.example.homeassistant.domain.api

import com.example.homeassistant.domain.settings.Location

data class AirPollution(
    val location: Location,
    val dateTime: Long,
    val airQualityIndex: Int,
    val carbonMonoxide: Double,
    val nitrogenMonoxide: Double,
    val nitrogenDioxide: Double,
    val ozone: Double,
    val sulphurDioxide: Double,
    val fineParticles: Double,
    val coarseParticles: Double,
    val ammonia: Double
)
