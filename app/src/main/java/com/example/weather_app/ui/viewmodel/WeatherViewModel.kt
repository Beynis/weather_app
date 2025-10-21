package com.example.weather_app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_app.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WeatherUiState(isLoading = true))
    val uiState: StateFlow<WeatherUiState> = _uiState

    init {
        loadWeatherData()
    }

    fun loadWeatherData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = "")
        viewModelScope.launch {
            try {
                // Retrofit call (defaults are set in your WeatherApi)
                val response = RetrofitInstance.api.getForecast()

                val currentTemp = response.current_weather.temperature
                val code = response.current_weather.weathercode
                val title = weatherCodeTitle(code)

                val minToday = response.daily.temperature_2m_min.firstOrNull()
                val maxToday = response.daily.temperature_2m_max.firstOrNull()

                _uiState.value = WeatherUiState(
                    isLoading = false,
                    title = title,
                    currentTemp = currentTemp,
                    minTemp = minToday,
                    maxTemp = maxToday
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    // Presentation mapper: WMO weather code -> short human text
    private fun weatherCodeTitle(weatherCode: Int?): String {
        return when (weatherCode) {
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2, 3 -> "Partly Cloudy"
            in 40..49 -> "Fog or Ice Fog"
            in 50..59 -> "Drizzle"
            in 60..69 -> "Rain"
            in 70..79 -> "Snow Fall"
            in 80..84 -> "Rain Showers"
            85, 86 -> "Snow Showers"
            87, 88 -> "Shower(s) of Snow Pellets"
            89, 90 -> "Hail"
            in 91..99 -> "Thunderstorm"
            else -> "Unknown $weatherCode"
        }
    }
}
