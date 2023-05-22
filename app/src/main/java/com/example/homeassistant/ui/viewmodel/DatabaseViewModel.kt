package com.example.homeassistant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.R
import com.example.homeassistant.domain.AirQuality
import com.example.homeassistant.domain.CurrentWeather
import com.example.homeassistant.domain.WeatherRecord
import com.example.homeassistant.domain.database.CurrentWeatherEntity
import com.example.homeassistant.domain.enums.AirQualityEnum
import com.example.homeassistant.domain.enums.AmmoniaEnum
import com.example.homeassistant.domain.enums.CarbonMonoxideEnum
import com.example.homeassistant.domain.enums.CoarseParticlesEnum
import com.example.homeassistant.domain.enums.FineParticlesEnum
import com.example.homeassistant.domain.enums.NitrogenDioxideEnum
import com.example.homeassistant.domain.enums.NitrogenMonoxideEnum
import com.example.homeassistant.domain.enums.OzoneEnum
import com.example.homeassistant.domain.enums.SulphurDioxideEnum
import com.example.homeassistant.domain.settings.PressureType
import com.example.homeassistant.domain.settings.Settings
import com.example.homeassistant.domain.settings.SpeedType
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.DatabaseRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DatabaseViewModel(private val databaseRepository: DatabaseRepository) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState()
    }
    val uiState: LiveData<UiState>
        get() = _uiState

    fun getWeatherRecords(settings: Settings) {
        viewModelScope.launch {
            databaseRepository.getCurrentWeatherRecords().collect { records ->
                val weatherRecords = records.map { weatherEntity ->
                    getWeatherRecordFromEntity(weatherEntity, settings)
                }
                _uiState.value = _uiState.value?.copy(weatherRecords = weatherRecords)
            }
        }
    }

    fun getAirQualityRecords(): LiveData<List<AirQuality>> {
        val records = databaseRepository.getAirQualityRecords()
        return records.map {
            it.map { airQualityEntity ->
                AirQuality(
                    dateTime = airQualityEntity.dateTime,
                    airQualityIndex = airQualityEntity.airQualityIndex,
                    carbonMonoxide = airQualityEntity.carbonMonoxide,
                    nitrogenMonoxide = airQualityEntity.nitrogenMonoxide,
                    nitrogenDioxide = airQualityEntity.nitrogenDioxide,
                    ozone = airQualityEntity.ozone,
                    sulphurDioxide = airQualityEntity.sulphurDioxide,
                    ammonia = airQualityEntity.ammonia,
                    fineParticles = airQualityEntity.fineParticles,
                    coarseParticles = airQualityEntity.coarseParticles,
                    airQualityIndexEnum = AirQualityEnum.getQualityByValue(airQualityEntity.airQualityIndex),
                    carbonMonoxideEnum = CarbonMonoxideEnum.getQualityByValue(airQualityEntity.carbonMonoxide),
                    nitrogenMonoxideEnum = NitrogenMonoxideEnum.getQualityByValue(airQualityEntity.nitrogenMonoxide),
                    nitrogenDioxideEnum = NitrogenDioxideEnum.getQualityByValue(airQualityEntity.nitrogenDioxide),
                    ozoneEnum = OzoneEnum.getQualityByValue(airQualityEntity.ozone),
                    sulphurDioxideEnum = SulphurDioxideEnum.getQualityByValue(airQualityEntity.sulphurDioxide),
                    ammoniaEnum = AmmoniaEnum.getQualityByValue(airQualityEntity.ammonia),
                    fineParticlesEnum = FineParticlesEnum.getQualityByValue(airQualityEntity.fineParticles),
                    coarseParticlesEnum = CoarseParticlesEnum.getQualityByValue(airQualityEntity.coarseParticles)
                )
            }
        }.asLiveData()
    }

    private fun getWeatherRecordFromEntity(
        weatherEntity: CurrentWeatherEntity,
        settings: Settings
    ): WeatherRecord {
        val rawTemperature = weatherEntity.temperature
        val rawFeelsLike = weatherEntity.feelsLike
        val rawMinTemperature = weatherEntity.minTemperature
        val rawMaxTemperature = weatherEntity.maxTemperature
        val rawPressure = weatherEntity.pressure
        val rawWindSpeed = weatherEntity.windSpeed

        val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
        val pressureType = PressureType.getByItemType(settings.pressureUnit)
        val speedType = SpeedType.getByItemType(settings.speedUnit)
        val hourFormatStringId = if (settings.amPmHourFormat) {
            R.string.lbl_hour_format_am_pm
        } else {
            R.string.lbl_hour_format
        }

        val temperature = TemperatureType.getTempByType(rawTemperature, temperatureType)
        val feelsLike = TemperatureType.getTempByType(rawFeelsLike, temperatureType)
        val minTemperature = TemperatureType.getTempByType(rawMinTemperature, temperatureType)
        val maxTemperature = TemperatureType.getTempByType(rawMaxTemperature, temperatureType)
        val pressure = PressureType.getPressureByType(rawPressure, pressureType)
        val windSpeed = SpeedType.getSpeedByType(rawWindSpeed, speedType)

        return WeatherRecord(
            weather = CurrentWeather(
                mainWeather = weatherEntity.mainWeather,
                description = weatherEntity.description,
                temperature = temperature,
                feelsLike = feelsLike,
                maxTemperature = maxTemperature,
                minTemperature = minTemperature,
                pressure = pressure,
                humidity = weatherEntity.humidity,
                visibility = weatherEntity.visibility,
                windSpeed = windSpeed,
                cloudiness = weatherEntity.cloudiness,
                dateTime = weatherEntity.dateTime,
                sunriseTime = weatherEntity.sunriseTime,
                sunsetTime = weatherEntity.sunsetTime,
                cityName = weatherEntity.cityName
            ),
            temperatureValueStringId = temperatureType.valueStringId,
            pressureValueStringId = pressureType.valueStringId,
            speedValueStringId = speedType.valueStringId,
            hourFormatStringId = hourFormatStringId
        )
    }

    data class UiState(val weatherRecords: List<WeatherRecord>? = null)

    class DatabaseViewModelFactory(
        private val databaseRepository: DatabaseRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DatabaseViewModel(databaseRepository) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}
