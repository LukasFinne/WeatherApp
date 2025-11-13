package com.example.weather.weather.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weather.weather.WeatherState

/**
 * A Composable that provides a button to check the weather.
 *
 * @param weatherState The current state of the weather screen, used to control the enabled state of the button.
 * @param onClick A callback that is triggered when the button is clicked.
 */
@Composable
fun CheckWeatherButton(weatherState: WeatherState, onClick: () -> Unit) {
    Button(modifier = Modifier, enabled = weatherState !is WeatherState.Loading, onClick = onClick) {
        Text("Check weather")
    }
}
