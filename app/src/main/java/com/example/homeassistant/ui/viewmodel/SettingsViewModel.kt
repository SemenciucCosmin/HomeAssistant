package com.example.homeassistant.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.domain.settings.Location
import com.example.homeassistant.domain.settings.Settings
import com.example.homeassistant.repository.SettingsRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    fun getSettings(): LiveData<Settings> = settingsRepository.getSettings().asLiveData()

     fun saveAmPamHourFormatToPreferenceStore(amPmHourFormat: Boolean, context: Context) {
        viewModelScope.launch {
            settingsRepository.saveAmPmHourFormatToPreferenceStore(amPmHourFormat, context)
        }
    }

     fun saveTemperatureUnitToPreferenceStore(temperatureUnit: String, context: Context) {
        viewModelScope.launch {
            settingsRepository.saveTemperatureUnitToPreferenceStore(temperatureUnit, context)
        }
    }

     fun saveSpeedUnitToPreferenceStore(speedUnit: String, context: Context) {
        viewModelScope.launch {
            settingsRepository.saveSpeedUnitToPreferenceStore(speedUnit, context)
        }
    }

     fun savePressureUnitToPreferenceStore(pressureUnit: String, context: Context) {
        viewModelScope.launch {
            settingsRepository.savePressureUnitToPreferenceStore(pressureUnit, context)
        }
    }

     fun saveNotificationsToPreferenceStore(showNotifications: Boolean, context: Context) {
        viewModelScope.launch {
            settingsRepository.saveNotificationsToPreferenceStore(showNotifications, context)
        }
    }

    fun saveLocationToPreferenceStore(location: Location, context: Context) {
        viewModelScope.launch {
            settingsRepository.saveLocationToPreferenceStore(location, context)
        }
    }

    class SettingsViewModelFactory(
        private val settingsRepository: SettingsRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(settingsRepository) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}
