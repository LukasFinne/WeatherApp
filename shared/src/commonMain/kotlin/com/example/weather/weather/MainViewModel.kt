package com.example.weather.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.weather.weather.ktorClient.KtorClient
import com.example.weather.weather.models.WeatherDataState
import com.example.weather.weather.repository.declaration.WeatherRepository
import com.example.weather.weather.repository.implementation.WeatherImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sealed class representing different UI states for the weather feature.
 *
 * This provides a type-safe way to manage UI state transitions and ensure
 * the UI responds appropriately to different application states.
 */
sealed class WeatherState {
    data object Initial : WeatherState()

    data object Loading : WeatherState()

    data object ValidationError : WeatherState()

    data class Loaded(val weatherData: WeatherDataState) : WeatherState()
}

/**
 * ViewModel responsible for managing weather-related UI state and coordinating weather data requests.
 *
 * This class handles user interactions, input validation, and orchestrates calls to the weather repository.
 * It maintains the current UI state and ensures proper state transitions during weather data fetching.
 *
 * @param repository The weather repository for fetching weather data and coordinates
 */
class MainViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _state = MutableStateFlow<WeatherState>(WeatherState.Initial)
    val state = _state

    /**
     * Fetches weather data for the specified city.
     *
     * This method validates the city input, updates the UI state to loading,
     * and makes a request to the weather repository. The UI state is updated
     * based on the validation result and API response.
     *
     * @param city The name of the city to get weather data for
     */
    fun getWeatherForCity(city: String) {
        viewModelScope.launch {
            _state.update { WeatherState.Loading }

            val cleanedCity = city.trimEnd()

            val isValidCity = cleanedCity.isNotEmpty() && cleanedCity.all { it.isLetter() || it.isWhitespace() }

            if (!isValidCity) {
                _state.update { WeatherState.ValidationError }
            } else {
                val weatherData = repository.getWeatherByCity(cleanedCity)
                _state.update { WeatherState.Loaded(weatherData) }
            }
        }
    }
}

val mainViewModelFactory = viewModelFactory {
    initializer {
        MainViewModel(weatherRepository())
    }
}

fun weatherRepository(): WeatherRepository = WeatherImpl(KtorClient.createClient())
