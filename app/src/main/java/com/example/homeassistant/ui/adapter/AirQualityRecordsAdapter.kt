package com.example.homeassistant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.homeassistant.R
import com.example.homeassistant.domain.AirQuality
import com.example.homeassistant.ui.viewholder.AirQualityRecordViewHolder

class AirQualityRecordsAdapter :
    ListAdapter<AirQuality, AirQualityRecordViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AirQuality>() {
            override fun areItemsTheSame(
                oldItem: AirQuality,
                newItem: AirQuality
            ): Boolean {
                return oldItem.dateTime == newItem.dateTime
            }

            override fun areContentsTheSame(
                oldItem: AirQuality,
                newItem: AirQuality
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirQualityRecordViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.air_quality_record, parent, false)
        return AirQualityRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: AirQualityRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}