package com.example.homeassistant.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.homeassistant.R
import com.example.homeassistant.datasource.PhonePermissionDataSource
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.domain.settings.PressureType
import com.example.homeassistant.domain.settings.SpeedType
import com.example.homeassistant.domain.settings.TemperatureType
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.example.homeassistant.utils.CELSIUS_TYPE
import com.example.homeassistant.utils.FAHRENHEIT_TYPE
import com.example.homeassistant.utils.HPA_TYPE
import com.example.homeassistant.utils.IN_HG_TYPE
import com.example.homeassistant.utils.KELVIN_TYPE
import com.example.homeassistant.utils.KILOMETERS_PER_HOUR_TYPE
import com.example.homeassistant.utils.KNOTS_TYPE
import com.example.homeassistant.utils.KPA_TYPE
import com.example.homeassistant.utils.METERS_PER_SECOND_TYPE
import com.example.homeassistant.utils.MILES_PER_HOUR_TYPE
import com.example.homeassistant.utils.MM_HG_TYPE


class SettingsFragment : Fragment() {
    private lateinit var hourFormatTab: ConstraintLayout
    private lateinit var temperatureTab: ConstraintLayout
    private lateinit var speedTab: ConstraintLayout
    private lateinit var pressureTab: ConstraintLayout
    private lateinit var notificationsTab: ConstraintLayout

    private lateinit var hourFormatSwitch: SwitchCompat
    private lateinit var temperatureOption: TextView
    private lateinit var speedOption: TextView
    private lateinit var pressureOption: TextView
    private lateinit var notificationsSwitch: SwitchCompat

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.SettingsViewModelFactory(
            SettingsRepository(SettingsDataSource(requireContext()))
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hourFormatTab = view.findViewById(R.id.hour_format_tab)
        temperatureTab = view.findViewById(R.id.temperature_tab)
        speedTab = view.findViewById(R.id.speed_tab)
        pressureTab = view.findViewById(R.id.pressure_tab)
        notificationsTab = view.findViewById(R.id.notifications_tab)

        hourFormatSwitch = hourFormatTab.findViewById(R.id.settings_switch)
        temperatureOption = temperatureTab.findViewById(R.id.settings_tab_option)
        speedOption = speedTab.findViewById(R.id.settings_tab_option)
        pressureOption = pressureTab.findViewById(R.id.settings_tab_option)
        notificationsSwitch = notificationsTab.findViewById(R.id.settings_switch)

        initializeViews()
        setClickListeners()

        settingsViewModel.getSettings().observe(viewLifecycleOwner) { settings ->
            val temperatureType = TemperatureType.getByItemType(settings.temperatureUnit)
            val speedType = SpeedType.getByItemType(settings.speedUnit)
            val pressureType = PressureType.getByItemType(settings.pressureUnit)

            hourFormatSwitch.isChecked = settings.amPmHourFormat
            temperatureOption.text = getString(temperatureType.stringId)
            speedOption.text = getString(speedType.stringId)
            pressureOption.text = getString(pressureType.stringId)
            notificationsSwitch.isChecked = settings.showNotifications

        }
    }

    private fun initializeViews() {
        val hourTimeTitle = hourFormatTab.findViewById<TextView>(R.id.settings_tab_title)
        val hourTimeIcon = hourFormatTab.findViewById<ImageView>(R.id.settings_tab_icon)
        hourTimeTitle.text = getString(R.string.lbl_settings_tab_am_pm)
        hourTimeIcon.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_time
        )

