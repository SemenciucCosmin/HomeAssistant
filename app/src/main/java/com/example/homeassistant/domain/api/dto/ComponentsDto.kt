package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class ComponentsDto(
    @Json(name = "co")
    val carbonMonoxide: Double? = null,
    @Json(name = "no")
    val nitrogenMonoxide: Double? = null,
    @Json(name = "no2")
    val nitrogenDioxide: Double? = null,
    @Json(name = "o3")
    val ozone: Double? = null,
    @Json(name = "so2")
    val sulphurDioxide: Double? = null,
    @Json(name = "pm2_5")
    val fineParticles: Double? = null,
    @Json(name = "pm10")
    val coarseParticles: Double? = null,
    @Json(name = "nh3")
    val ammonia: Double? = null
)
