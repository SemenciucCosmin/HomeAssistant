package com.example.homeassistant.domain

import com.example.homeassistant.R
import kotlin.math.roundToInt

enum class OzoneEnum(
    val titleStringId: Int = R.string.lbl_ozone,
    val lowerBound: Int,
    val upperBound: Int,
    val qualityStringId: Int
) {
    GOOD(lowerBound = 0, upperBound = 60, qualityStringId = R.string.lbl_quality_good),
    FAIR(lowerBound = 60, upperBound = 100, qualityStringId = R.string.lbl_quality_fair),
    MODERATE(lowerBound = 100, upperBound = 140, qualityStringId = R.string.lbl_quality_moderate),
    POOR(lowerBound = 140, upperBound = 180, qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(lowerBound = 180, upperBound = Int.MAX_VALUE, qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Double): OzoneEnum {
            return when (value.roundToInt()) {
                in (GOOD.lowerBound..GOOD.upperBound) -> GOOD
                in (FAIR.lowerBound..FAIR.upperBound) -> FAIR
                in (MODERATE.lowerBound..MODERATE.upperBound) -> MODERATE
                in (POOR.lowerBound..POOR.upperBound) -> POOR
                in (VERY_POOR.lowerBound..GOOD.upperBound) -> VERY_POOR
                else -> GOOD
            }
        }
    }
}
