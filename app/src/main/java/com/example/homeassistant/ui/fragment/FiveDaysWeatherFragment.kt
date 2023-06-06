package com.example.homeassistant.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.City
import com.example.homeassistant.domain.Forecast
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.ui.adapter.FiveDaysWeatherAdapter
import com.example.homeassistant.ui.adapter.FiveDaysWeatherCallback
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.example.homeassistant.ui.viewmodel.WeatherApiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FiveDaysWeatherFragment : Fragment(), FiveDaysWeatherCallback {
    companion object {
        private const val MILLIS_MULTIPLIER = 1000
    }

    private lateinit var cityNameView: TextView
    private lateinit var sunriseTimeView: TextView
    private lateinit var sunsetTimeView: TextView
    private val weatherApiViewModel: WeatherApiViewModel by viewModels {
        WeatherApiViewModel.WeatherApiViewModelFactory(WeatherApiRepository())
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.SettingsViewModelFactory(
            SettingsRepository(SettingsDataSource(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_five_days_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            weatherApiViewModel.setStringIds(settings)
            weatherApiViewModel.getFiveDaysWeather(settings)
        }

        val fiveDaysWeatherAdapter = FiveDaysWeatherAdapter(this)
        val fiveDaysWeatherRecyclerView =
            view.findViewById<RecyclerView>(R.id.five_days_weather_recycler_view)
        fiveDaysWeatherRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fiveDaysWeatherAdapter
        }

        weatherApiViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            fiveDaysWeatherAdapter.submitList(uiState.fiveDaysWeather?.forecasts)
            if (uiState.fiveDaysWeather != null)
                setViews(uiState.fiveDaysWeather.city, uiState.hourFormatStringId)
        }
    }

    private fun initViews() {
        cityNameView = requireView().findViewById(R.id.city_name)
        sunriseTimeView = requireView().findViewById(R.id.sunrise_time)
        sunsetTimeView = requireView().findViewById(R.id.sunset_time)
    }

    private fun setViews(city: City, hourFormatStringId: Int) {
        val timeFormatter = SimpleDateFormat(getString(hourFormatStringId), Locale.ENGLISH)
        val sunriseTimeFormatted = timeFormatter.format(Date(city.sunriseTime * MILLIS_MULTIPLIER))
        val sunsetTimeFormatted = timeFormatter.format(Date(city.sunsetTime * MILLIS_MULTIPLIER))

        cityNameView.text = city.name
        sunriseTimeView.text = getString(R.string.lbl_card_field_sunrise, sunriseTimeFormatted)
        sunsetTimeView.text = getString(R.string.lbl_card_field_sunset, sunsetTimeFormatted)
    }

    override fun onItemClick(forecast: Forecast) {
        weatherApiViewModel.updateFiveDaysWeatherCardState(forecast)
    }
}
