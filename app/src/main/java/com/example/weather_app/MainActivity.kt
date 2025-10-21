package com.example.weather_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_app.ui.theme.Weather_AppTheme

class MainActivity : ComponentActivity() {
    private val vm: WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Weather_AppTheme {
                val ui by vm.uiState.collectAsState()

                WeatherScreen(
                    conditionTitle = ui.conditionTitle,
                    location = ui.location,
                    temperature = ui.temperature,
                    minMax = ui.minMax,
                    isLoading = ui.isLoading,
                    error = ui.error,
                    forecast = ui.forecast,
                    weatherCodeTitle = { vm.weatherCodeTitle(it) },
                    getDayName = { vm.getDayName(it) },
                    currentCity = ui.currentCity,
                    availableCities = ui.availableCities,
                    onCitySelected = { city -> vm.selectCity(city) }
                )
            }
        }
    }
}

@Composable
fun WeatherScreen(
    conditionTitle: String,
    location: String,
    temperature: String,
    minMax: String,
    isLoading: Boolean,
    error: String?,
    forecast: List<DayForecast> = emptyList(),
    weatherCodeTitle: (Int?) -> String = { "" },
    getDayName: (String) -> String = { "" },
    currentCity: City? = null,
    availableCities: List<City> = emptyList(),
    onCitySelected: (City) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundImage = painterResource(currentCity?.backgroundDrawable ?: R.drawable.zurich)
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp)
        ) {
            when {
                isLoading -> {
                    Text(
                        text = "Lade Wetter …",
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                error != null -> {
                    Text(
                        text = error,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(2.dp),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    Text(
                        text = conditionTitle,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        modifier = Modifier.padding(2.dp)
                    )

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            modifier = Modifier
                                .clickable { expanded = true }
                                .padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = location,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "▼",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            availableCities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city.name) },
                                    onClick = {
                                        onCitySelected(city)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        text = temperature,
                        fontSize = 96.sp,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )

                    Text(
                        text = minMax,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(2.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        forecast.forEach { day ->
                            ForecastItem(
                                day = day,
                                weatherCodeTitle = weatherCodeTitle,
                                getDayName = getDayName
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(
    day: DayForecast, weatherCodeTitle: (Int?) -> String, getDayName: (String) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getDayName(day.date),
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = weatherCodeTitle(day.weatherCode),
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "↓${day.tempMin}°", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "↑${day.tempMax}°", fontSize = 14.sp, color = Color.White
            )
        }
    }
}