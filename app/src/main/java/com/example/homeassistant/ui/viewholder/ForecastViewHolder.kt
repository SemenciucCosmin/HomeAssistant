package com.example.homeassistant.ui.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.domain.Forecast
import com.example.homeassistant.ui.adapter.FiveDaysWeatherCallback
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ForecastViewHolder(private val view: View, private val callback: FiveDaysWeatherCallback) :
    RecyclerView.ViewHolder(view) {
    private val cardView = view.findViewById<CardView>(R.id.card_view)
    private val detailsLayout = view.findViewById<ConstraintLayout>(R.id.details_layout)
    private val detailsParentLayout = view.findViewById<FrameLayout>(R.id.details_parent_layout)
    private val dateTimeField = view.findViewById<TextView>(R.id.date_time)
    private val mainWeatherField = view.findViewById<TextView>(R.id.main_weather)
    private val descriptionField = view.findViewById<TextView>(R.id.weather_description)
    private val temperatureField = view.findViewById<TextView>(R.id.current_temperature)
    private val feelsLikeField = view.findViewById<TextView>(R.id.feels_like)
    private val minTemperatureField = view.findViewById<TextView>(R.id.min_temperature)
    private val maxTemperatureField = view.findViewById<TextView>(R.id.max_temperature)
    private val pressureField = view.findViewById<TextView>(R.id.current_pressure)
    private val humidityField = view.findViewById<TextView>(R.id.current_humidity)
    private val visibilityField = view.findViewById<TextView>(R.id.current_visibility)
    private val windSpeedField = view.findViewById<TextView>(R.id.current_wind_speed)
    private val cloudinessField = view.findViewById<TextView>(R.id.cloudiness)
    private val precipitationField = view.findViewById<TextView>(R.id.precipitation)

    fun bind(forecast: Forecast) {
        val dateFormat = view.context.getString(R.string.lbl_record_date_format)
        val timeFormat = view.context.getString(forecast.hourFormatStringId)
        val timeFormatter = SimpleDateFormat("$dateFormat, $timeFormat", Locale.ENGLISH)
        val dateTime = timeFormatter.format(Date(forecast.dateTime * 1000))

        val temperatureFormatted = view.context.getString(
            forecast.temperatureValueStringId,
            forecast.temperature
        )

        val feelsLikeFormatted = view.context.getString(
            forecast.temperatureValueStringId,
            forecast.feelsLike
        )

        val minTemperatureFormatted = view.context.getString(
            forecast.temperatureValueStringId,
            forecast.minTemperature
        )

        val maxTemperatureFormatted = view.context.getString(
            forecast.temperatureValueStringId,
            forecast.maxTemperature
        )

        val pressureFormatted = view.context.getString(
            forecast.pressureValueStringId,
            forecast.pressure
        )

        val windSpeedFormatted = view.context.getString(
            forecast.speedValueStringId,
            forecast.windSpeed
        )

        detailsLayout.isVisible = forecast.isExpanded
        dateTimeField.text = dateTime
        mainWeatherField.text = forecast.mainWeather
        descriptionField.text = forecast.description.replaceFirstChar(Char::titlecase)
        temperatureField.text = temperatureFormatted
        feelsLikeField.text = feelsLikeFormatted
        minTemperatureField.text = view.context.getString(
            R.string.lbl_card_field_min_temperature,
            minTemperatureFormatted
        )
        maxTemperatureField.text = view.context.getString(
            R.string.lbl_card_field_max_temperature,
            maxTemperatureFormatted
        )
        pressureField.text = view.context.getString(
            R.string.lbl_card_field_pressure,
            pressureFormatted
        )
        humidityField.text = view.context.getString(
            R.string.lbl_card_field_humidity, forecast.humidity
        )
        visibilityField.text = view.context.getString(
            R.string.lbl_card_field_visibility, forecast.visibility
        )
        windSpeedField.text = view.context.getString(
            R.string.lbl_card_field_wind_speed,
            windSpeedFormatted
        )
        cloudinessField.text = view.context.getString(
            R.string.lbl_card_field_cloudiness, forecast.cloudiness
        )
        precipitationField.text = view.context.getString(
            R.string.lbl_card_field_precipitation, forecast.precipitation
        )

        cardView.setOnClickListener {
            detailsLayout.isVisible = !forecast.isExpanded
            callback.onItemClick(forecast.copy(isExpanded = !forecast.isExpanded))
        }
    }
}
