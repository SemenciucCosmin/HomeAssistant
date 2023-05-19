package com.example.homeassistant.domain.api

import com.example.homeassistant.domain.AirQualityEnum
import com.example.homeassistant.domain.AmmoniaEnum
import com.example.homeassistant.domain.CarbonMonoxideEnum
import com.example.homeassistant.domain.CoarseParticlesEnum
import com.example.homeassistant.domain.FineParticlesEnum
import com.example.homeassistant.domain.NitrogenDioxideEnum
import com.example.homeassistant.domain.NitrogenMonoxideEnum
import com.example.homeassistant.domain.OzoneEnum
import com.example.homeassistant.domain.SulphurDioxideEnum
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
    val ammonia: Double,
    val fineParticles: Double,
    val coarseParticles: Double,
    val airQualityIndexEnum: AirQualityEnum,
    val carbonMonoxideEnum: CarbonMonoxideEnum,
    val nitrogenMonoxideEnum: NitrogenMonoxideEnum,
    val nitrogenDioxideEnum: NitrogenDioxideEnum,
    val ozoneEnum: OzoneEnum,
    val sulphurDioxideEnum: SulphurDioxideEnum,
    val ammoniaEnum: AmmoniaEnum,
    val fineParticlesEnum: FineParticlesEnum,
    val coarseParticlesEnum: CoarseParticlesEnum
)
