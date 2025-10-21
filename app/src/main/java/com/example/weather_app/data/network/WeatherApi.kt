package com.example.weather_app.data.network

import com.example.weather_app.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double = 47.37,
        @Query("longitude") longitude: Double = 8.55,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("timezone") timezone: String = "Europe/Berlin"
    ): WeatherResponse
}