package com.example.weather_app.ui.viewmodel

/**
 * UI-facing state for the Weather screen.
 * Keep it presentation-oriented (not the raw API model).
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val error: String = "",
    val title: String = "",            // e.g., "Partly Cloudy"
    val currentTemp: Double = 0.0,   // e.g., 12.3
    val minTemp: Double? = 0.0,       // today's min
    val maxTemp: Double? = 0.0        // today's max
)
