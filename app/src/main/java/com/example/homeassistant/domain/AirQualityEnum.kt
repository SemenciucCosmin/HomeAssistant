package com.example.homeassistant.domain

import com.example.homeassistant.R

enum class AirQualityEnum(
    val titleStringId: Int = R.string.lbl_air_quality,
    val qualityStringId: Int
) {
    GOOD(qualityStringId = R.string.lbl_quality_good),
    FAIR(qualityStringId = R.string.lbl_quality_fair),
    MODERATE(qualityStringId = R.string.lbl_quality_moderate),
    POOR(qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Int): AirQualityEnum {
            return when (value) {
                1 -> GOOD
                2 -> FAIR
                3 -> MODERATE
                4 -> POOR
                5 -> VERY_POOR
                else -> GOOD
            }
        }
    }
}
