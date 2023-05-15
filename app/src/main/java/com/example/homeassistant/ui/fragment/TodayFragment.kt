package com.example.homeassistant.ui.fragment

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
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
import com.example.homeassistant.domain.api.AirPollution
import com.example.homeassistant.domain.api.CurrentWeather
import com.example.homeassistant.domain.bluetooth.BluetoothStatus
import com.example.homeassistant.domain.settings.PressureType
import com.example.homeassistant.domain.settings.Settings
import com.example.homeassistant.domain.settings.SpeedType
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.BluetoothRepository
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.ui.viewmodel.BluetoothViewModel
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.example.homeassistant.ui.viewmodel.WeatherApiViewModel
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.utils.showBluetoothStatusRationale
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext


class TodayFragment : Fragment() {
    companion object {
        private const val TAG = "TodayFragment"
    }

    private lateinit var cardSensorData: View
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
            weatherApiViewModel.getCurrentWeather(46.781773, 23.612390)
            weatherApiViewModel.getAirPollution(46.781773, 23.612390)
        }

        weatherApiViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            val settings = settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
                if (uiState.currentWeather != null && settings != null) {
                    setCurrentWeatherCard(uiState.currentWeather, settings)
                    setWeatherMeasurementsCard(uiState.currentWeather, settings)
                    setWeatherDetailsCard(uiState.currentWeather, settings)
                } else {
                    if (uiState.currentWeatherError != null) {

                    }
                }
            }

            if (uiState.airPollution != null) {
                setAirQualityCard(uiState.airPollution)
            } else {
                if (uiState.airPollutionError != null) {

                }
            }
        }

        bluetoothViewModel.uiState.observe(viewLifecycleOwner){ uiState ->
            val settings = settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
                if (settings != null) {
                    setSensorDataCard(uiState.temperature, uiState.humidity, settings)
                }
            }
        }

        bluetoothManager = requireActivity().getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        bluetoothViewModel.getBluetoothInformation()
            .observe(viewLifecycleOwner) { bluetoothInformation ->
                when (bluetoothInformation.status) {
                    BluetoothStatus.ON -> {
                        if (bluetoothSocket?.isConnected == false || bluetoothSocket == null) {
                            val selectedDevice = bluetoothAdapter.bondedDevices.firstOrNull {
                                it.address == bluetoothInformation.deviceAddress
                            }
                            if (selectedDevice != null && selectedDevice.uuids != null) {
                                val uuid = selectedDevice.uuids.first().uuid
                                bluetoothSocket = selectedDevice.createRfcommSocketToServiceRecord(uuid)
                                bluetoothSocket?.let { socket ->
                                    try {
                                        socket.connect()
                                        bluetoothViewModel.receiveData(socket)
                                    } catch (e: IOException) {
                                        Log.d(TAG, "Bluetooth socket connection failed.")
                                    }
                                }
                            }
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
        cardSensorData = requireView().findViewById(R.id.card_sensor_data_click)
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
        cardSensorData.setOnClickListener {
            findNavController().navigate(R.id.nav_sensor_records)
        }
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

    private fun setSensorDataCard(temperature: Double, humidity: Int, settings: Settings) {
        val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
        val sensorTemperature = TemperatureType.getTemperatureByType(
            temperature,
            temperatureType
        )
        val formattedTemperature = getString(temperatureType.valueStringId, sensorTemperature)

        sensorTemperatureView.text =
            getString(R.string.lbl_card_field_sensor_temperature, formattedTemperature)
        sensorHumidityView.text =
            getString(R.string.lbl_card_field_sensor_humidity, humidity)
    }

    private fun setCurrentWeatherCard(currentWeather: CurrentWeather, settings: Settings) {
        val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
        val currentTemperature = TemperatureType.getTemperatureByType(
            currentWeather.temperature,
            temperatureType
        )

        val feelsLike = TemperatureType.getTemperatureByType(
            currentWeather.feelsLike,
            temperatureType
        )

        val minTemperature = TemperatureType.getTemperatureByType(
            currentWeather.minTemperature,
            temperatureType
        )

        val maxTemperature = TemperatureType.getTemperatureByType(
            currentWeather.maxTemperature,
            temperatureType
        )

        val minTemperatureFormatted = getString(temperatureType.valueStringId, minTemperature)
        val maxTemperatureFormatted = getString(temperatureType.valueStringId, maxTemperature)

        currentTemperatureView.text = getString(temperatureType.valueStringId, currentTemperature)
        feelsLikeView.text = getString(temperatureType.valueStringId, feelsLike)
        mainWeatherView.text = currentWeather.mainWeather
        weatherDescriptionView.text = currentWeather.description.replaceFirstChar(Char::titlecase)
        minTemperatureView.text =
            getString(R.string.lbl_card_field_min_temperature, minTemperatureFormatted)
        maxTemperatureView.text =
            getString(R.string.lbl_card_field_max_temperature, maxTemperatureFormatted)
    }

    private fun setWeatherMeasurementsCard(currentWeather: CurrentWeather, settings: Settings) {
        val pressureType = PressureType.getByItemType(settings.pressureUnit)
        val speedType = SpeedType.getByItemType(settings.speedUnit)

        val pressure = PressureType.getPressureByType(currentWeather.pressure, pressureType)
        val speed = SpeedType.getSpeedByType(currentWeather.windSpeed, speedType)

        val pressureFormatted = getString(pressureType.valueStringId, pressure)
        val speedFormatted = getString(speedType.valueStringId, speed)

        currentPressureView.text = getString(R.string.lbl_card_field_pressure, pressureFormatted)
        currentWindSpeedView.text = getString(R.string.lbl_card_field_wind_speed, speedFormatted)
        currentHumidityView.text =
            getString(R.string.lbl_card_field_humidity, currentWeather.humidity)
        currentVisibilityView.text =
            getString(R.string.lbl_card_field_visibility, currentWeather.visibility)
    }

    private fun setWeatherDetailsCard(currentWeather: CurrentWeather, settings: Settings) {
        val pattern = if (settings.amPmHourFormat) "HH:mm a" else "HH:mm"
        val timeFormatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        val sunriseTime = timeFormatter.format(Date(currentWeather.sunriseTime * 1000))
        val sunsetTime = timeFormatter.format(Date(currentWeather.sunsetTime * 1000))

        cityView.text = getString(R.string.lbl_card_field_city, currentWeather.cityName)
        sunsetView.text = getString(R.string.lbl_card_field_sunset, sunsetTime)
        sunriseView.text = getString(R.string.lbl_card_field_sunrise, sunriseTime)
        cloudinessView.text =
            getString(R.string.lbl_card_field_cloudiness, currentWeather.cloudiness)
    }

    private fun setAirQualityCard(airPollution: AirPollution) {
        qualityIndexView.text =
            getString(R.string.lbl_card_field_quality_index, airPollution.airQualityIndex)
        carbonMonoxideView.text =
            getString(R.string.lbl_card_field_carbon_monoxide, airPollution.carbonMonoxide)
        fineParticlesView.text =
            getString(R.string.lbl_card_field_fine_particles, airPollution.fineParticles)
        coarseParticlesView.text =
            getString(R.string.lbl_card_field_coarse_particles, airPollution.coarseParticles)
    }
}
