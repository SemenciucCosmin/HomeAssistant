package com.example.homeassistant.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.homeassistant.domain.settings.Location
import com.example.homeassistant.domain.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SettingsDataSource.NAME)
class SettingsDataSource(context: Context) {
    companion object {
        const val NAME = "SETTINGS_PREFERENCES"
        const val DEFAULT_TEMPERATURE_UNIT = "CELSIUS"
        const val DEFAULT_SPEED_UNIT = "M/S"
        const val DEFAULT_PRESSURE_UNIT = "HPA"
        const val DEFAULT_LATITUDE = 0.0
        const val DEFAULT_LONGITUDE = 0.0
    }

    private val amPmHourFormat = booleanPreferencesKey("hour_format")
    private val temperatureUnit = stringPreferencesKey("temperature_unit")
    private val speedUnit = stringPreferencesKey("speed_unit")
    private val pressureUnit = stringPreferencesKey("pressure_unit")
    private val showNotifications = booleanPreferencesKey("show_notifications")
    private val latitude = doublePreferencesKey("latitude")
    private val longitude = doublePreferencesKey("longitude")
    val settingsFlow: Flow<Settings> = context.dataStore.data.catch {
        if (it is IOException) {
            it.printStackTrace()
            emit(emptyPreferences())
        } else {
            throw it
        }
    }.map { preferences ->
        val amPmHourFormat = preferences[amPmHourFormat] ?: true
        val temperatureUnit = preferences[temperatureUnit] ?: DEFAULT_TEMPERATURE_UNIT
        val speedUnit = preferences[speedUnit] ?: DEFAULT_SPEED_UNIT
        val pressureUnit = preferences[pressureUnit] ?: DEFAULT_PRESSURE_UNIT
        val showNotifications = preferences[showNotifications] ?: true
        val latitude = preferences[latitude] ?: DEFAULT_LATITUDE
        val longitude = preferences[longitude] ?: DEFAULT_LONGITUDE

        Settings(
            amPmHourFormat = amPmHourFormat,
            temperatureUnit = temperatureUnit,
            speedUnit = speedUnit,
            pressureUnit = pressureUnit,
            showNotifications = showNotifications,
            location = Location(latitude, longitude)
        )
    }

    suspend fun saveAmPamHourFormatToPreferenceStore(amPmHourFormat: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.amPmHourFormat] = amPmHourFormat
        }
    }

    suspend fun saveTemperatureUnitToPreferenceStore(temperatureUnit: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.temperatureUnit] = temperatureUnit
        }
    }

    suspend fun saveSpeedUnitToPreferenceStore(speedUnit: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.speedUnit] = speedUnit
        }
    }

    suspend fun savePressureUnitToPreferenceStore(pressureUnit: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.pressureUnit] = pressureUnit
        }
    }

    suspend fun saveNotificationsToPreferenceStore(showNotifications: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.showNotifications] = showNotifications
        }
    }

    suspend fun saveLocationToPreferenceStore(location: Location, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.latitude] = location.latitude
            preferences[this.longitude] = location.longitude
        }
    }
}