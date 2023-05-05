package com.example.homeassistant.domain.settings

import com.example.homeassistant.R
import com.example.homeassistant.utils.KILOMETERS_PER_HOUR_TYPE
import com.example.homeassistant.utils.KNOTS_TYPE
import com.example.homeassistant.utils.METERS_PER_SECOND_TYPE
import com.example.homeassistant.utils.MILES_PER_HOUR_TYPE

enum class SpeedType(val itemType: String, val stringId: Int) {
    METERS_PER_SECOND(METERS_PER_SECOND_TYPE, R.string.lbl_unit_meters_per_second),
    KILOMETERS_PER_HOUR(KILOMETERS_PER_HOUR_TYPE, R.string.lbl_unit_kilometers_per_hour),
    MILES_PER_HOUR(MILES_PER_HOUR_TYPE, R.string.lbl_unit_miles_per_hour),
    KNOTS(KNOTS_TYPE, R.string.lbl_unit_knots);

    companion object {
        fun getByItemType(itemType: String) =
            values().firstOrNull { it.itemType == itemType } ?: METERS_PER_SECOND
    }
}
