package com.example.homeassistant.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homeassistant.R
import com.example.homeassistant.database.HomeAssistantDatabase
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.api.CallResult
import com.example.homeassistant.domain.api.dto.AirQualityDto
import com.example.homeassistant.domain.api.dto.CurrentWeatherDto
import com.example.homeassistant.domain.database.AirQualityEntity
import com.example.homeassistant.domain.database.CurrentWeatherEntity
import com.example.homeassistant.repository.DatabaseRepository
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class AddRecordsWorker(private val appContext: Context, private val params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 0
    }

    private val weatherApiRepository = WeatherApiRepository()
    private val databaseRepository = DatabaseRepository(
        HomeAssistantDatabase.getDatabase(appContext)
    )
    private val settingsRepository = SettingsRepository(
        SettingsDataSource(appContext),
        PhonePermissionDataSource(appContext)
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
                val currentWeatherCallResult = weatherApiRepository.getCurrentWeather(
                    latitude = latitude,
                    longitude = longitude
                )

                val airQualityCallResult = weatherApiRepository.getAirQuality(
                    latitude = latitude,
                    longitude = longitude
                )

                addCurrentWeatherRecord(currentWeatherCallResult)
                addAirQualityRecord(airQualityCallResult)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val settings = settingsRepository.getSettings().first()
            if (settings.showNotifications) {
                val intent = Intent(appContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(
                    appContext,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val builder = NotificationCompat.Builder(appContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(appContext.getString(R.string.lbl_notification_title))
                    .setContentText(appContext.getString(R.string.lbl_notification_message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                val channelName = appContext.getString(R.string.lbl_notification_channel_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val descriptionText = appContext.getString(R.string.lbl_notification_description)
                val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                    description = descriptionText
                }
                val notificationManager = appContext.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                with(NotificationManagerCompat.from(appContext)) {
                    notify(NOTIFICATION_ID, builder.build())
                }
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

    private suspend fun addCurrentWeatherRecord(currentWeatherCallResult: CallResult<CurrentWeatherDto>) {
        if (currentWeatherCallResult is CallResult.Success) {
            val currentWeatherDto = currentWeatherCallResult.data
            val currentWeatherEntity = fromCurrentWeatherDtoToEntity(currentWeatherDto)
            databaseRepository.insertCurrentWeatherEntity(currentWeatherEntity)
        }
    }

    private suspend fun addAirQualityRecord(airQualityCallResult: CallResult<AirQualityDto>) {
        if (airQualityCallResult is CallResult.Success) {
            val airQualityDto = airQualityCallResult.data
            val airQualityEntity = fromAirQualityDtoToEntity(airQualityDto)
            databaseRepository.insertAirQualityEntity(airQualityEntity)
        }
    }

    private fun fromCurrentWeatherDtoToEntity(currentWeatherDto: CurrentWeatherDto): CurrentWeatherEntity {
        val mainWeather = currentWeatherDto.weatherDto?.first()?.main ?: ""
        val description = currentWeatherDto.weatherDto?.first()?.description ?: ""
        val temperature = currentWeatherDto.measurementsDto?.temperature ?: 0.0
        val feelsLike = currentWeatherDto.measurementsDto?.feelsLike ?: 0.0
        val minTemperature = currentWeatherDto.measurementsDto?.minTemperature ?: 0.0
        val maxTemperature = currentWeatherDto.measurementsDto?.maxTemperature ?: 0.0
        val pressure = currentWeatherDto.measurementsDto?.pressure ?: 0
        val humidity = currentWeatherDto.measurementsDto?.humidity ?: 0
        val visibility = currentWeatherDto.visibility ?: 0
        val windSpeed = currentWeatherDto.windDto?.speed ?: 0.0
        val cloudiness = currentWeatherDto.cloudsDto?.cloudiness ?: 0
        val dateTime = currentWeatherDto.dateTime ?: System.currentTimeMillis()
        val sunriseTime = currentWeatherDto.weatherInfoDto?.sunriseTime ?: 0L
        val sunsetTime = currentWeatherDto.weatherInfoDto?.sunsetTime ?: 0L
        val cityName = currentWeatherDto.cityName ?: ""

        return CurrentWeatherEntity(
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

    private fun fromAirQualityDtoToEntity(airQualityDto: AirQualityDto): AirQualityEntity {
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
