package com.example.homeassistant.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.homeassistant.R
import com.example.homeassistant.repository.api.WeatherApiRepository
import com.example.homeassistant.ui.viewmodel.api.WeatherApiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class TodayFragment : Fragment() {

    private val weatherApiViewModel: WeatherApiViewModel by viewModels {
        WeatherApiViewModel.WeatherApiViewModelFactory(WeatherApiRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherApiViewModel.getCurrentWeather(46.781773f, 23.612390f)
        weatherApiViewModel.getAirPollution(46.781773f, 23.612390f)

        weatherApiViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            val currentWeather = uiState.currentWeather
            val airPollution = uiState.airPollution
            val timeFormatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH)
            val date = timeFormatter.format(Date((currentWeather?.dateTime ?: 0L) * 1000))
        }
    }
}
