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
import com.example.homeassistant.repository.DatabaseRepository
import com.example.homeassistant.ui.adapter.AirQualityRecordsAdapter
import com.example.homeassistant.ui.viewmodel.DatabaseViewModel

class AirQualityRecordsFragment : Fragment() {

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

        val airQualityRecordsAdapter = AirQualityRecordsAdapter()
        val airQualityRecordsRecyclerView = view.findViewById<RecyclerView>(R.id.air_quality_records)
        airQualityRecordsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = airQualityRecordsAdapter
        }

        databaseViewModel.getAirQualityRecords().observe(viewLifecycleOwner) { airQualityRecords ->
            airQualityRecords?.let {
                airQualityRecordsAdapter.submitList(it)
            }
        }
    }
}
