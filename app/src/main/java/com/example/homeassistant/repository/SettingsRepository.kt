package com.example.homeassistant.repository

import android.content.Context
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.settings.Location
import com.example.homeassistant.domain.settings.Settings
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val settingsDataSource: SettingsDataSource) {
    fun getSettings(): Flow<Settings> = settingsDataSource.settingsFlow

    suspend fun saveAmPamHourFormatToPreferenceStore(amPmHourFormat: Boolean, context: Context) {
        settingsDataSource.saveAmPamHourFormatToPreferenceStore(amPmHourFormat, context)
    }

    suspend fun saveTemperatureUnitToPreferenceStore(temperatureUnit: String, context: Context) {
        settingsDataSource.saveTemperatureUnitToPreferenceStore(temperatureUnit, context)
    }

    suspend fun saveSpeedUnitToPreferenceStore(speedUnit: String, context: Context) {
        settingsDataSource.saveSpeedUnitToPreferenceStore(speedUnit, context)
    }

    suspend fun savePressureUnitToPreferenceStore(pressureUnit: String, context: Context) {
        settingsDataSource.savePressureUnitToPreferenceStore(pressureUnit, context)
    }

    suspend fun saveNotificationsToPreferenceStore(showNotifications: Boolean, context: Context) {
        settingsDataSource.saveNotificationsToPreferenceStore(showNotifications, context)
    }

    suspend fun saveLocationToPreferenceStore(location: Location, context: Context) {
        settingsDataSource.saveLocationToPreferenceStore(location, context)
    }
}
