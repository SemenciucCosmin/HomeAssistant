package com.example.homeassistant.ui.viewmodel.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.domain.api.AirPollution
import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.api.CurrentWeather
import com.example.homeassistant.domain.settings.Location
import com.example.homeassistant.repository.api.WeatherApiRepository
import com.example.homeassistant.utils.API_ERROR_MESSAGE
import kotlinx.coroutines.launch

class WeatherApiViewModel(private val weatherApiRepository: WeatherApiRepository) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState()
    }
    val uiState: LiveData<UiState>
        get() = _uiState

    fun getCurrentWeather(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            when (val callResult = weatherApiRepository.getCurrentWeather(latitude, longitude)) {
                is CallResult.Success -> {
                    val currentWeather = CurrentWeather(
                        location = Location(
                            latitude = callResult.data.locationDto?.latitude?.toFloat() ?: 0f,
                            longitude = callResult.data.locationDto?.longitude?.toFloat() ?: 0f
                        ),
                        mainWeather = callResult.data.weatherDto?.first()?.main ?: "",
                        description = callResult.data.weatherDto?.first()?.description ?: "",
                        temperature = callResult.data.measurementsDto?.temperature ?: 0f,
                        feelsLike = callResult.data.measurementsDto?.feelsLike ?: 0f,
                        maxTemperature = callResult.data.measurementsDto?.maxTemperature ?: 0f,
                        minTemperature = callResult.data.measurementsDto?.minTemperature ?: 0f,
                        pressure = callResult.data.measurementsDto?.pressure ?: 0,
                        humidity = callResult.data.measurementsDto?.humidity ?: 0,
                        visibility = callResult.data.visibility ?: 0,
                        windSpeed = callResult.data.wind?.speed ?: 0f,
                        cloudiness = callResult.data.clouds?.cloudiness ?: 0,
                        dateTime = callResult.data.dateTime ?: 0L,
                        sunriseTime = callResult.data.weatherInfoDto?.sunriseTime ?: 0L,
                        sunsetTime = callResult.data.weatherInfoDto?.sunsetTime ?: 0L,
                        cityName = callResult.data.cityName ?: ""
                    )
                    _uiState.value = _uiState.value?.copy(currentWeather = currentWeather)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(currentWeatherError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    fun getAirPollution(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            when (val callResult = weatherApiRepository.getAirPollution(latitude, longitude)) {
                is CallResult.Success -> {
                    val details = callResult.data.detailsDto?.first()
                    val airPollution = AirPollution(
                        location = Location(
                            latitude = callResult.data.locationDto?.latitude?.toFloat() ?: 0f,
                            longitude = callResult.data.locationDto?.longitude?.toFloat() ?: 0f
                        ),
                        dateTime = details?.dateTime ?: 0L,
                        carbonMonoxide = details?.componentsDto?.carbonMonoxide ?: 0f,
                        nitrogenMonoxide = details?.componentsDto?.nitrogenMonoxide ?: 0f,
                        nitrogenDioxide = details?.componentsDto?.nitrogenDioxide ?: 0f,
                        ozone = details?.componentsDto?.ozone ?: 0f,
                        sulphurDioxide = details?.componentsDto?.sulphurDioxide ?: 0f,
                        fineParticles = details?.componentsDto?.fineParticles ?: 0f,
                        coarseParticles = details?.componentsDto?.coarseParticles ?: 0f,
                        ammonia = details?.componentsDto?.ammonia ?: 0f
                    )
                    _uiState.value = _uiState.value?.copy(airPollution = airPollution)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(airPollutionError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    data class UiState(
        val currentWeather: CurrentWeather? = null,
        val currentWeatherError: String? = null,
        val airPollution: AirPollution? = null,
        val airPollutionError: String? = null
    )

    class WeatherApiViewModelFactory(
        private val weatherApiRepository: WeatherApiRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WeatherApiViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WeatherApiViewModel(weatherApiRepository) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}
