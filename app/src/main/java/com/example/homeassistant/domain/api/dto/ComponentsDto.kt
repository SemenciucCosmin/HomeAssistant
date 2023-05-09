package com.example.homeassistant.domain.api.dto

import com.squareup.moshi.Json

data class ComponentsDto(
    @Json(name = "co")
    val carbonMonoxide: Float? = null,
    @Json(name = "no")
    val nitrogenMonoxide: Float? = null,
    @Json(name = "no2")
    val nitrogenDioxide: Float? = null,
    @Json(name = "o3")
    val ozone: Float? = null,
    @Json(name = "so2")
    val sulphurDioxide: Float? = null,
    @Json(name = "pm2_5")
    val fineParticles: Float? = null,
    @Json(name = "pm10")
    val coarseParticles: Float? = null,
    @Json(name = "nh3")
    val ammonia: Float? = null
)
