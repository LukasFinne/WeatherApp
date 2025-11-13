package com.example.weather.weather

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.weather.components.CheckWeatherButton
import com.example.weather.weather.components.CityInput
import com.example.weather.weather.components.ErrorView
import com.example.weather.weather.components.LoadingView
import com.example.weather.weather.components.WeatherSuccessView
import com.example.weather.weather.models.WeatherDataState
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Main composable for the weather application.
 *
 * This composable displays the main UI of the application, including an input field for the city,
 * a button to check the weather, and the weather information.
 *
 * @param viewModel The view model for the main screen.
 */
@Composable
@Preview
fun App(
    viewModel: MainViewModel = viewModel(
        factory = mainViewModelFactory,
    ),
) {
    MaterialTheme {
        val weatherState by viewModel.state.collectAsStateWithLifecycle()
        var city by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        // Triggers when weatherState changes
        LaunchedEffect(weatherState) {
            if (weatherState is WeatherState.Initial || weatherState is WeatherState.Loaded) {
                focusRequester.requestFocus()
            }
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CityInput(
                    weatherState = weatherState,
                    city = city,
                    onValueChange = { city = it },
                    focusRequester = focusRequester,
                ) {
                    viewModel.getWeatherForCity(city)
                }

                CheckWeatherButton(weatherState) {
                    viewModel.getWeatherForCity(city)
                }
            }
            // Add transition animations between states
            Crossfade(targetState = weatherState) { weatherState ->
                when (val state = weatherState) {
                    WeatherState.Initial, WeatherState.ValidationError -> {}
                    is WeatherState.Loaded -> {
                        when (val data = state.weatherData) {
                            is WeatherDataState.Success -> WeatherSuccessView(weatherData = data.weatherData)

                            WeatherDataState.CoordinatesIsEmpty -> ErrorView(
                                "No coordinates found for the entered city.",
                            )

                            WeatherDataState.Error -> ErrorView("Something unexpected happened! Please try again!")

                            WeatherDataState.ClientError -> ErrorView("Client error! Please try again!")

                            WeatherDataState.ServerError -> ErrorView("Server error! Please try again!")
                            WeatherDataState.NoWeatherData -> ErrorView("No weather data found!")
                            WeatherDataState.NoInternetConnection -> ErrorView("No internet connection!")
                            WeatherDataState.NoTransformationFoundException -> ErrorView("Failed to deserialize data!")
                        }
                    }
                    WeatherState.Loading -> {
                        LoadingView()
                    }
                }
            }
        }
    }
}
