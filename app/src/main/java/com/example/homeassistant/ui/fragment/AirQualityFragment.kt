package com.example.homeassistant.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.homeassistant.R
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.AirQualityEnum
import com.example.homeassistant.domain.AmmoniaEnum
import com.example.homeassistant.domain.CarbonMonoxideEnum
import com.example.homeassistant.domain.CoarseParticlesEnum
import com.example.homeassistant.domain.FineParticlesEnum
import com.example.homeassistant.domain.NitrogenDioxideEnum
import com.example.homeassistant.domain.NitrogenMonoxideEnum
import com.example.homeassistant.domain.OzoneEnum
import com.example.homeassistant.domain.SulphurDioxideEnum
import com.example.homeassistant.domain.api.AirPollution
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.repository.WeatherApiRepository
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.example.homeassistant.ui.viewmodel.WeatherApiViewModel
import com.google.android.material.card.MaterialCardView

class AirQualityFragment : Fragment() {

    private lateinit var cardAirQuality: MaterialCardView
    private lateinit var cardCarbonMonoxide: MaterialCardView
    private lateinit var cardNitrogenMonoxide: MaterialCardView
    private lateinit var cardNitrogenDioxide: MaterialCardView
    private lateinit var cardOzone: MaterialCardView
    private lateinit var cardSulphurDioxide: MaterialCardView
    private lateinit var cardAmmonia: MaterialCardView
    private lateinit var cardFineParticles: MaterialCardView
    private lateinit var cardCoarseParticles: MaterialCardView

    private lateinit var airQualityTitle: TextView
    private lateinit var airQualityValue: TextView
    private lateinit var airQualityGoodRange: TextView
    private lateinit var airQualityFairRange: TextView
    private lateinit var airQualityModerateRange: TextView
    private lateinit var airQualityPoorRange: TextView
    private lateinit var airQualityVeryPoorRange: TextView

    private lateinit var carbonMonoxideTitle: TextView
    private lateinit var carbonMonoxideValue: TextView
    private lateinit var carbonMonoxideGoodRange: TextView
    private lateinit var carbonMonoxideFairRange: TextView
    private lateinit var carbonMonoxideModerateRange: TextView
    private lateinit var carbonMonoxidePoorRange: TextView
    private lateinit var carbonMonoxideVeryPoorRange: TextView

    private lateinit var nitrogenMonoxideTitle: TextView
    private lateinit var nitrogenMonoxideValue: TextView
    private lateinit var nitrogenMonoxideGoodRange: TextView
    private lateinit var nitrogenMonoxideModerateRange: TextView
    private lateinit var nitrogenMonoxideFairRange: TextView
    private lateinit var nitrogenMonoxidePoorRange: TextView
    private lateinit var nitrogenMonoxideVeryPoorRange: TextView

    private lateinit var nitrogenDioxideTitle: TextView
    private lateinit var nitrogenDioxideValue: TextView
    private lateinit var nitrogenDioxideGoodRange: TextView
    private lateinit var nitrogenDioxideFairRange: TextView
    private lateinit var nitrogenDioxideModerateRange: TextView
    private lateinit var nitrogenDioxidePoorRange: TextView
    private lateinit var nitrogenDioxideVeryPoorRange: TextView

    private lateinit var ozoneTitle: TextView
    private lateinit var ozoneValue: TextView
    private lateinit var ozoneGoodRange: TextView
    private lateinit var ozoneFairRange: TextView
    private lateinit var ozoneModerateRange: TextView
    private lateinit var ozonePoorRange: TextView
    private lateinit var ozoneVeryPoorRange: TextView

    private lateinit var sulphurDioxideTitle: TextView
    private lateinit var sulphurDioxideValue: TextView
    private lateinit var sulphurDioxideGoodRange: TextView
    private lateinit var sulphurDioxideFairRange: TextView
    private lateinit var sulphurDioxideModerateRange: TextView
    private lateinit var sulphurDioxidePoorRange: TextView
    private lateinit var sulphurDioxideVeryPoorRange: TextView

