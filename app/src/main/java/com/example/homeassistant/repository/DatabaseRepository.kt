package com.example.homeassistant.repository

import com.example.homeassistant.database.HomeAssistantDatabase
import com.example.homeassistant.domain.database.AirQualityEntity

class DatabaseRepository(private val homeAssistantDatabase: HomeAssistantDatabase) {
    fun getAirQualityRecords() =
        homeAssistantDatabase.airQualityDao().getAirQualityRecords()

    fun removeUnusedRecords() =
        homeAssistantDatabase.airQualityDao().removeUnusedRecords()

    suspend fun insert(airQualityEntity: AirQualityEntity) =
        homeAssistantDatabase.airQualityDao().insert(airQualityEntity)

}
