package com.example.homeassistant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.domain.api.AirPollution
import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.api.City
import com.example.homeassistant.domain.api.CurrentWeather
import com.example.homeassistant.domain.api.FiveDaysWeather
import com.example.homeassistant.domain.api.Forecast
import com.example.homeassistant.domain.api.dto.CityDto
import com.example.homeassistant.domain.api.dto.ForecastDto
import com.example.homeassistant.domain.settings.Location
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.utils.API_ERROR_MESSAGE
import kotlinx.coroutines.launch

class WeatherApiViewModel(private val weatherApiRepository: WeatherApiRepository) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState()
    }
    val uiState: LiveData<UiState>
        get() = _uiState

    fun getCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            when (val callResult = weatherApiRepository.getCurrentWeather(latitude, longitude)) {
                is CallResult.Success -> {
                    val currentWeather = CurrentWeather(
                        location = Location(
                            latitude = callResult.data.locationDto?.latitude ?: 0.0,
                            longitude = callResult.data.locationDto?.longitude ?: 0.0
                        ),
                        mainWeather = callResult.data.weatherDto?.first()?.main ?: "",
                        description = callResult.data.weatherDto?.first()?.description ?: "",
                        temperature = callResult.data.measurementsDto?.temperature ?: 0.0,
                        feelsLike = callResult.data.measurementsDto?.feelsLike ?: 0.0,
                        maxTemperature = callResult.data.measurementsDto?.maxTemperature ?: 0.0,
                        minTemperature = callResult.data.measurementsDto?.minTemperature ?: 0.0,
                        pressure = callResult.data.measurementsDto?.pressure ?: 0,
                        humidity = callResult.data.measurementsDto?.humidity ?: 0,
                        visibility = callResult.data.visibility ?: 0,
                        windSpeed = callResult.data.windDto?.speed ?: 0.0,
                        cloudiness = callResult.data.cloudsDto?.cloudiness ?: 0,
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

    fun getFiveDaysWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            when (val callResult = weatherApiRepository.getFiveDaysWeather(latitude, longitude)) {
                is CallResult.Success -> {
                    val forecasts = getForecastsFromDto(callResult.data.forecasts)
                    val city = getCityFromDto(callResult.data.cityDto)
                    val fiveDaysWeather = FiveDaysWeather(forecasts = forecasts, city = city)
                    _uiState.value = _uiState.value?.copy(fiveDaysWeather = fiveDaysWeather)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(fiveDaysWeatherError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    fun getAirPollution(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            when (val callResult = weatherApiRepository.getAirPollution(latitude, longitude)) {
                is CallResult.Success -> {
                    val details = callResult.data.detailsDto?.first()
                    val airPollution = AirPollution(
                        location = Location(
                            latitude = callResult.data.locationDto?.latitude ?: 0.0,
                            longitude = callResult.data.locationDto?.longitude ?: 0.0
                        ),
                        dateTime = details?.dateTime ?: 0L,
                        airQualityIndex = details?.airQualityDto?.index ?: 0,
                        carbonMonoxide = details?.componentsDto?.carbonMonoxide ?: 0.0,
                        nitrogenMonoxide = details?.componentsDto?.nitrogenMonoxide ?: 0.0,
                        nitrogenDioxide = details?.componentsDto?.nitrogenDioxide ?: 0.0,
                        ozone = details?.componentsDto?.ozone ?: 0.0,
                        sulphurDioxide = details?.componentsDto?.sulphurDioxide ?: 0.0,
                        fineParticles = details?.componentsDto?.fineParticles ?: 0.0,
                        coarseParticles = details?.componentsDto?.coarseParticles ?: 0.0,
                        ammonia = details?.componentsDto?.ammonia ?: 0.0
                    )
                    _uiState.value = _uiState.value?.copy(airPollution = airPollution)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(airPollutionError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    private fun getForecastsFromDto(forecasts: List<ForecastDto>): List<Forecast> {
        return forecasts.map { forecastDto ->
            Forecast(
                dateTime = forecastDto.dateTime ?: 0L,
                temperature = forecastDto.measurementsDto?.temperature ?: 0.0,
                feelsLike = forecastDto.measurementsDto?.feelsLike ?: 0.0,
                minTemperature = forecastDto.measurementsDto?.minTemperature ?: 0.0,
                maxTemperature = forecastDto.measurementsDto?.maxTemperature ?: 0.0,
                pressure = forecastDto.measurementsDto?.pressure ?: 0,
                humidity = forecastDto.measurementsDto?.humidity ?: 0,
                mainWeather = forecastDto.weatherDto?.first()?.main ?: "",
                description = forecastDto.weatherDto?.first()?.description ?: "",
                cloudiness = forecastDto.cloudsDto?.cloudiness ?: 0,
                windSpeed = forecastDto.windDto?.speed ?: 0.0,
                visibility = forecastDto.visibility ?: 0,
                precipitation = forecastDto.precipitation ?: 0.0
            )
        }
    }

    private fun getCityFromDto(cityDto: CityDto?): City {
        return City(
            name = cityDto?.name ?: "",
            location = Location(
                latitude = cityDto?.locationDto?.latitude ?: 0.0,
                longitude = cityDto?.locationDto?.longitude ?: 0.0
            ),
            sunriseTime = cityDto?.sunriseTime ?: 0L,
            sunsetTime = cityDto?.sunsetTime ?: 0L,
        )
    }

    data class UiState(
        val currentWeather: CurrentWeather? = null,
        val currentWeatherError: String? = null,
        val fiveDaysWeather: FiveDaysWeather? = null,
        val fiveDaysWeatherError: String? = null,
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
