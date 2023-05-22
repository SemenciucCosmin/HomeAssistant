package com.example.homeassistant.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.database.HomeAssistantDatabase
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.repository.DatabaseRepository
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.ui.adapter.WeatherRecordsAdapter
import com.example.homeassistant.ui.viewmodel.DatabaseViewModel
import com.example.homeassistant.ui.viewmodel.SettingsViewModel

class WeatherRecordsFragment : Fragment() {

    private val databaseViewModel: DatabaseViewModel by viewModels {
        DatabaseViewModel.DatabaseViewModelFactory(
            DatabaseRepository(HomeAssistantDatabase.getDatabase(requireContext()))
        )
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.SettingsViewModelFactory(
            SettingsRepository(
                SettingsDataSource(requireContext()),
                PhonePermissionDataSource(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val weatherRecordsAdapter = WeatherRecordsAdapter()
        val weatherRecordsRecyclerView = view.findViewById<RecyclerView>(R.id.weather_records)
        weatherRecordsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = weatherRecordsAdapter
        }

        settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            databaseViewModel.getWeatherRecords(settings)
        }

        databaseViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            weatherRecordsAdapter.submitList(uiState.weatherRecords)
        }
    }
}
