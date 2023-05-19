package com.example.homeassistant.domain

import com.example.homeassistant.R
import kotlin.math.roundToInt

enum class SulphurDioxideEnum(
    val titleStringId: Int = R.string.lbl_sulphur,
    val lowerBound: Int,
    val upperBound: Int,
    val qualityStringId: Int
) {
    GOOD(lowerBound = 0, upperBound = 20, qualityStringId = R.string.lbl_quality_good),
    FAIR(lowerBound = 20, upperBound = 80, qualityStringId = R.string.lbl_quality_fair),
    MODERATE(lowerBound = 80, upperBound = 250, qualityStringId = R.string.lbl_quality_moderate),
    POOR(lowerBound = 250, upperBound = 350, qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(lowerBound = 350, upperBound = Int.MAX_VALUE, qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Double): SulphurDioxideEnum {
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
