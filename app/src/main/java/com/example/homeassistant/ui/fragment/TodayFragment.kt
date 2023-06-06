package com.example.homeassistant.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.homeassistant.R
import com.example.homeassistant.datasource.BluetoothStatusDataSource
import com.example.homeassistant.datasource.DeviceDataSource
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.AirQuality
import com.example.homeassistant.domain.bluetooth.BluetoothStatus
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.BluetoothRepository
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.ui.viewmodel.BluetoothViewModel
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.example.homeassistant.ui.viewmodel.WeatherApiViewModel
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.utils.showBluetoothStatusRationale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TodayFragment : Fragment() {
    companion object {
        private const val MILLIS_MULTIPLIER = 1000
    }

    private lateinit var cardCurrentWeather: View
    private lateinit var cardWeatherMeasurements: View
    private lateinit var cardWeatherDetails: View
    private lateinit var cardAirQuality: View

    private lateinit var sensorTemperatureView: TextView
    private lateinit var sensorHumidityView: TextView

    private lateinit var currentTemperatureView: TextView
    private lateinit var feelsLikeView: TextView
    private lateinit var mainWeatherView: TextView
    private lateinit var weatherDescriptionView: TextView
    private lateinit var minTemperatureView: TextView
    private lateinit var maxTemperatureView: TextView

    private lateinit var currentPressureView: TextView
    private lateinit var currentHumidityView: TextView
    private lateinit var currentVisibilityView: TextView
    private lateinit var currentWindSpeedView: TextView

    private lateinit var cityView: TextView
    private lateinit var cloudinessView: TextView
    private lateinit var sunriseView: TextView
    private lateinit var sunsetView: TextView

    private lateinit var qualityIndexView: TextView
    private lateinit var carbonMonoxideView: TextView
    private lateinit var fineParticlesView: TextView
    private lateinit var coarseParticlesView: TextView

    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val weatherApiViewModel: WeatherApiViewModel by viewModels {
        WeatherApiViewModel.WeatherApiViewModelFactory(WeatherApiRepository())
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.SettingsViewModelFactory(
            SettingsRepository(SettingsDataSource(requireContext()))
        )
    }
    private val bluetoothViewModel: BluetoothViewModel by viewModels {
        BluetoothViewModel.BluetoothViewModelFactory(
            BluetoothRepository(
                PhonePermissionDataSource(requireContext()),
                BluetoothStatusDataSource(requireContext(), lifecycleScope),
                DeviceDataSource(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setClickListeners()

        settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            weatherApiViewModel.setStringIds(settings)
            weatherApiViewModel.getCurrentWeather(settings)
            weatherApiViewModel.getAirQuality(settings)
            bluetoothViewModel.setTemperatureType(settings.temperatureUnit)
        }

        weatherApiViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            if (uiState.currentWeather != null) {
                setCurrentWeatherCard(
                    mainWeather = uiState.currentWeather.mainWeather,
                    description = uiState.currentWeather.description,
                    temperature = uiState.currentWeather.temperature,
                    feelsLike = uiState.currentWeather.feelsLike,
                    minTemperature = uiState.currentWeather.minTemperature,
                    maxTemperature = uiState.currentWeather.maxTemperature,
                    temperatureValueStringId = uiState.temperatureValueStringId
                )

                setWeatherMeasurementsCard(
                    pressure = uiState.currentWeather.pressure,
                    speed = uiState.currentWeather.windSpeed,
                    humidity = uiState.currentWeather.humidity,
                    visibility = uiState.currentWeather.visibility,
                    pressureValueStringId = uiState.pressureValueStringId,
                    speedValueStringId = uiState.speedValueStringId
                )

                setWeatherDetailsCard(
                    cityName = uiState.currentWeather.cityName,
                    sunsetTime = uiState.currentWeather.sunsetTime,
                    sunriseTime = uiState.currentWeather.sunriseTime,
                    cloudiness = uiState.currentWeather.cloudiness,
                    hourValueStringId = uiState.hourFormatStringId
                )
            }

            if (uiState.airQuality != null) {
                setAirQualityCard(uiState.airQuality)
            }
        }

        bluetoothViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            setSensorDataCard(
                uiState.temperature,
                uiState.humidity,
                uiState.temperatureType
            )
        }

        bluetoothManager = requireContext().getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        bluetoothViewModel.getBluetoothInformation().observe(viewLifecycleOwner) { bluetooth ->
            when (bluetooth.status) {
                BluetoothStatus.ON -> {
                    if (bluetoothSocket?.isConnected == false || bluetoothSocket == null) {
                        bluetoothSocket = bluetoothViewModel.getBluetoothSocket(
                            bluetoothAdapter,
                            bluetooth.deviceAddress
                        )
                        bluetoothSocket?.let { bluetoothViewModel.receiveData(it) }
                    }
                }

                BluetoothStatus.OFF -> {
                    showBluetoothStatusRationale(requireContext())
                }

                BluetoothStatus.NOT_GRANTED -> {
                    showBluetoothPermissionRationale(requireContext())
                }
            }
        }
    }

    private fun initViews() {
        cardCurrentWeather = requireView().findViewById(R.id.card_current_weather_click)
        cardWeatherMeasurements = requireView().findViewById(R.id.card_weather_measurements_click)
        cardWeatherDetails = requireView().findViewById(R.id.card_weather_details_click)
        cardAirQuality = requireView().findViewById(R.id.card_air_quality_click)

        sensorTemperatureView = requireView().findViewById(R.id.sensor_temperature)
        sensorHumidityView = requireView().findViewById(R.id.sensor_humidity)

        currentTemperatureView = requireView().findViewById(R.id.current_temperature)
        feelsLikeView = requireView().findViewById(R.id.feels_like)
        mainWeatherView = requireView().findViewById(R.id.main_weather)
        weatherDescriptionView = requireView().findViewById(R.id.weather_description)
        minTemperatureView = requireView().findViewById(R.id.min_temperature)
        maxTemperatureView = requireView().findViewById(R.id.max_temperature)

        currentPressureView = requireView().findViewById(R.id.current_pressure)
        currentHumidityView = requireView().findViewById(R.id.current_humidity)
        currentVisibilityView = requireView().findViewById(R.id.current_visibility)
        currentWindSpeedView = requireView().findViewById(R.id.current_wind_speed)

        cityView = requireView().findViewById(R.id.city)
        cloudinessView = requireView().findViewById(R.id.cloudiness)
        sunriseView = requireView().findViewById(R.id.sunrise)
        sunsetView = requireView().findViewById(R.id.sunset)

        qualityIndexView = requireView().findViewById(R.id.quality_index)
        carbonMonoxideView = requireView().findViewById(R.id.carbon_monoxide)
        fineParticlesView = requireView().findViewById(R.id.fine_particles)
        coarseParticlesView = requireView().findViewById(R.id.coarse_particles)
    }

    private fun setClickListeners() {
        cardCurrentWeather.setOnClickListener {
            findNavController().navigate(R.id.nav_weather_records)
        }
        cardWeatherMeasurements.setOnClickListener {
            findNavController().navigate(R.id.nav_weather_records)
        }
        cardWeatherDetails.setOnClickListener {
            findNavController().navigate(R.id.nav_weather_records)
        }
        cardAirQuality.setOnClickListener {
            findNavController().navigate(R.id.nav_air_quality)
        }
    }

    private fun setSensorDataCard(
        temperature: Double,
        humidity: Int,
        temperatureType: TemperatureType
    ) {
        val convertedTemperature = TemperatureType.getTempByType(temperature, temperatureType)
        val formattedTemperature = getString(temperatureType.valueStringId, convertedTemperature)

        sensorTemperatureView.text =
            getString(R.string.lbl_card_field_sensor_temperature, formattedTemperature)
        sensorHumidityView.text =
            getString(R.string.lbl_card_field_sensor_humidity, humidity)
    }

    private fun setCurrentWeatherCard(
        mainWeather: String,
        description: String,
        temperature: Double,
        feelsLike: Double,
        minTemperature: Double,
        maxTemperature: Double,
        temperatureValueStringId: Int
    ) {
        val minTemperatureFormatted = getString(temperatureValueStringId, minTemperature)
        val maxTemperatureFormatted = getString(temperatureValueStringId, maxTemperature)

        currentTemperatureView.text = getString(temperatureValueStringId, temperature)
        feelsLikeView.text = getString(temperatureValueStringId, feelsLike)
        mainWeatherView.text = mainWeather
        weatherDescriptionView.text = description.replaceFirstChar(Char::titlecase)
        minTemperatureView.text =
            getString(R.string.lbl_card_field_min_temperature, minTemperatureFormatted)
        maxTemperatureView.text =
            getString(R.string.lbl_card_field_max_temperature, maxTemperatureFormatted)
    }

    private fun setWeatherMeasurementsCard(
        pressure: Int,
        speed: Double,
        humidity: Int,
        visibility: Int,
        pressureValueStringId: Int,
        speedValueStringId: Int
    ) {
        val pressureFormatted = getString(pressureValueStringId, pressure)
        val speedFormatted = getString(speedValueStringId, speed)

        currentPressureView.text = getString(R.string.lbl_card_field_pressure, pressureFormatted)
        currentWindSpeedView.text = getString(R.string.lbl_card_field_wind_speed, speedFormatted)
        currentHumidityView.text =
            getString(R.string.lbl_card_field_humidity, humidity)
        currentVisibilityView.text =
            getString(R.string.lbl_card_field_visibility, visibility)
    }

    private fun setWeatherDetailsCard(
        cityName: String,
        sunsetTime: Long,
        sunriseTime: Long,
        cloudiness: Int,
        hourValueStringId: Int
    ) {
        val timeFormatter = SimpleDateFormat(getString(hourValueStringId), Locale.ENGLISH)
        val sunriseTimeFormatted = timeFormatter.format(Date(sunriseTime * MILLIS_MULTIPLIER))
        val sunsetTimeFormatted = timeFormatter.format(Date(sunsetTime * MILLIS_MULTIPLIER))

        cityView.text = getString(R.string.lbl_card_field_city, cityName)
        sunriseView.text = getString(R.string.lbl_card_field_sunrise, sunriseTimeFormatted)
        sunsetView.text = getString(R.string.lbl_card_field_sunset, sunsetTimeFormatted)
        cloudinessView.text = getString(R.string.lbl_card_field_cloudiness, cloudiness)
    }

    private fun setAirQualityCard(airQuality: AirQuality) {
        qualityIndexView.text = getString(
            R.string.lbl_card_field_quality_index,
            airQuality.airQualityIndex,
            getString(airQuality.airQualityIndexEnum.qualityStringId)
        )
        carbonMonoxideView.text = getString(
            R.string.lbl_card_field_carbon_monoxide,
            airQuality.carbonMonoxide,
            getString(airQuality.carbonMonoxideEnum.qualityStringId)
        )
        fineParticlesView.text = getString(
            R.string.lbl_card_field_fine_particles,
            airQuality.fineParticles,
            getString(airQuality.fineParticlesEnum.qualityStringId)
        )
        coarseParticlesView.text = getString(
            R.string.lbl_card_field_coarse_particles,
            airQuality.coarseParticles,
            getString(airQuality.coarseParticlesEnum.qualityStringId)
        )
    }
}
