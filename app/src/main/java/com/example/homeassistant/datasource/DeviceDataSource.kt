package com.example.homeassistant.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.homeassistant.datasource.DeviceDataSource.Companion.NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = NAME)

class DeviceDataSource(context: Context) {
    companion object {
        const val NAME = "DEVICE_PREFERENCES"
        private const val NO_ADDRESS = ""
        private const val DEVICE_ADDRESS = "device_address"
    }

    private val deviceAddress = stringPreferencesKey(DEVICE_ADDRESS)

    val deviceAddressFlow: Flow<String> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[this.deviceAddress] ?: NO_ADDRESS
        }

    suspend fun saveLocationToPreferenceStore(deviceAddress: String, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[this.deviceAddress] = deviceAddress
        }
    }
}