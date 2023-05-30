package com.example.homeassistant.ui.adapter

import com.example.homeassistant.domain.Forecast

interface FiveDaysWeatherCallback {
    fun onItemClick(forecast: Forecast)
}
