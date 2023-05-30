package com.example.homeassistant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.homeassistant.R
import com.example.homeassistant.domain.Forecast
import com.example.homeassistant.ui.viewholder.ForecastViewHolder

class FiveDaysWeatherAdapter(private val callback: FiveDaysWeatherCallback) :
    ListAdapter<Forecast, ForecastViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Forecast>() {
            override fun areItemsTheSame(
                oldItem: Forecast,
                newItem: Forecast
            ): Boolean {
                return oldItem.dateTime == newItem.dateTime
            }

            override fun areContentsTheSame(
                oldItem: Forecast,
                newItem: Forecast
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.five_days_weather_list_item, parent, false)
        return ForecastViewHolder(view, callback)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
