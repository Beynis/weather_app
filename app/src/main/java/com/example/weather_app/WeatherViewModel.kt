package com.example.weather_app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class City(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val backgroundDrawable: Int
)

data class DayForecast(
    val date: String,
    val weatherCode: Int,
    val tempMin: Int,
    val tempMax: Int
)

data class WeatherUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val conditionTitle: String = "",
    val location: String = "Zürich",
    val temperature: String = "--°",
    val minMax: String = "",
    val rawWeatherCode: Int? = null,
    val forecast: List<DayForecast> = emptyList(),
    val currentCity: City? = null,
    val availableCities: List<City> = emptyList()
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val cities = listOf(
        City("Zurich", 47.37, 8.55, R.drawable.zurich),
        City("New York", 40.7128, -74.0060, R.drawable.newyork)
    )

    private var selectedCity: City = cities[0]

    init {
        _uiState.value = _uiState.value.copy(
            availableCities = cities,
            currentCity = selectedCity
        )
        loadWeatherData()
    }

    fun loadWeatherData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val apiUrl = buildApiUrl(selectedCity)
                val json = withContext(Dispatchers.IO) { httpGet(apiUrl) }
                val parsed = parseWeather(json)
                _uiState.value = parsed.copy(
                    isLoading = false,
                    currentCity = selectedCity,
                    availableCities = cities
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = "goht nid ${e.message}"
                )
            }
        }
    }

    private fun buildApiUrl(city: City): String {
        return "https://api.open-meteo.com/v1/forecast?latitude=${city.latitude}&longitude=${city.longitude}&daily=temperature_2m_max,temperature_2m_min,weathercode&current_weather=true&timezone=Europe%2FBerlin"
    }

    fun selectCity(city: City) {
        selectedCity = city
        loadWeatherData()
    }

    private fun httpGet(urlStr: String): String {
        val url = URL(urlStr)
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
        }
        conn.inputStream.bufferedReader().use { br ->
            val body = br.readText()
            conn.disconnect()
            return body
        }
    }

    private fun parseWeather(jsonStr: String): WeatherUiState {
        val root = JSONObject(jsonStr)

        // current_weather
        val current = root.getJSONObject("current_weather")
        val tempNow = current.getDouble("temperature")
        val weatherCode = current.optInt("weathercode")

        // daily (heute: Index 0)
        val daily = root.getJSONObject("daily")
        val minArr = daily.getJSONArray("temperature_2m_min")
        val maxArr = daily.getJSONArray("temperature_2m_max")
        val weatherCodeArr = daily.getJSONArray("weathercode")
        val dateArr = daily.getJSONArray("time")
        val tMin = minArr.getDouble(0)
        val tMax = maxArr.getDouble(0)

        val conditionTitle = weatherCodeTitle(weatherCode)

        // Parse next 5 days (indices 0-4)
        val forecastList = mutableListOf<DayForecast>()
        for (i in 0 until minOf(5, dateArr.length())) {
            forecastList.add(
                DayForecast(
                    date = dateArr.getString(i),
                    weatherCode = weatherCodeArr.getInt(i),
                    tempMin = minArr.getDouble(i).toInt(),
                    tempMax = maxArr.getDouble(i).toInt()
                )
            )
        }

        return WeatherUiState(
            isLoading = false,
            error = null,
            conditionTitle = conditionTitle,
            location = selectedCity.name,
            temperature = "${tempNow.toInt()}°",
            minMax = "${tMin.toInt()}° / ${tMax.toInt()}°",
            rawWeatherCode = weatherCode,
            forecast = forecastList,
            currentCity = selectedCity,
            availableCities = cities
        )
    }

    fun weatherCodeTitle(weatherCode: Int?): String {
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
            else -> "unknown $weatherCode"
        }
    }

    fun getDayName(dateStr: String): String {
        return try {
            val date = LocalDate.parse(dateStr)
            date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        } catch (e: Exception) {
            dateStr
        }
    }
}