    private lateinit var ammoniaTitle: TextView
    private lateinit var ammoniaValue: TextView
    private lateinit var ammoniaGoodRange: TextView
    private lateinit var ammoniaFairRange: TextView
    private lateinit var ammoniaModerateRange: TextView
    private lateinit var ammoniaPoorRange: TextView
    private lateinit var ammoniaVeryPoorRange: TextView

    private lateinit var fineParticlesTitle: TextView
    private lateinit var fineParticlesValue: TextView
    private lateinit var fineParticlesGoodRange: TextView
    private lateinit var fineParticlesFairRange: TextView
    private lateinit var fineParticlesModerateRange: TextView
    private lateinit var fineParticlesPoorRange: TextView
    private lateinit var fineParticlesVeryPoorRange: TextView

    private lateinit var coarseParticlesTitle: TextView
    private lateinit var coarseParticlesValue: TextView
    private lateinit var coarseParticlesGoodRange: TextView
    private lateinit var coarseParticlesFairRange: TextView
    private lateinit var coarseParticlesModerateRange: TextView
    private lateinit var coarseParticlesPoorRange: TextView
    private lateinit var coarseParticlesVeryPoorRange: TextView

    private val weatherApiViewModel: WeatherApiViewModel by viewModels {
        WeatherApiViewModel.WeatherApiViewModelFactory(WeatherApiRepository())
    }
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.SettingsViewModelFactory(
            SettingsRepository(
                SettingsDataSource(requireContext()),
                PhonePermissionDataSource(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_air_quality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setTitles()
        setRanges()

        settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            weatherApiViewModel.getAirPollution(settings)
        }

        weatherApiViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            if (uiState.airPollution != null) {
                setCardValues(uiState.airPollution)
            }
        }
    }

