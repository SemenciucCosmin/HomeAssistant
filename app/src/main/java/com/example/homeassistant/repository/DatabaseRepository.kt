package com.example.homeassistant.repository

import com.example.homeassistant.database.HomeAssistantDatabase
import com.example.homeassistant.domain.database.AirQualityEntity
import com.example.homeassistant.domain.database.CurrentWeatherEntity

class DatabaseRepository(private val homeAssistantDatabase: HomeAssistantDatabase) {
    fun getCurrentWeatherRecords() =
        homeAssistantDatabase.currentWeatherDao().getCurrentWeatherRecords()

    suspend fun insertCurrentWeatherEntity(currentWeatherEntity: CurrentWeatherEntity) =
        homeAssistantDatabase.currentWeatherDao().insert(currentWeatherEntity)

    fun getAirQualityRecords() =
        homeAssistantDatabase.airQualityDao().getAirQualityRecords()

    suspend fun insertAirQualityEntity(airQualityEntity: AirQualityEntity) =
        homeAssistantDatabase.airQualityDao().insert(airQualityEntity)

}
