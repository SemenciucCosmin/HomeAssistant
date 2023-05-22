package com.example.homeassistant.domain.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey
    @ColumnInfo(name = "dateTime")
    val dateTime: Long,

    @ColumnInfo(name = "mainWeather")
    val mainWeather: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "temperature")
    val temperature: Double,

    @ColumnInfo(name = "feelsLike")
    val feelsLike: Double,

    @ColumnInfo(name = "minTemperature")
    val minTemperature: Double,

    @ColumnInfo(name = "maxTemperature")
    val maxTemperature: Double,

    @ColumnInfo(name = "pressure")
    val pressure: Int,

    @ColumnInfo(name = "humidity")
    val humidity: Int,

    @ColumnInfo(name = "visibility")
    val visibility: Int,

    @ColumnInfo(name = "windSpeed")
    val windSpeed: Double,

    @ColumnInfo(name = "cloudiness")
    val cloudiness: Int,

    @ColumnInfo(name = "sunriseTime")
    val sunriseTime: Long,

    @ColumnInfo(name = "sunsetTime")
    val sunsetTime: Long,

    @ColumnInfo(name = "cityName")
    val cityName: String
)
