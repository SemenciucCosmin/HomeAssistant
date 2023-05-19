package com.example.homeassistant.domain

import com.example.homeassistant.R
import kotlin.math.roundToInt

enum class NitrogenDioxideEnum(
    val titleStringId: Int = R.string.lbl_nitrogen_dioxide,
    val lowerBound: Int,
    val upperBound: Int,
    val qualityStringId: Int
) {
    GOOD(lowerBound = 0, upperBound = 40, qualityStringId = R.string.lbl_quality_good),
    FAIR(lowerBound = 40, upperBound = 70, qualityStringId = R.string.lbl_quality_fair),
    MODERATE(lowerBound = 70, upperBound = 150, qualityStringId = R.string.lbl_quality_moderate),
    POOR(lowerBound = 150, upperBound = 200, qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(lowerBound = 200, upperBound = Int.MAX_VALUE, qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Double): NitrogenDioxideEnum {
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
