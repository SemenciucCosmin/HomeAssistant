package com.example.homeassistant.service

import com.example.homeassistant.BuildConfig
import com.example.homeassistant.domain.api.dto.AirQualityDto
import com.example.homeassistant.domain.api.dto.AirQualityIndexDto
import com.example.homeassistant.domain.api.dto.CurrentWeatherDto
import com.example.homeassistant.domain.api.dto.FiveDaysWeatherDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .addLast(KotlinJsonAdapterFactory())
    .build()

private val weatherRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl("https://api.openweathermap.org")
    .build()

interface CurrentWeatherApiService {
    @GET("/data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String = BuildConfig.API_KEY
    ): Response<CurrentWeatherDto>
}

interface FiveDaysWeatherApiService {
    @GET("/data/2.5/forecast")
    suspend fun getFiveDaysWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String = BuildConfig.API_KEY
    ): Response<FiveDaysWeatherDto>
}

interface AirQualityApiService {
    @GET("/data/2.5/air_pollution")
    suspend fun getAirQuality(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String = BuildConfig.API_KEY
    ): Response<AirQualityDto>
}

object WeatherApiService {
    val currentWeatherRetrofitService: CurrentWeatherApiService by lazy {
        weatherRetrofit.create(CurrentWeatherApiService::class.java)
    }
    val fiveDaysWeatherRetrofitService: FiveDaysWeatherApiService by lazy {
        weatherRetrofit.create(FiveDaysWeatherApiService::class.java)
    }
    val airQualityRetrofitService: AirQualityApiService by lazy {
        weatherRetrofit.create(AirQualityApiService::class.java)
    }
}
