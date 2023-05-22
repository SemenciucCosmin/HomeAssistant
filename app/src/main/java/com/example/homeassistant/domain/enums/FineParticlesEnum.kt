package com.example.homeassistant.domain.enums

import com.example.homeassistant.R
import kotlin.math.roundToInt

enum class FineParticlesEnum(
    val titleStringId: Int = R.string.lbl_fine_particles,
    val lowerBound: Int,
    val upperBound: Int,
    val qualityStringId: Int
) {
    GOOD(lowerBound = 0, upperBound = 10, qualityStringId = R.string.lbl_quality_good),
    FAIR(lowerBound = 10, upperBound = 25, qualityStringId = R.string.lbl_quality_fair),
    MODERATE(lowerBound = 25, upperBound = 50, qualityStringId = R.string.lbl_quality_moderate),
    POOR(lowerBound = 50, upperBound = 75, qualityStringId = R.string.lbl_quality_poor),
    VERY_POOR(lowerBound = 75, upperBound = Int.MAX_VALUE, qualityStringId = R.string.lbl_quality_very_poor);

    companion object {
        fun getQualityByValue(value: Double): FineParticlesEnum {
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
