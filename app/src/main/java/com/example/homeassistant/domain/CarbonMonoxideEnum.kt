package com.example.homeassistant.domain

import com.example.homeassistant.R
import kotlin.math.roundToInt

enum class CarbonMonoxideEnum(
    val titleStringId: Int = R.string.lbl_carbon_monoxide,
    val lowerBound: Int,
    val upperBound: Int,
    val qualityStringId: Int
) {
    GOOD(lowerBound = 0, upperBound = 4400, qualityStringId = R.string.lbl_quality_good),
    FAIR(lowerBound = 4400, upperBound = 9400, qualityStringId = R.string.lbl_quality_fair),
    MODERATE(lowerBound = 9400, upperBound = 12400, qualityStringId = R.string.lbl_quality_moderate),
    POOR(lowerBound = 12400, upperBound = 15400, qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(lowerBound = 15400, upperBound = Int.MAX_VALUE, qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Double): CarbonMonoxideEnum {
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
