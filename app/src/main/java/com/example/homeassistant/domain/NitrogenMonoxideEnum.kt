package com.example.homeassistant.domain

import com.example.homeassistant.R
import kotlin.math.roundToInt

enum class NitrogenMonoxideEnum(
    val titleStringId: Int = R.string.lbl_nitrogen_monoxide,
    val lowerBound: Int,
    val upperBound: Int,
    val qualityStringId: Int
) {
    GOOD(lowerBound = 0, upperBound = 25, qualityStringId = R.string.lbl_quality_good),
    FAIR(lowerBound = 25, upperBound = 50, qualityStringId = R.string.lbl_quality_fair),
    MODERATE(lowerBound = 50, upperBound = 75, qualityStringId = R.string.lbl_quality_moderate),
    POOR(lowerBound = 75, upperBound = 100, qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(lowerBound = 100, upperBound = Int.MAX_VALUE, qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Double): NitrogenMonoxideEnum {
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
