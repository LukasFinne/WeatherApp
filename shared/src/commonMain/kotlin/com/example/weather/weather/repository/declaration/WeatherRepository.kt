package com.example.weather.weather.repository.declaration

import com.example.weather.weather.ktorClient.models.NetworkError
import com.example.weather.weather.ktorClient.models.Result
import com.example.weather.weather.models.Coordinates
import com.example.weather.weather.models.WeatherData
import com.example.weather.weather.models.WeatherDataState

interface WeatherRepository {
    suspend fun getWeatherCoordinates(lat: String, lon: String): Result<WeatherData?, NetworkError>

    suspend fun getCoordinatesByCity(city: String): Result<List<Coordinates>?, NetworkError>

    suspend fun getWeatherByCity(city: String): WeatherDataState
}
