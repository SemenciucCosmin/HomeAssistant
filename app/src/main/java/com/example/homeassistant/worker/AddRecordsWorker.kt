package com.example.homeassistant.worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homeassistant.database.HomeAssistantDatabase
import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.api.dto.AirQualityDto
import com.example.homeassistant.domain.database.AirQualityEntity
import com.example.homeassistant.repository.DatabaseRepository
import com.example.homeassistant.repository.WeatherApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddRecordsWorker(private val appContext: Context, private val params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    private val weatherApiRepository = WeatherApiRepository()
    private val databaseRepository = DatabaseRepository(
        HomeAssistantDatabase.getDatabase(appContext)
    )
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            if (isLocationPermissionGranted()) {
                latitude = params.inputData.getDouble("latitude", 0.0)
                longitude = params.inputData.getDouble("longitude", 0.0)
            }

            if (latitude != 0.0 && longitude != 0.0) {
                val airQualityCallResult = weatherApiRepository.getAirQuality(
                    latitude = latitude,
                    longitude = longitude
                )
                addAirQualityRecord(airQualityCallResult)
            }
        }
        return Result.success()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun addAirQualityRecord(airQualityCallResult: CallResult<AirQualityDto>) {
        if (airQualityCallResult is CallResult.Success) {
            val airQualityDto = airQualityCallResult.data
            val airQualityEntity = fromDtoToEntity(airQualityDto)
            databaseRepository.insert(airQualityEntity)
        }
    }

    private fun fromDtoToEntity(airQualityDto: AirQualityDto): AirQualityEntity {
        val details = airQualityDto.detailsDto?.first()
        val dateTime = details?.dateTime ?: System.currentTimeMillis()
        val airQualityIndex = details?.airQualityIndexDto?.index ?: 0
        val carbonMonoxide = details?.componentsDto?.carbonMonoxide ?: 0.0
        val nitrogenMonoxide = details?.componentsDto?.nitrogenMonoxide ?: 0.0
        val nitrogenDioxide = details?.componentsDto?.nitrogenDioxide ?: 0.0
        val ozone = details?.componentsDto?.ozone ?: 0.0
        val sulphurDioxide = details?.componentsDto?.sulphurDioxide ?: 0.0
        val ammonia = details?.componentsDto?.ammonia ?: 0.0
        val fineParticles = details?.componentsDto?.fineParticles ?: 0.0
        val coarseParticles = details?.componentsDto?.coarseParticles ?: 0.0

        return AirQualityEntity(
            dateTime = dateTime,
            airQualityIndex = airQualityIndex,
            carbonMonoxide = carbonMonoxide,
            nitrogenMonoxide = nitrogenMonoxide,
            nitrogenDioxide = nitrogenDioxide,
            ozone = ozone,
            sulphurDioxide = sulphurDioxide,
            ammonia = ammonia,
            fineParticles = fineParticles,
            coarseParticles = coarseParticles
        )
    }
}
