package com.example.homeassistant.domain

import com.example.homeassistant.domain.enums.AirQualityEnum
import com.example.homeassistant.domain.enums.AmmoniaEnum
import com.example.homeassistant.domain.enums.CarbonMonoxideEnum
import com.example.homeassistant.domain.enums.CoarseParticlesEnum
import com.example.homeassistant.domain.enums.FineParticlesEnum
import com.example.homeassistant.domain.enums.NitrogenDioxideEnum
import com.example.homeassistant.domain.enums.NitrogenMonoxideEnum
import com.example.homeassistant.domain.enums.OzoneEnum
import com.example.homeassistant.domain.enums.SulphurDioxideEnum
import com.example.homeassistant.domain.settings.Location

data class AirPollution(
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
