package com.example.homeassistant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.homeassistant.R
import com.example.homeassistant.domain.enums.AirQualityEnum
import com.example.homeassistant.domain.enums.AmmoniaEnum
import com.example.homeassistant.domain.enums.CarbonMonoxideEnum
import com.example.homeassistant.domain.enums.CoarseParticlesEnum
import com.example.homeassistant.domain.enums.FineParticlesEnum
import com.example.homeassistant.domain.enums.NitrogenDioxideEnum
import com.example.homeassistant.domain.enums.NitrogenMonoxideEnum
import com.example.homeassistant.domain.enums.OzoneEnum
import com.example.homeassistant.domain.enums.SulphurDioxideEnum
import com.example.homeassistant.domain.AirQuality
import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.City
import com.example.homeassistant.domain.CurrentWeather
import com.example.homeassistant.domain.FiveDaysWeather
import com.example.homeassistant.domain.Forecast
import com.example.homeassistant.domain.api.dto.AirQualityDto
import com.example.homeassistant.domain.api.dto.AirQualityIndexDto
import com.example.homeassistant.domain.api.dto.CityDto
import com.example.homeassistant.domain.api.dto.CurrentWeatherDto
import com.example.homeassistant.domain.api.dto.ForecastDto
import com.example.homeassistant.domain.settings.Location
import com.example.homeassistant.domain.settings.PressureType
import com.example.homeassistant.domain.settings.Settings
import com.example.homeassistant.domain.settings.SpeedType
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.utils.API_ERROR_MESSAGE
import kotlinx.coroutines.launch

class WeatherApiViewModel(private val weatherApiRepository: WeatherApiRepository) : ViewModel() {
    private val _uiState = MutableLiveData<UiState>().apply {
        value = UiState()
    }
    val uiState: LiveData<UiState>
        get() = _uiState

