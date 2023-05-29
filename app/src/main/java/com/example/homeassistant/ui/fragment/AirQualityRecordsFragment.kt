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
import com.example.homeassistant.domain.AirQuality
import com.example.homeassistant.repository.DatabaseRepository
import com.example.homeassistant.ui.adapter.AirQualityRecordsAdapter
import com.example.homeassistant.ui.viewmodel.DatabaseViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.color.MaterialColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AirQualityRecordsFragment : Fragment() {

    companion object {
        private const val BAR_WIDTH = 0.5f
        private const val SQUARE_SPACE = 0f
        private const val SQUARE_OFFSET = 2f
        private const val SQUARE_SIZE = 6f
        private const val AXIS_MAXIMUM = 5f
        private const val AXIS_MINIMUM = 0f
        private const val DEFAULT_RECORD_VALUE = 0f
        private const val MAXIMUM_INDEX = 29
        private const val NO_LABEL = ""
    }

    private lateinit var barChart: BarChart
    private lateinit var startDateTextView: TextView
    private lateinit var endDateTextView: TextView
    private val databaseViewModel: DatabaseViewModel by viewModels {
        DatabaseViewModel.DatabaseViewModelFactory(
            DatabaseRepository(HomeAssistantDatabase.getDatabase(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_air_quality_records, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.air_quality_chart)
        startDateTextView = view.findViewById(R.id.start_date)
        endDateTextView = view.findViewById(R.id.end_date)

        val airQualityRecordsAdapter = AirQualityRecordsAdapter()
        val airQualityRecordsRecyclerView =
            view.findViewById<RecyclerView>(R.id.air_quality_records)
        airQualityRecordsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = airQualityRecordsAdapter
        }

        databaseViewModel.getAirQualityRecords().observe(viewLifecycleOwner) { airQualityRecords ->
            airQualityRecords?.let {
                airQualityRecordsAdapter.submitList(it)
                setBarChart(airQualityRecords.reversed())
            }
        }
    }

    private fun setBarChart(airQualityRecords: List<AirQuality>) {
        val formatter = SimpleDateFormat(getString(R.string.lbl_chart_date_format), Locale.ENGLISH)
        val startDate = formatter.format(Date(airQualityRecords.first().dateTime * 1000))
        val endDate = formatter.format(Date(airQualityRecords.last().dateTime * 1000 + 2592000000L))
        val barData = BarData()
        val numberOfRecords = airQualityRecords.size
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
        val shadowColor = MaterialColors.getColor(
            requireContext(),
            com.google.android.material.R.attr.colorTertiary,
            Color.BLACK
        )

        startDateTextView.text = startDate
        endDateTextView.text = endDate

        airQualityRecords.forEachIndexed { index, record ->
            val barDataSet = BarDataSet(
                listOf(BarEntry(index.toFloat(), record.airQualityIndex.toFloat())),
                NO_LABEL
            )
            barDataSet.setDrawValues(false)
            barDataSet.color = mainColor
            barDataSet.barShadowColor = shadowColor
            barData.addDataSet(barDataSet)
        }

        if (numberOfRecords <= MAXIMUM_INDEX) {
            for (index in numberOfRecords..MAXIMUM_INDEX) {
                val barDataSet = BarDataSet(
                    listOf(BarEntry(index.toFloat(), DEFAULT_RECORD_VALUE)),
                    NO_LABEL
                )
                barDataSet.setDrawValues(false)
                barDataSet.color = mainColor
                barDataSet.barShadowColor = shadowColor
                barData.addDataSet(barDataSet)
            }
        }

        barData.barWidth = BAR_WIDTH

        barChart.apply {
            data = barData
            setDrawBarShadow(true)
            setTouchEnabled(false)
            axisLeft.textColor = mainColor
            axisRight.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.setDrawAxisLine(false)
            description.isEnabled = false
            axisLeft.axisMaximum = AXIS_MAXIMUM
            axisLeft.axisMinimum = AXIS_MINIMUM

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
