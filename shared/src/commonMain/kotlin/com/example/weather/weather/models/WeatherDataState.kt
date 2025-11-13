package com.example.weather.weather.models

/**
 * Sealed class representing different states of weather data retrieval operations.
 *
 * This provides a type-safe way to handle the various outcomes when fetching weather data,
 * from successful results to different types of failures that can occur during the process.
 */
sealed class WeatherDataState {
    data class Success(val weatherData: CityWeather) : WeatherDataState()

    data object CoordinatesIsEmpty : WeatherDataState()

    data object NoWeatherData : WeatherDataState()

    data object ClientError : WeatherDataState()

    data object ServerError : WeatherDataState()

    data object Error : WeatherDataState()

    data object NoInternetConnection : WeatherDataState()

    data object NoTransformationFoundException : WeatherDataState()
}
