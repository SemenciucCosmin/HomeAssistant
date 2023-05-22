package com.example.homeassistant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.homeassistant.R
import com.example.homeassistant.domain.WeatherRecord
import com.example.homeassistant.ui.viewholder.WeatherRecordViewHolder

class WeatherRecordsAdapter :
    ListAdapter<WeatherRecord, WeatherRecordViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<WeatherRecord>() {
            override fun areItemsTheSame(
                oldItem: WeatherRecord,
                newItem: WeatherRecord
            ): Boolean {
                return oldItem.weather.dateTime == newItem.weather.dateTime
            }

            override fun areContentsTheSame(
                oldItem: WeatherRecord,
                newItem: WeatherRecord
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherRecordViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.weather_record, parent, false)
        return WeatherRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
