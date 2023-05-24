package com.example.homeassistant.ui.fragment

import android.graphics.Color
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
import com.example.homeassistant.database.HomeAssistantDatabase
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.WeatherRecord
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.DatabaseRepository
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.ui.adapter.WeatherRecordsAdapter
import com.example.homeassistant.ui.viewmodel.DatabaseViewModel
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRecordsFragment : Fragment() {

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val SQUARE_SPACE = 0f
        private const val SQUARE_OFFSET = 2f
        private const val SQUARE_SIZE = 6f
        private const val DEFAULT_RECORD_VALUE = 0f
        private const val MAXIMUM_INDEX = 29
        private const val NO_LABEL = ""
        private const val KELVIN_AXIS_MINIMUM = 233.15
        private const val KELVIN_AXIS_MAXIMUM = 323.15
    }

    private var axisMinimum: Double = KELVIN_AXIS_MINIMUM
    private var axisMaximum: Double = KELVIN_AXIS_MAXIMUM
    private lateinit var lineChart: LineChart
    private lateinit var startDateTextView: TextView
    private lateinit var endDateTextView: TextView
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

        lineChart = view.findViewById(R.id.weather_chart)
        startDateTextView = view.findViewById(R.id.start_date)
        endDateTextView = view.findViewById(R.id.end_date)

        val weatherRecordsAdapter = WeatherRecordsAdapter()
        val weatherRecordsRecyclerView = view.findViewById<RecyclerView>(R.id.weather_records)
        weatherRecordsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = weatherRecordsAdapter
        }

        settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            databaseViewModel.getWeatherRecords(settings)
            val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
            axisMinimum = TemperatureType.getTempByType(KELVIN_AXIS_MINIMUM, temperatureType)
            axisMaximum = TemperatureType.getTempByType(KELVIN_AXIS_MAXIMUM, temperatureType)
        }

        databaseViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.weatherRecords?.let {
                weatherRecordsAdapter.submitList(it)
                setBarChart(it)
            }
        }
    }

    private fun setBarChart(weatherRecords: List<WeatherRecord>) {
        val formatter = SimpleDateFormat(getString(R.string.lbl_chart_date_format), Locale.ENGLISH)
        val startDate = formatter.format(Date(weatherRecords.first().weather.dateTime * 1000))
        val endDate = formatter.format(Date(weatherRecords.last().weather.dateTime * 1000))
        val lineData = LineData()
        val numberOfRecords = weatherRecords.size
        val mainColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.colorPrimary,
            Color.BLACK
        )
        val secondaryColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.colorPrimarySurface,
            Color.BLACK
        )
        val transparentColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.colorTertiary,
            Color.BLACK
        )

        startDateTextView.text = startDate
        endDateTextView.text = endDate

        weatherRecords.forEachIndexed { index, record ->
            val lineDataSet = LineDataSet(
                listOf(BarEntry(index.toFloat(), record.weather.temperature.toFloat())),
                NO_LABEL
            )
            lineDataSet.setDrawValues(false)
            lineDataSet.setCircleColor(mainColor)
            lineDataSet.circleHoleColor = mainColor
            lineData.addDataSet(lineDataSet)
        }

        if (numberOfRecords <= MAXIMUM_INDEX) {
            for (index in numberOfRecords..MAXIMUM_INDEX) {
                val lineDataSet = LineDataSet(
                    listOf(BarEntry(index.toFloat(), DEFAULT_RECORD_VALUE)),
                    NO_LABEL
                )
                lineDataSet.setDrawValues(false)
                lineDataSet.setCircleColor(transparentColor)
                lineDataSet.circleHoleColor = transparentColor
                lineData.addDataSet(lineDataSet)
            }
        }

        lineChart.apply {
            data = lineData
            setTouchEnabled(false)
            axisLeft.textColor = mainColor
            axisRight.isEnabled = false
//            xAxis.isEnabled = false
//            axisLeft.setDrawAxisLine(false)
            description.isEnabled = false
            axisLeft.axisMaximum = axisMaximum.toFloat()
            axisLeft.axisMinimum = axisMinimum.toFloat()

            val chartLegend = legend
            chartLegend.xEntrySpace = SQUARE_SPACE
            chartLegend.xOffset = SQUARE_OFFSET
            chartLegend.formSize = SQUARE_SIZE

            val entries = chartLegend.entries
            entries.forEach { it.formColor = secondaryColor }
            entries.first().formColor = mainColor
            entries.last().formColor = mainColor
            chartLegend.setCustom(entries)

            invalidate()
            refreshDrawableState()
        }
    }
}
