package com.example.homeassistant.ui.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.domain.enums.AirQualityEnum
import com.example.homeassistant.domain.enums.AmmoniaEnum
import com.example.homeassistant.domain.enums.CarbonMonoxideEnum
import com.example.homeassistant.domain.enums.CoarseParticlesEnum
import com.example.homeassistant.domain.enums.FineParticlesEnum
import com.example.homeassistant.domain.enums.NitrogenDioxideEnum
import com.example.homeassistant.domain.enums.NitrogenMonoxideEnum
import com.example.homeassistant.domain.enums.OzoneEnum
import com.example.homeassistant.domain.enums.SulphurDioxideEnum
import com.example.homeassistant.domain.AirQuality
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AirQualityRecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val airQualityRecordDate = view.findViewById<TextView>(R.id.air_quality_record_date)
    private val airQualityValue = view.findViewById<TextView>(R.id.air_quality_value)
    private val airQualityQuality = view.findViewById<TextView>(R.id.air_quality_quality)
    private val carbonMonoxideValue = view.findViewById<TextView>(R.id.carbon_monoxide_value)
    private val carbonMonoxideQuality = view.findViewById<TextView>(R.id.carbon_monoxide_quality)
    private val nitrogenMonoxideValue = view.findViewById<TextView>(R.id.nitrogen_monoxide_value)
    private val nitrogenMonoxideQuality = view.findViewById<TextView>(R.id.nitrogen_monoxide_quality)
    private val nitrogenDioxideValue = view.findViewById<TextView>(R.id.nitrogen_dioxide_value)
    private val nitrogenDioxideQuality = view.findViewById<TextView>(R.id.nitrogen_dioxide_quality)
    private val ozoneValue = view.findViewById<TextView>(R.id.ozone_value)
    private val ozoneQuality = view.findViewById<TextView>(R.id.ozone_quality)
    private val sulphurDioxideValue = view.findViewById<TextView>(R.id.sulphur_dioxide_value)
    private val sulphurDioxideQuality = view.findViewById<TextView>(R.id.sulphur_dioxide_quality)
    private val ammoniaValue = view.findViewById<TextView>(R.id.ammonia_value)
    private val ammoniaQuality = view.findViewById<TextView>(R.id.ammonia_quality)
    private val fineParticlesValue = view.findViewById<TextView>(R.id.fine_particles_value)
    private val fineParticlesQuality = view.findViewById<TextView>(R.id.fine_particles_quality)
    private val coarseParticlesValue = view.findViewById<TextView>(R.id.coarse_particles_value)
    private val coarseParticlesQuality = view.findViewById<TextView>(R.id.coarse_particles_quality)

    fun bind(airQuality: AirQuality) {
        val timeFormatter = SimpleDateFormat(
            view.context.getString(R.string.lbl_record_date_format),
            Locale.ENGLISH
        )
        val date = timeFormatter.format(Date(airQuality.dateTime * 1000))
        airQualityRecordDate.text = date

        airQualityValue.text = airQuality.airQualityIndex.toString()
        airQualityQuality.text = view.context.getString(
            AirQualityEnum.getQualityByValue(airQuality.airQualityIndex).qualityStringId
        )

        carbonMonoxideValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.carbonMonoxide
        )
        carbonMonoxideQuality.text = view.context.getString(
            CarbonMonoxideEnum.getQualityByValue(airQuality.carbonMonoxide).qualityStringId
        )

        nitrogenMonoxideValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.nitrogenMonoxide
        )
        nitrogenMonoxideQuality.text = view.context.getString(
            NitrogenMonoxideEnum.getQualityByValue(airQuality.nitrogenMonoxide).qualityStringId
        )

        nitrogenDioxideValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.nitrogenDioxide
        )
        nitrogenDioxideQuality.text = view.context.getString(
            NitrogenDioxideEnum.getQualityByValue(airQuality.nitrogenDioxide).qualityStringId
        )

        ozoneValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.ozone
        )
        ozoneQuality.text = view.context.getString(
            OzoneEnum.getQualityByValue(airQuality.ozone).qualityStringId
        )

        sulphurDioxideValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.sulphurDioxide
        )
        sulphurDioxideQuality.text = view.context.getString(
            SulphurDioxideEnum.getQualityByValue(airQuality.sulphurDioxide).qualityStringId
        )

        ammoniaValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.ammonia
        )
        ammoniaQuality.text = view.context.getString(
            AmmoniaEnum.getQualityByValue(airQuality.ammonia).qualityStringId
        )

        fineParticlesValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.fineParticles
        )
        fineParticlesQuality.text = view.context.getString(
            FineParticlesEnum.getQualityByValue(airQuality.fineParticles).qualityStringId
        )

        coarseParticlesValue.text = view.context.getString(
            R.string.lbl_float_format,
            airQuality.coarseParticles
        )
        coarseParticlesQuality.text = view.context.getString(
            CoarseParticlesEnum.getQualityByValue(airQuality.coarseParticles).qualityStringId
        )
    }
}
