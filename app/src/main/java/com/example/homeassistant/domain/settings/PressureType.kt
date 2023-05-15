package com.example.homeassistant.domain.settings

import com.example.homeassistant.R
import com.example.homeassistant.utils.HPA_TYPE
import com.example.homeassistant.utils.IN_HG_TYPE
import com.example.homeassistant.utils.KPA_TYPE
import com.example.homeassistant.utils.MM_HG_TYPE
import kotlin.math.roundToInt

enum class PressureType(val itemType: String, val stringId: Int, val valueStringId: Int) {
    HPA(HPA_TYPE, R.string.lbl_unit_hpa, R.string.lbl_unit_value_hpa),
    KPA(KPA_TYPE, R.string.lbl_unit_kpa, R.string.lbl_unit_value_kpa),
    MM_HG(MM_HG_TYPE, R.string.lbl_unit_mmHg, R.string.lbl_unit_value_mmHg),
    IN_HG(IN_HG_TYPE, R.string.lbl_unit_inHg, R.string.lbl_unit_value_inHg);

    companion object {
        fun getByItemType(itemType: String) =
            values().firstOrNull { it.itemType == itemType } ?: HPA

        fun getPressureByType(pressure: Int, itemType: PressureType): Int {
            return when (itemType) {
                HPA -> {
                    pressure
                }

                KPA -> {
                    hpaToKpa(pressure)
                }

                MM_HG -> {
                    hpaToMmHg(pressure)
                }

                IN_HG -> {
                    hpaToInHg(pressure)
                }
            }
        }

        private fun hpaToKpa(pressure: Int) = (pressure * 0.1).roundToInt()
        private fun hpaToMmHg(pressure: Int) = (pressure * 0.75).roundToInt()
        private fun hpaToInHg(pressure: Int) = (pressure * 0.030).roundToInt()
    }
}