    private fun initViews() {
        cardAirQuality = requireView().findViewById(R.id.air_quality_card)
        cardCarbonMonoxide = requireView().findViewById(R.id.carbon_monoxide_card)
        cardNitrogenMonoxide = requireView().findViewById(R.id.nitrogen_monoxide_card)
        cardNitrogenDioxide = requireView().findViewById(R.id.nitrogen_dioxide_card)
        cardOzone = requireView().findViewById(R.id.ozone_card)
        cardSulphurDioxide = requireView().findViewById(R.id.sulphur_dioxide_card)
        cardAmmonia = requireView().findViewById(R.id.ammonia_card)
        cardFineParticles = requireView().findViewById(R.id.fine_particles_card)
        cardCoarseParticles = requireView().findViewById(R.id.coarse_particles_card)

        airQualityTitle = cardAirQuality.findViewById(R.id.title)
        airQualityValue = cardAirQuality.findViewById(R.id.value)
        airQualityGoodRange = cardAirQuality.findViewById(R.id.good_range)
        airQualityFairRange = cardAirQuality.findViewById(R.id.fair_range)
        airQualityModerateRange = cardAirQuality.findViewById(R.id.moderate_range)
        airQualityPoorRange = cardAirQuality.findViewById(R.id.poor_range)
        airQualityVeryPoorRange = cardAirQuality.findViewById(R.id.very_poor_range)

        carbonMonoxideTitle = cardCarbonMonoxide.findViewById(R.id.title)
        carbonMonoxideValue = cardCarbonMonoxide.findViewById(R.id.value)
        carbonMonoxideGoodRange = cardCarbonMonoxide.findViewById(R.id.good_range)
        carbonMonoxideFairRange = cardCarbonMonoxide.findViewById(R.id.fair_range)
        carbonMonoxideModerateRange = cardCarbonMonoxide.findViewById(R.id.moderate_range)
        carbonMonoxidePoorRange = cardCarbonMonoxide.findViewById(R.id.poor_range)
        carbonMonoxideVeryPoorRange = cardCarbonMonoxide.findViewById(R.id.very_poor_range)

        nitrogenMonoxideTitle = cardNitrogenMonoxide.findViewById(R.id.title)
        nitrogenMonoxideValue = cardNitrogenMonoxide.findViewById(R.id.value)
        nitrogenMonoxideGoodRange = cardNitrogenMonoxide.findViewById(R.id.good_range)
        nitrogenMonoxideFairRange = cardNitrogenMonoxide.findViewById(R.id.fair_range)
        nitrogenMonoxideModerateRange = cardNitrogenMonoxide.findViewById(R.id.moderate_range)
        nitrogenMonoxidePoorRange = cardNitrogenMonoxide.findViewById(R.id.poor_range)
        nitrogenMonoxideVeryPoorRange = cardNitrogenMonoxide.findViewById(R.id.very_poor_range)

        nitrogenDioxideTitle = cardNitrogenDioxide.findViewById(R.id.title)
        nitrogenDioxideValue = cardNitrogenDioxide.findViewById(R.id.value)
        nitrogenDioxideGoodRange = cardNitrogenDioxide.findViewById(R.id.good_range)
        nitrogenDioxideFairRange = cardNitrogenDioxide.findViewById(R.id.fair_range)
        nitrogenDioxideModerateRange = cardNitrogenDioxide.findViewById(R.id.moderate_range)
        nitrogenDioxidePoorRange = cardNitrogenDioxide.findViewById(R.id.poor_range)
        nitrogenDioxideVeryPoorRange = cardNitrogenDioxide.findViewById(R.id.very_poor_range)

        ozoneTitle = cardOzone.findViewById(R.id.title)
        ozoneValue = cardOzone.findViewById(R.id.value)
        ozoneGoodRange = cardOzone.findViewById(R.id.good_range)
        ozoneFairRange = cardOzone.findViewById(R.id.fair_range)
        ozoneModerateRange = cardOzone.findViewById(R.id.moderate_range)
        ozonePoorRange = cardOzone.findViewById(R.id.poor_range)
        ozoneVeryPoorRange = cardOzone.findViewById(R.id.very_poor_range)

        sulphurDioxideTitle = cardSulphurDioxide.findViewById(R.id.title)
        sulphurDioxideValue = cardSulphurDioxide.findViewById(R.id.value)
        sulphurDioxideGoodRange = cardSulphurDioxide.findViewById(R.id.good_range)
        sulphurDioxideFairRange = cardSulphurDioxide.findViewById(R.id.fair_range)
        sulphurDioxideModerateRange = cardSulphurDioxide.findViewById(R.id.moderate_range)
        sulphurDioxidePoorRange = cardSulphurDioxide.findViewById(R.id.poor_range)
        sulphurDioxideVeryPoorRange = cardSulphurDioxide.findViewById(R.id.very_poor_range)

        ammoniaTitle = cardAmmonia.findViewById(R.id.title)
        ammoniaValue = cardAmmonia.findViewById(R.id.value)
        ammoniaGoodRange = cardAmmonia.findViewById(R.id.good_range)
        ammoniaFairRange = cardAmmonia.findViewById(R.id.fair_range)
        ammoniaModerateRange = cardAmmonia.findViewById(R.id.moderate_range)
        ammoniaPoorRange = cardAmmonia.findViewById(R.id.poor_range)
        ammoniaVeryPoorRange = cardAmmonia.findViewById(R.id.very_poor_range)

        fineParticlesTitle = cardFineParticles.findViewById(R.id.title)
        fineParticlesValue = cardFineParticles.findViewById(R.id.value)
        fineParticlesGoodRange = cardFineParticles.findViewById(R.id.good_range)
        fineParticlesFairRange = cardFineParticles.findViewById(R.id.fair_range)
        fineParticlesModerateRange = cardFineParticles.findViewById(R.id.moderate_range)
        fineParticlesPoorRange = cardFineParticles.findViewById(R.id.poor_range)
        fineParticlesVeryPoorRange = cardFineParticles.findViewById(R.id.very_poor_range)

        coarseParticlesTitle = cardCoarseParticles.findViewById(R.id.title)
        coarseParticlesValue = cardCoarseParticles.findViewById(R.id.value)
        coarseParticlesGoodRange = cardCoarseParticles.findViewById(R.id.good_range)
        coarseParticlesFairRange = cardCoarseParticles.findViewById(R.id.fair_range)
        coarseParticlesModerateRange = cardCoarseParticles.findViewById(R.id.moderate_range)
        coarseParticlesPoorRange = cardCoarseParticles.findViewById(R.id.poor_range)
        coarseParticlesVeryPoorRange = cardCoarseParticles.findViewById(R.id.very_poor_range)
    }