    fun getCurrentWeather(settings: Settings) {
        viewModelScope.launch {
            val callResult = weatherApiRepository.getCurrentWeather(
                settings.location.latitude,
                settings.location.longitude
            )

            when (callResult) {
                is CallResult.Success -> {
                    val currentWeather = getCurrentWeatherFromDto(callResult.data, settings)
                    _uiState.value = _uiState.value?.copy(currentWeather = currentWeather)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(currentWeatherError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    fun getFiveDaysWeather(settings: Settings) {
        viewModelScope.launch {
            val callResult = weatherApiRepository.getFiveDaysWeather(
                settings.location.latitude,
                settings.location.longitude
            )
            when (callResult) {
                is CallResult.Success -> {
                    val forecasts = callResult.data.forecasts.mapNotNull { forecastDto ->
                        getForecastFromDto(forecastDto, settings)
                    }
                    val city = getCityFromDto(callResult.data.cityDto) ?: return@launch
                    val fiveDaysWeather = FiveDaysWeather(forecasts = forecasts, city = city)
                    _uiState.value = _uiState.value?.copy(fiveDaysWeather = fiveDaysWeather)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(fiveDaysWeatherError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    fun getAirQuality(settings: Settings) {
        viewModelScope.launch {
            val callResult = weatherApiRepository.getAirQuality(
                settings.location.latitude,
                settings.location.longitude
            )

            when (callResult) {
                is CallResult.Success -> {
                    val airQuality = getAirQualityFromDto(callResult.data)
                    _uiState.value = _uiState.value?.copy(airQuality = airQuality)
                }

                is CallResult.Error -> {
                    _uiState.value = _uiState.value?.copy(airQualityError = API_ERROR_MESSAGE)
                }
            }
        }
    }

    private fun getCurrentWeatherFromDto(
        currentWeatherDto: CurrentWeatherDto,
        settings: Settings
    ): CurrentWeather? {
        val latitude = currentWeatherDto.locationDto?.latitude ?: return null
        val longitude = currentWeatherDto.locationDto.longitude ?: return null
        val mainWeather = currentWeatherDto.weatherDto?.first()?.main ?: return null
        val description = currentWeatherDto.weatherDto.first().description ?: return null
        val rawTemperature = currentWeatherDto.measurementsDto?.temperature ?: return null
        val rawFeelsLike = currentWeatherDto.measurementsDto.feelsLike ?: return null
        val rawMinTemperature = currentWeatherDto.measurementsDto.minTemperature ?: return null
        val rawMaxTemperature = currentWeatherDto.measurementsDto.maxTemperature ?: return null
        val rawPressure = currentWeatherDto.measurementsDto.pressure ?: return null
        val humidity = currentWeatherDto.measurementsDto.humidity ?: return null
        val visibility = currentWeatherDto.visibility ?: return null
        val rawWindSpeed = currentWeatherDto.windDto?.speed ?: return null
        val cloudiness = currentWeatherDto.cloudsDto?.cloudiness ?: return null
        val dateTime = currentWeatherDto.dateTime ?: return null
        val sunriseTime = currentWeatherDto.weatherInfoDto?.sunriseTime ?: return null
        val sunsetTime = currentWeatherDto.weatherInfoDto.sunsetTime ?: return null
        val cityName = currentWeatherDto.cityName ?: return null

        val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
        val pressureType = PressureType.getByItemType(settings.pressureUnit)
        val speedType = SpeedType.getByItemType(settings.speedUnit)

        val temperature = TemperatureType.getTempByType(rawTemperature, temperatureType)
        val feelsLike = TemperatureType.getTempByType(rawFeelsLike, temperatureType)
        val minTemperature = TemperatureType.getTempByType(rawMinTemperature, temperatureType)
        val maxTemperature = TemperatureType.getTempByType(rawMaxTemperature, temperatureType)
        val pressure = PressureType.getPressureByType(rawPressure, pressureType)
        val windSpeed = SpeedType.getSpeedByType(rawWindSpeed, speedType)

        return CurrentWeather(
            location = Location(latitude, longitude),
            mainWeather = mainWeather,
            description = description,
            temperature = temperature,
            feelsLike = feelsLike,
            maxTemperature = maxTemperature,
            minTemperature = minTemperature,
            pressure = pressure,
            humidity = humidity,
            visibility = visibility,
            windSpeed = windSpeed,
            cloudiness = cloudiness,
            dateTime = dateTime,
            sunriseTime = sunriseTime,
            sunsetTime = sunsetTime,
            cityName = cityName
        )
    }

    private fun getForecastFromDto(forecastDto: ForecastDto, settings: Settings): Forecast? {
        val dateTime = forecastDto.dateTime ?: return null
        val rawTemperature = forecastDto.measurementsDto?.temperature ?: return null
        val rawFeelsLike = forecastDto.measurementsDto.feelsLike ?: return null
        val rawMinTemperature = forecastDto.measurementsDto.minTemperature ?: return null
        val rawMaxTemperature = forecastDto.measurementsDto.maxTemperature ?: return null
        val rawPressure = forecastDto.measurementsDto.pressure ?: return null
        val humidity = forecastDto.measurementsDto.humidity ?: return null
        val mainWeather = forecastDto.weatherDto?.first()?.main ?: return null
        val description = forecastDto.weatherDto.first().description ?: return null
        val cloudiness = forecastDto.cloudsDto?.cloudiness ?: return null
        val rawWindSpeed = forecastDto.windDto?.speed ?: return null
        val visibility = forecastDto.visibility ?: return null
        val precipitation = forecastDto.precipitation ?: return null

        val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
        val pressureType = PressureType.getByItemType(settings.pressureUnit)
        val speedType = SpeedType.getByItemType(settings.speedUnit)

        val temperature = TemperatureType.getTempByType(rawTemperature, temperatureType)
        val feelsLike = TemperatureType.getTempByType(rawFeelsLike, temperatureType)
        val minTemperature = TemperatureType.getTempByType(rawMinTemperature, temperatureType)
        val maxTemperature = TemperatureType.getTempByType(rawMaxTemperature, temperatureType)
        val pressure = PressureType.getPressureByType(rawPressure, pressureType)
        val windSpeed = SpeedType.getSpeedByType(rawWindSpeed, speedType)

        return Forecast(
            dateTime = dateTime,
            temperature = temperature,
            feelsLike = feelsLike,
            minTemperature = minTemperature,
            maxTemperature = maxTemperature,
            pressure = pressure,
            humidity = humidity,
            mainWeather = mainWeather,
            description = description,
            cloudiness = cloudiness,
            windSpeed = windSpeed,
            visibility = visibility,
            precipitation = precipitation
        )
    }

    private fun getCityFromDto(cityDto: CityDto?): City? {
        return City(
            name = cityDto?.name ?: return null,
            location = Location(
                latitude = cityDto.locationDto?.latitude ?: return null,
                longitude = cityDto.locationDto.longitude ?: return null
            ),
            sunriseTime = cityDto.sunriseTime ?: return null,
            sunsetTime = cityDto.sunsetTime ?: return null,
        )
    }

    private fun getAirQualityFromDto(airQualityDto: AirQualityDto): AirQuality? {
        val dateTime = airQualityDto.detailsDto?.first()?.dateTime ?: return null
        val details = airQualityDto.detailsDto.first()
        val airQualityIndex = details.airQualityIndexDto?.index ?: return null
        val carbonMonoxide = details.componentsDto?.carbonMonoxide ?: return null
        val nitrogenMonoxide = details.componentsDto.nitrogenMonoxide ?: return null
        val nitrogenDioxide = details.componentsDto.nitrogenDioxide ?: return null
        val ozone = details.componentsDto.ozone ?: return null
        val sulphurDioxide = details.componentsDto.sulphurDioxide ?: return null
        val fineParticles = details.componentsDto.fineParticles ?: return null
        val coarseParticles = details.componentsDto.coarseParticles ?: return null
        val ammonia = details.componentsDto.ammonia ?: return null

        return AirQuality(
            dateTime = dateTime,
            airQualityIndex = airQualityIndex,
            carbonMonoxide = carbonMonoxide,
            nitrogenMonoxide = nitrogenMonoxide,
            nitrogenDioxide = nitrogenDioxide,
            ozone = ozone,
            sulphurDioxide = sulphurDioxide,
            ammonia = ammonia,
            fineParticles = fineParticles,
            coarseParticles = coarseParticles,
            airQualityIndexEnum = AirQualityEnum.getQualityByValue(airQualityIndex),
            carbonMonoxideEnum = CarbonMonoxideEnum.getQualityByValue(carbonMonoxide),
            nitrogenMonoxideEnum = NitrogenMonoxideEnum.getQualityByValue(nitrogenMonoxide),
            nitrogenDioxideEnum = NitrogenDioxideEnum.getQualityByValue(nitrogenDioxide),
            ozoneEnum = OzoneEnum.getQualityByValue(ozone),
            sulphurDioxideEnum = SulphurDioxideEnum.getQualityByValue(sulphurDioxide),
            ammoniaEnum = AmmoniaEnum.getQualityByValue(ammonia),
            fineParticlesEnum = FineParticlesEnum.getQualityByValue(fineParticles),
            coarseParticlesEnum = CoarseParticlesEnum.getQualityByValue(coarseParticles)
        )
    }

    fun setStringIds(settings: Settings) {
        val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
        val pressureType = PressureType.getByItemType(settings.pressureUnit)
        val speedType = SpeedType.getByItemType(settings.speedUnit)
        val hourFormatStringId = if (settings.amPmHourFormat) {
            R.string.lbl_hour_format_am_pm
        } else {
            R.string.lbl_hour_format
        }

        _uiState.value = _uiState.value?.copy(
            temperatureValueStringId = temperatureType.valueStringId,
            pressureValueStringId = pressureType.valueStringId,
            speedValueStringId = speedType.valueStringId,
            hourFormatStringId = hourFormatStringId
        )
    }

    data class UiState(
        val currentWeather: CurrentWeather? = null,
        val currentWeatherError: String? = null,
        val fiveDaysWeather: FiveDaysWeather? = null,
        val fiveDaysWeatherError: String? = null,
        val airQuality: AirQuality? = null,
        val airQualityError: String? = null,
        val temperatureValueStringId: Int = R.string.lbl_unit_value_celsius,
        val pressureValueStringId: Int = R.string.lbl_unit_value_hpa,
        val speedValueStringId: Int = R.string.lbl_unit_value_meters_per_second,
        val hourFormatStringId: Int = R.string.lbl_hour_format_am_pm
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
