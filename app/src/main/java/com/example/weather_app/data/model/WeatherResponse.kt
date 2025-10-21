package com.example.weather_app.data.model

/**
 * Minimal model matching https://api.open-meteo.com/v1/forecast
 */
data class WeatherResponse(
    val current_weather: CurrentWeather,
    val daily: Daily
)

data class CurrentWeather(
    val temperature: Double,
    val weathercode: Int
)

data class Daily(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)