        val temperatureTitle = temperatureTab.findViewById<TextView>(R.id.settings_tab_title)
        val temperatureIcon = temperatureTab.findViewById<ImageView>(R.id.settings_tab_icon)
        temperatureTitle.text = getString(R.string.lbl_settings_tab_temperature)
        temperatureIcon.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_temperature
        )

        val speedTitle = speedTab.findViewById<TextView>(R.id.settings_tab_title)
        val speedIcon = speedTab.findViewById<ImageView>(R.id.settings_tab_icon)
        speedTitle.text = getString(R.string.lbl_settings_tab_speed)
        speedIcon.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_speed
        )

        val pressureTitle = pressureTab.findViewById<TextView>(R.id.settings_tab_title)
        val pressureIcon = pressureTab.findViewById<ImageView>(R.id.settings_tab_icon)
        pressureTitle.text = getString(R.string.lbl_settings_tab_pressure)
        pressureIcon.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_pressure
        )

        val notificationsIcon = notificationsTab.findViewById<ImageView>(R.id.settings_tab_icon)
        val notificationsTitle = notificationsTab.findViewById<TextView>(R.id.settings_tab_title)
        notificationsTitle.text = getString(R.string.lbl_settings_tab_notifications)
        notificationsIcon.background = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_notifications
        )
    }

    private fun setClickListeners() {
        hourFormatSwitch.setOnClickListener {
            settingsViewModel.saveAmPamHourFormatToPreferenceStore(
                hourFormatSwitch.isChecked,
                requireContext()
            )
        }

        temperatureTab.setOnClickListener {
            val items = arrayOf(
                getString(TemperatureType.CELSIUS.stringId),
                getString(TemperatureType.FAHRENHEIT.stringId),
                getString(TemperatureType.KELVIN.stringId)
            )
            val checkedItem = items.indexOf(temperatureOption.text)
            showSettingsDialog(items, checkedItem, R.string.lbl_settings_tab_temperature)
        }

        speedTab.setOnClickListener {
            val items = arrayOf(
                getString(SpeedType.METERS_PER_SECOND.stringId),
                getString(SpeedType.KILOMETERS_PER_HOUR.stringId),
                getString(SpeedType.KNOTS.stringId)
            )
            val checkedItem = items.indexOf(speedOption.text)
            showSettingsDialog(items, checkedItem, R.string.lbl_settings_tab_speed)
        }

        pressureTab.setOnClickListener {
            val items = arrayOf(
                getString(PressureType.HPA.stringId),
                getString(PressureType.KPA.stringId),
                getString(PressureType.MM_HG.stringId),
                getString(PressureType.IN_HG.stringId)
            )
            val checkedItem = items.indexOf(pressureOption.text)
            showSettingsDialog(items, checkedItem, R.string.lbl_settings_tab_pressure)
        }

        notificationsSwitch.setOnClickListener {
            settingsViewModel.saveNotificationsToPreferenceStore(
                notificationsSwitch.isChecked,
                requireContext()
            )
        }
    }

    private fun showSettingsDialog(items: Array<String>, checkedItem: Int, titleId: Int) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialogStyle)
        alertDialogBuilder.setTitle(getString(titleId))
        alertDialogBuilder.setSingleChoiceItems(items, checkedItem) { dialog, option ->
            when (items[option]) {
                getString(TemperatureType.CELSIUS.stringId) -> {
                    settingsViewModel.saveTemperatureUnitToPreferenceStore(
                        CELSIUS_TYPE,
                        requireContext()
                    )
                }

                getString(TemperatureType.FAHRENHEIT.stringId) -> {
                    settingsViewModel.saveTemperatureUnitToPreferenceStore(
                        FAHRENHEIT_TYPE,
                        requireContext()
                    )
                }

                getString(TemperatureType.KELVIN.stringId) -> {
                    settingsViewModel.saveTemperatureUnitToPreferenceStore(
                        KELVIN_TYPE,
                        requireContext()
                    )
                }

                getString(SpeedType.METERS_PER_SECOND.stringId) -> {
                    settingsViewModel.saveSpeedUnitToPreferenceStore(
                        METERS_PER_SECOND_TYPE,
                        requireContext()
                    )
                }

                getString(SpeedType.KILOMETERS_PER_HOUR.stringId) -> {
                    settingsViewModel.saveSpeedUnitToPreferenceStore(
                        KILOMETERS_PER_HOUR_TYPE,
                        requireContext()
                    )
                }

                getString(SpeedType.MILES_PER_HOUR.stringId) -> {
                    settingsViewModel.saveSpeedUnitToPreferenceStore(
                        MILES_PER_HOUR_TYPE,
                        requireContext()
                    )
                }

                getString(SpeedType.KNOTS.stringId) -> {
                    settingsViewModel.saveSpeedUnitToPreferenceStore(
                        KNOTS_TYPE,
                        requireContext()
                    )
                }

                getString(PressureType.HPA.stringId) -> {
                    settingsViewModel.savePressureUnitToPreferenceStore(
                        HPA_TYPE,
                        requireContext()
                    )
                }

                getString(PressureType.KPA.stringId) -> {
                    settingsViewModel.savePressureUnitToPreferenceStore(
                        KPA_TYPE,
                        requireContext()
                    )
                }

                getString(PressureType.MM_HG.stringId) -> {
                    settingsViewModel.savePressureUnitToPreferenceStore(
                        MM_HG_TYPE,
                        requireContext()
                    )
                }

                getString(PressureType.IN_HG.stringId) -> {
                    settingsViewModel.savePressureUnitToPreferenceStore(
                        IN_HG_TYPE,
                        requireContext()
                    )
                }
            }
            dialog.cancel()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
