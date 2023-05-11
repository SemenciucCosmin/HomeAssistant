package com.example.homeassistant.domain.api

import com.example.homeassistant.domain.settings.Location

data class AirPollution(
    val location: Location,
    val dateTime: Long,
    val airQualityIndex: Int,
    val carbonMonoxide: Float,
    val nitrogenMonoxide: Float,
    val nitrogenDioxide: Float,
    val ozone: Float,
    val sulphurDioxide: Float,
    val fineParticles: Float,
    val coarseParticles: Float,
    val ammonia: Float
)
