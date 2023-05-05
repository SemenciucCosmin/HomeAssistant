package com.example.homeassistant.domain.settings

import com.example.homeassistant.R
import com.example.homeassistant.utils.HPA_TYPE
import com.example.homeassistant.utils.IN_HG_TYPE
import com.example.homeassistant.utils.KPA_TYPE
import com.example.homeassistant.utils.MM_HG_TYPE

enum class PressureType(val itemType: String, val stringId: Int) {
    HPA(HPA_TYPE, R.string.lbl_unit_hpa),
    KPA(KPA_TYPE, R.string.lbl_unit_kpa),
    MM_HG(MM_HG_TYPE, R.string.lbl_unit_mmHg),
    IN_HG(IN_HG_TYPE, R.string.lbl_unit_inHg);

    companion object {
        fun getByItemType(itemType: String) =
            values().firstOrNull { it.itemType == itemType } ?: HPA
    }
}
