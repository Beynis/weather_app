package com.example.weather_app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_app.R
import com.example.weather_app.ui.theme.Weather_AppTheme

@Composable
fun WeatherScreen(
    conditionTitle: String,
    location: String,
    temperature: String
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val backgroundImage = painterResource(R.drawable.zurich)
        Image(
            painter = backgroundImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = conditionTitle,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(2.dp)
            )
            Text(
                text = location,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                modifier = Modifier.padding(2.dp)
            )
            Text(
                text = temperature,
                fontSize = 96.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherScreenPreview() {
    Weather_AppTheme {
        WeatherScreen(
            conditionTitle = stringResource(R.string.condiitonTitle),
            location = stringResource(R.string.location),
            temperature = stringResource(R.string.temperature)
        )
    }
}
