package com.example.homeassistant.repository.api

import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.api.dto.AirPollutionDto
import com.example.homeassistant.domain.api.dto.CurrentWeatherDto
import com.example.homeassistant.service.WeatherApiService
import java.io.IOException

class WeatherApiRepository {
    suspend fun getCurrentWeather(
        latitude: Float,
        longitude: Float
    ): CallResult<CurrentWeatherDto> {
        return safeApiCall(
            call = { getCurrentWeatherCall(latitude, longitude) },
            errorMessage = "Exception occurred"
        )
    }

    suspend fun getAirPollution(
        latitude: Float,
        longitude: Float
    ): CallResult<AirPollutionDto> {
        return safeApiCall(
            call = { getAirPollutionCall(latitude, longitude) },
            errorMessage = "Exception occurred"
        )
    }

    private suspend fun getCurrentWeatherCall(
        latitude: Float,
        longitude: Float
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

    private suspend fun getAirPollutionCall(
        latitude: Float,
        longitude: Float
    ): CallResult<AirPollutionDto> {
        val response = WeatherApiService.airPollutionRetrofitService.getAirPollution(
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