    private fun setTitles() {
        airQualityTitle.text = getString(AirQualityEnum.GOOD.titleStringId)
        carbonMonoxideTitle.text = getString(CarbonMonoxideEnum.GOOD.titleStringId)
        nitrogenMonoxideTitle.text = getString(NitrogenMonoxideEnum.GOOD.titleStringId)
        nitrogenDioxideTitle.text = getString(NitrogenDioxideEnum.GOOD.titleStringId)
        ozoneTitle.text = getString(OzoneEnum.GOOD.titleStringId)
        sulphurDioxideTitle.text = getString(SulphurDioxideEnum.GOOD.titleStringId)
        ammoniaTitle.text = getString(AmmoniaEnum.GOOD.titleStringId)
        fineParticlesTitle.text = getString(FineParticlesEnum.GOOD.titleStringId)
        coarseParticlesTitle.text = getString(CoarseParticlesEnum.GOOD.titleStringId)
    }
    
    private fun setRanges() {
        airQualityGoodRange.text = getString(R.string.lbl_air_quality_good_index)
        airQualityFairRange.text = getString(R.string.lbl_air_quality_fair_index)
        airQualityModerateRange.text = getString(R.string.lbl_air_quality_moderate_index)
        airQualityPoorRange.text = getString(R.string.lbl_air_quality_poor_index)
        airQualityVeryPoorRange.text = getString(R.string.lbl_air_quality_very_poor_index)
        
        carbonMonoxideGoodRange.text = getString(R.string.lbl_carbon_monoxide_good_range)
        carbonMonoxideFairRange.text = getString(R.string.lbl_carbon_monoxide_fair_range)
        carbonMonoxideModerateRange.text = getString(R.string.lbl_carbon_monoxide_moderate_range)
        carbonMonoxidePoorRange.text = getString(R.string.lbl_carbon_monoxide_poor_range)
        carbonMonoxideVeryPoorRange.text = getString(R.string.lbl_carbon_monoxide_very_poor_range)
        
        nitrogenMonoxideGoodRange.text = getString(R.string.lbl_nitrogen_monoxide_good_range)
        nitrogenMonoxideFairRange.text = getString(R.string.lbl_nitrogen_monoxide_fair_range)
        nitrogenMonoxideModerateRange.text = getString(R.string.lbl_nitrogen_monoxide_moderate_range)
        nitrogenMonoxidePoorRange.text = getString(R.string.lbl_nitrogen_monoxide_poor_range)
        nitrogenMonoxideVeryPoorRange.text = getString(R.string.lbl_nitrogen_monoxide_very_poor_range)
        
        nitrogenDioxideGoodRange.text = getString(R.string.lbl_nitrogen_dioxide_good_range)
        nitrogenDioxideFairRange.text = getString(R.string.lbl_nitrogen_dioxide_fair_range)
        nitrogenDioxideModerateRange.text = getString(R.string.lbl_nitrogen_dioxide_moderate_range)
        nitrogenDioxidePoorRange.text = getString(R.string.lbl_nitrogen_dioxide_poor_range)
        nitrogenDioxideVeryPoorRange.text = getString(R.string.lbl_nitrogen_dioxide_very_poor_range)
        
        ozoneGoodRange.text = getString(R.string.lbl_ozone_good_range)
        ozoneFairRange.text = getString(R.string.lbl_ozone_fair_range)
        ozoneModerateRange.text = getString(R.string.lbl_ozone_moderate_range)
        ozonePoorRange.text = getString(R.string.lbl_ozone_poor_range)
        ozoneVeryPoorRange.text = getString(R.string.lbl_ozone_very_poor_range)

        sulphurDioxideGoodRange.text = getString(R.string.lbl_sulphur_dioxide_good_range)
        sulphurDioxideFairRange.text = getString(R.string.lbl_sulphur_dioxide_fair_range)
        sulphurDioxideModerateRange.text = getString(R.string.lbl_sulphur_dioxide_moderate_range)
        sulphurDioxidePoorRange.text = getString(R.string.lbl_sulphur_dioxide_poor_range)
        sulphurDioxideVeryPoorRange.text = getString(R.string.lbl_sulphur_dioxide_very_poor_range)

        ammoniaGoodRange.text = getString(R.string.lbl_ammonia_good_range)
        ammoniaFairRange.text = getString(R.string.lbl_ammonia_fair_range)
        ammoniaModerateRange.text = getString(R.string.lbl_ammonia_moderate_range)
        ammoniaPoorRange.text = getString(R.string.lbl_ammonia_poor_range)
        ammoniaVeryPoorRange.text = getString(R.string.lbl_ammonia_very_poor_range)

        fineParticlesGoodRange.text = getString(R.string.lbl_fine_particles_good_range)
        fineParticlesFairRange.text = getString(R.string.lbl_fine_particles_fair_range)
        fineParticlesModerateRange.text = getString(R.string.lbl_fine_particles_moderate_range)
        fineParticlesPoorRange.text = getString(R.string.lbl_fine_particles_poor_range)
        fineParticlesVeryPoorRange.text = getString(R.string.lbl_fine_particles_very_poor_range)

        coarseParticlesGoodRange.text = getString(R.string.lbl_coarse_particles_good_range)
        coarseParticlesFairRange.text = getString(R.string.lbl_coarse_particles_fair_range)
        coarseParticlesModerateRange.text = getString(R.string.lbl_coarse_particles_moderate_range)
        coarseParticlesPoorRange.text = getString(R.string.lbl_coarse_particles_poor_range)
        coarseParticlesVeryPoorRange.text = getString(R.string.lbl_coarse_particles_very_poor_range)
    }

