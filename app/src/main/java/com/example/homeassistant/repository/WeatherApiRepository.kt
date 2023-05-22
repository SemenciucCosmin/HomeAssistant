package com.example.homeassistant.repository

import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.api.dto.AirQualityDto
import com.example.homeassistant.domain.api.dto.CurrentWeatherDto
import com.example.homeassistant.domain.api.dto.FiveDaysWeatherDto
import com.example.homeassistant.service.WeatherApiService
import java.io.IOException

class WeatherApiRepository {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): CallResult<CurrentWeatherDto> {
        return safeApiCall(
            call = { getCurrentWeatherCall(latitude, longitude) },
            errorMessage = "Exception occurred"
        )
    }

    suspend fun getFiveDaysWeather(
        latitude: Double,
        longitude: Double
    ): CallResult<FiveDaysWeatherDto> {
        return safeApiCall(
            call = { getFiveDaysWeatherCall(latitude, longitude) },
            errorMessage = "Exception occurred"
        )
    }

    suspend fun getAirQuality(
        latitude: Double,
        longitude: Double
    ): CallResult<AirQualityDto> {
        return safeApiCall(
            call = { getAirQualityCall(latitude, longitude) },
            errorMessage = "Exception occurred"
        )
    }

    private suspend fun getCurrentWeatherCall(
        latitude: Double,
        longitude: Double
    ): CallResult<CurrentWeatherDto> {
        val response = WeatherApiService.currentWeatherRetrofitService.getCurrentWeather(
            latitude,
            longitude
        )

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                CallResult.Success(body)
            } else {
                CallResult.Error(Exception(response.errorBody().toString()))
            }
        } else {
            CallResult.Error(Exception(response.errorBody().toString()))
        }
    }

    private suspend fun getFiveDaysWeatherCall(
        latitude: Double,
        longitude: Double
    ): CallResult<FiveDaysWeatherDto> {
        val response = WeatherApiService.fiveDaysWeatherRetrofitService.getFiveDaysWeather(
            latitude,
            longitude
        )

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                CallResult.Success(body)
            } else {
                CallResult.Error(Exception(response.errorBody().toString()))
            }
        } else {
            CallResult.Error(Exception(response.errorBody().toString()))
        }
    }

    private suspend fun getAirQualityCall(
        latitude: Double,
        longitude: Double
    ): CallResult<AirQualityDto> {
        val response = WeatherApiService.airQualityRetrofitService.getAirQuality(
            latitude,
            longitude
        )

        return if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                CallResult.Success(body)
            } else {
                CallResult.Error(Exception(response.errorBody().toString()))
            }
        } else {
            CallResult.Error(Exception(response.errorBody().toString()))
        }
    }

    private suspend fun <T : Any> safeApiCall(
        call: suspend () -> CallResult<T>,
        errorMessage: String
    ): CallResult<T> {
        return try {
            call.invoke()
        } catch (exception: Exception) {
            CallResult.Error(IOException(errorMessage, exception))
        }
    }
}
