package com.example.homeassistant.ui.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homeassistant.R
import com.example.homeassistant.domain.WeatherRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherRecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val weatherRecordDate = view.findViewById<TextView>(R.id.weather_record_date)
    private val mainWeatherField = view.findViewById<TextView>(R.id.main_weather_field)
    private val descriptionField = view.findViewById<TextView>(R.id.description_field)
    private val temperatureField = view.findViewById<TextView>(R.id.temperature_field)
    private val feelsLikeField = view.findViewById<TextView>(R.id.feels_like_field)
    private val minTemperatureField = view.findViewById<TextView>(R.id.min_temperature_field)
    private val maxTemperatureField = view.findViewById<TextView>(R.id.max_temperature_field)
    private val pressureField = view.findViewById<TextView>(R.id.pressure_field)
    private val humidityField = view.findViewById<TextView>(R.id.humidity_field)
    private val visibilityField = view.findViewById<TextView>(R.id.visibility_field)
    private val windSpeedField = view.findViewById<TextView>(R.id.wind_speed_field)
    private val cloudinessField = view.findViewById<TextView>(R.id.cloudiness_field)
    private val cityNameField = view.findViewById<TextView>(R.id.city_name_field)
    private val sunriseField = view.findViewById<TextView>(R.id.sunrise_field)
    private val sunsetField = view.findViewById<TextView>(R.id.sunset_field)

    fun bind(weatherRecord: WeatherRecord) {
        val dateFormatter = SimpleDateFormat(
            view.context.getString(R.string.lbl_record_date_format),
            Locale.ENGLISH
        )

        val timeFormatter = SimpleDateFormat(
            view.context.getString(weatherRecord.hourFormatStringId),
            Locale.ENGLISH
        )

        val date = dateFormatter.format(Date(weatherRecord.weather.dateTime * 1000))

        val sunriseTimeFormatted = timeFormatter.format(
            Date(weatherRecord.weather.sunriseTime * 1000)
        )

        val sunsetTimeFormatted = timeFormatter.format(
            Date(weatherRecord.weather.sunsetTime * 1000)
        )

        val temperatureFormatted = view.context.getString(
            weatherRecord.temperatureValueStringId,
            weatherRecord.weather.temperature
        )

        val feelsLikeFormatted = view.context.getString(
            weatherRecord.temperatureValueStringId,
            weatherRecord.weather.feelsLike
        )

        val minTemperatureFormatted = view.context.getString(
            weatherRecord.temperatureValueStringId,
            weatherRecord.weather.minTemperature
        )

        val maxTemperatureFormatted = view.context.getString(
            weatherRecord.temperatureValueStringId,
            weatherRecord.weather.maxTemperature
        )

        val pressureFormatted = view.context.getString(
            weatherRecord.pressureValueStringId,
            weatherRecord.weather.pressure
        )

        val windSpeedFormatted = view.context.getString(
            weatherRecord.speedValueStringId,
            weatherRecord.weather.windSpeed
        )

        weatherRecordDate.text = date
        mainWeatherField.text = weatherRecord.weather.mainWeather
        descriptionField.text = weatherRecord.weather.description.replaceFirstChar(Char::titlecase)
        temperatureField.text = view.context.getString(
            R.string.lbl_card_field_temperature,
            temperatureFormatted
        )
        feelsLikeField.text = view.context.getString(
            R.string.lbl_card_field_feels_like,
            feelsLikeFormatted
        )
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
            R.string.lbl_card_field_humidity,
            weatherRecord.weather.humidity
        )
        visibilityField.text = view.context.getString(
            R.string.lbl_card_field_visibility,
            weatherRecord.weather.visibility
        )
        windSpeedField.text = view.context.getString(
            R.string.lbl_card_field_wind_speed,
            windSpeedFormatted
        )
        cloudinessField.text = view.context.getString(
            R.string.lbl_card_field_cloudiness,
            weatherRecord.weather.cloudiness
        )
        cityNameField.text = view.context.getString(
            R.string.lbl_card_field_city,
            weatherRecord.weather.cityName
        )
        sunriseField.text = view.context.getString(
            R.string.lbl_card_field_sunrise,
            sunriseTimeFormatted
        )
        sunsetField.text = view.context.getString(
            R.string.lbl_card_field_sunset,
            sunsetTimeFormatted
        )
    }
}