    private fun setCardValues(airPollution: AirPollution) {
        val airQuality = AirQualityEnum.getQualityByValue(airPollution.airQualityIndex)
        airQualityValue.text = getString(
            R.string.lbl_quality_card_current_index,
            airPollution.airQualityIndex,
            getString(airQuality.qualityStringId)

        )

        val carbonMonoxide = CarbonMonoxideEnum.getQualityByValue(airPollution.carbonMonoxide)
        carbonMonoxideValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.carbonMonoxide,
            getString(carbonMonoxide.qualityStringId)

        )

        val nitrogenMonoxide = NitrogenMonoxideEnum.getQualityByValue(airPollution.nitrogenMonoxide)
        nitrogenMonoxideValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.nitrogenMonoxide,
            getString(nitrogenMonoxide.qualityStringId)

        )

        val nitrogenDioxide = NitrogenDioxideEnum.getQualityByValue(airPollution.nitrogenDioxide)
        nitrogenDioxideValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.nitrogenDioxide,
            getString(nitrogenDioxide.qualityStringId)

        )

        val ozone = OzoneEnum.getQualityByValue(airPollution.ozone)
        ozoneValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.ozone,
            getString(ozone.qualityStringId)

        )

        val sulphurDioxide = SulphurDioxideEnum.getQualityByValue(airPollution.sulphurDioxide)
        sulphurDioxideValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.sulphurDioxide,
            getString(sulphurDioxide.qualityStringId)

        )

        val ammonia = AmmoniaEnum.getQualityByValue(airPollution.ammonia)
        ammoniaValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.ammonia,
            getString(ammonia.qualityStringId)

        )

        val fineParticles = FineParticlesEnum.getQualityByValue(airPollution.fineParticles)
        fineParticlesValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.fineParticles,
            getString(fineParticles.qualityStringId)

        )

        val coarseParticles = CoarseParticlesEnum.getQualityByValue(airPollution.coarseParticles)
        coarseParticlesValue.text = getString(
            R.string.lbl_quality_card_current_value,
            airPollution.coarseParticles,
            getString(coarseParticles.qualityStringId)

        )
    }
}
