package com.example.weather.weather.repository.implementation
import com.example.weather.weather.ktorClient.models.NetworkError
import com.example.weather.weather.ktorClient.models.Result
import com.example.weather.weather.ktorClient.safeApiCall
import com.example.weather.weather.models.CityWeather
import com.example.weather.weather.models.Coordinates
import com.example.weather.weather.models.WeatherData
import com.example.weather.weather.models.WeatherDataState
import com.example.weather.weather.repository.declaration.WeatherRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

/**
 * Concrete implementation of the WeatherRepository interface using HTTP APIs.
 *
 * This class handles communication with external weather and geocoding APIs:
 * - MET Norway API for weather data
 * - OpenStreetMap Nominatim API for geocoding (city name to coordinates)
 *
 * It provides a complete workflow to fetch weather data for a given city name,
 * including coordinate resolution and error handling.
 *
 * @param client The HTTP client used for making API requests
 */
class WeatherImpl(private val client: HttpClient) : WeatherRepository {
    companion object {
        private const val WEATHER_API_BASE_URL = "https://api.met.no/weatherapi/locationforecast/2.0"
        private const val GEOCODING_API_BASE_URL = "https://nominatim.openstreetmap.org"
    }

    override suspend fun getWeatherCoordinates(lat: String, lon: String): Result<WeatherData?, NetworkError> =
        safeApiCall {
            client
                .get(
                    "$WEATHER_API_BASE_URL/compact?lat=$lat&lon=$lon",
                ).takeIf { it.status != HttpStatusCode.NoContent }
                ?.body<WeatherData>()
        }

    override suspend fun getCoordinatesByCity(city: String): Result<List<Coordinates>?, NetworkError> = safeApiCall {
        client
            .get("$GEOCODING_API_BASE_URL/search?city=$city&format=jsonv2")
            .body<List<Coordinates>>()
    }

    @Suppress("ReturnCount")
    override suspend fun getWeatherByCity(city: String): WeatherDataState {
        val coordinatesResult = getCoordinatesByCity(city)

        val coordinatesList = when (coordinatesResult) {
            is Result.Success -> coordinatesResult.data ?: listOf()
            is Result.Error -> return mapNetworkErrorToWeatherDataState(coordinatesResult.error)
        }

        val coordinates = coordinatesList.firstOrNull() ?: return WeatherDataState.CoordinatesIsEmpty

        val weatherResult = getWeatherCoordinates(coordinates.lat, coordinates.lon)

        val weatherData = when (weatherResult) {
            is Result.Success -> weatherResult.data
            is Result.Error -> return mapNetworkErrorToWeatherDataState(weatherResult.error)
        }

        if (weatherData == null || weatherData.properties.timeSeries.isEmpty()) {
            return WeatherDataState.NoWeatherData
        }

        val timeSeries = weatherData.properties.timeSeries[0]

        return WeatherDataState.Success(
            CityWeather(
                temperature = timeSeries.data.instant.details.airTemperature ?: 0.0,
                windSpeed = timeSeries.data.instant.details.windSpeed ?: 0.0,
                summary = timeSeries.data.nextOneHours
                    ?.summary
                    ?.symbolCode
                    ?: "unknown",
            ),
        )
    }

    private fun mapNetworkErrorToWeatherDataState(error: NetworkError): WeatherDataState = when (error) {
        is NetworkError.NoInternetConnection -> WeatherDataState.NoInternetConnection
        is NetworkError.ClientRequestException -> WeatherDataState.ClientError
        is NetworkError.ServerResponseException -> WeatherDataState.ServerError
        is NetworkError.UnknownError -> WeatherDataState.Error
        is NetworkError.NoTransformationFoundException -> WeatherDataState.NoTransformationFoundException
    }
}
