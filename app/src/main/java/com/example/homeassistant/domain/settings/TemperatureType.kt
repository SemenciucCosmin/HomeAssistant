package com.example.homeassistant.domain.settings

import com.example.homeassistant.R
import com.example.homeassistant.utils.CELSIUS_TYPE
import com.example.homeassistant.utils.FAHRENHEIT_TYPE
import com.example.homeassistant.utils.KELVIN_TYPE

enum class TemperatureType(val itemType: String, val stringId: Int) {
    CELSIUS(CELSIUS_TYPE, R.string.lbl_unit_celsius),
    FAHRENHEIT(FAHRENHEIT_TYPE, R.string.lbl_unit_fahrenheit),
    KELVIN(KELVIN_TYPE, R.string.lbl_unit_kelvin);

    companion object {
        fun getByItemType(itemType: String) =
            values().firstOrNull { it.itemType == itemType } ?: CELSIUS
    }
}
