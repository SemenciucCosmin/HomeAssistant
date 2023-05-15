package com.example.homeassistant.domain.settings

import com.example.homeassistant.R
import com.example.homeassistant.utils.KILOMETERS_PER_HOUR_TYPE
import com.example.homeassistant.utils.KNOTS_TYPE
import com.example.homeassistant.utils.METERS_PER_SECOND_TYPE
import com.example.homeassistant.utils.MILES_PER_HOUR_TYPE
import kotlin.math.roundToInt

enum class SpeedType(val itemType: String, val stringId: Int, val valueStringId: Int) {
    METERS_PER_SECOND(METERS_PER_SECOND_TYPE, R.string.lbl_unit_meters_per_second, R.string.lbl_unit_value_meters_per_second),
    KILOMETERS_PER_HOUR(KILOMETERS_PER_HOUR_TYPE, R.string.lbl_unit_kilometers_per_hour, R.string.lbl_unit_value_kilometers_per_hour),
    MILES_PER_HOUR(MILES_PER_HOUR_TYPE, R.string.lbl_unit_miles_per_hour, R.string.lbl_unit_value_miles_per_hour),
    KNOTS(KNOTS_TYPE, R.string.lbl_unit_knots, R.string.lbl_unit_value_knots);

    companion object {
        fun getByItemType(itemType: String) =
            values().firstOrNull { it.itemType == itemType } ?: METERS_PER_SECOND

        fun getSpeedByType(speed: Double, itemType: SpeedType) : Double {
            return when(itemType) {
                METERS_PER_SECOND -> {
                    speed
                }

                KILOMETERS_PER_HOUR -> {
                    metersPerSecondToKilometersPerHour(speed)
                }

                MILES_PER_HOUR -> {
                    metersPerSecondToMilesPerHour(speed)
                }

                KNOTS -> {
                    metersPerSecondToKnots(speed)
                }
            }
        }

        private fun metersPerSecondToKilometersPerHour(speed: Double) = speed * 3.6f
        private fun metersPerSecondToMilesPerHour(speed: Double) = ((speed * 2.2369f) * 10).roundToInt() / 10.0
        private fun metersPerSecondToKnots(speed: Double) = ((speed * 1.9438452f) * 10).roundToInt() / 10.0
    }
}
