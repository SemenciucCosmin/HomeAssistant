package com.example.homeassistant.domain.settings

import com.example.homeassistant.R
import com.example.homeassistant.utils.CELSIUS_TYPE
import com.example.homeassistant.utils.FAHRENHEIT_TYPE
import com.example.homeassistant.utils.KELVIN_TYPE

enum class TemperatureType(val itemType: String, val stringId: Int, val valueStringId: Int) {
    CELSIUS(CELSIUS_TYPE, R.string.lbl_unit_celsius, R.string.lbl_unit_value_celsius),
    FAHRENHEIT(FAHRENHEIT_TYPE, R.string.lbl_unit_fahrenheit, R.string.lbl_unit_value_fahrenheit),
    KELVIN(KELVIN_TYPE, R.string.lbl_unit_kelvin, R.string.lbl_unit_value_kelvin);

    companion object {
        fun getByItemType(itemType: String) =
            values().firstOrNull { it.itemType == itemType } ?: CELSIUS

        fun getTempByType(temperature: Double, itemType: TemperatureType): Double {
            return when (itemType) {
                CELSIUS -> {
                    kelvinToCelsius(temperature)
                }

                FAHRENHEIT -> {
                    kelvinToFahrenheit(temperature)
                }

                KELVIN -> {
                    temperature
                }
            }
        }

        private fun kelvinToCelsius(temp: Double) = temp - 273.15
        private fun kelvinToFahrenheit(temp: Double) = (temp - 273.15) * 9 / 5 + 32
    }
}
