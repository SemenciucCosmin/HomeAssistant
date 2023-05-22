package com.example.homeassistant.domain.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "air_quality")
data class AirQualityEntity(
    @PrimaryKey
    @ColumnInfo(name = "dateTime")
    val dateTime: Long,

    @ColumnInfo(name = "airQualityIndex")
    val airQualityIndex: Int,

    @ColumnInfo(name = "carbonMonoxide")
    val carbonMonoxide: Double,

    @ColumnInfo(name = "nitrogenMonoxide")
    val nitrogenMonoxide: Double,

    @ColumnInfo(name = "nitrogenDioxide")
    val nitrogenDioxide: Double,

    @ColumnInfo(name = "ozone")
    val ozone: Double,

    @ColumnInfo(name = "sulphurDioxide")
    val sulphurDioxide: Double,

    @ColumnInfo(name = "ammonia")
    val ammonia: Double,

    @ColumnInfo(name = "fineParticles")
    val fineParticles: Double,

    @ColumnInfo(name = "coarseParticles")
    val coarseParticles: Double
)
