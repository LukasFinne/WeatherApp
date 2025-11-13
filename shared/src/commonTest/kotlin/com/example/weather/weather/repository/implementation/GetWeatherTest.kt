package com.example.weather.weather.repository.implementation

import com.example.weather.weather.ktorClient.KtorClient
import com.example.weather.weather.ktorClient.models.NetworkError
import com.example.weather.weather.ktorClient.models.Result
import com.example.weather.weather.models.weatherYrNoJSON
import com.example.weather.weather.repository.declaration.WeatherRepository
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit test class for testing weather data retrieval functionality.
 *
 * This class tests the WeatherRepository implementation with mocked HTTP responses
 * to ensure proper handling of various API response scenarios including success,
 * error conditions, and edge cases.
 */
class GetWeatherTest {
    private lateinit var weatherRepository: WeatherRepository

    /**
     * Tests that `getWeather` successfully returns weather data when provided with valid coordinates.
     * It mocks a successful API response and verifies that the result is a `Result.Success`
     * with the expected temperature data.
     */
    @Test
    fun `getWeather returns weather data for valid coordinates`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel(weatherYrNoJSON),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherCoordinates("52.5200", "13.4050")
        assertTrue(result is Result.Success, "Expected success result")
        assertEquals(
            15.5,
            result.data
                ?.properties
                ?.timeSeries[0]
                ?.data
                ?.instant
                ?.details
                ?.airTemperature,
        )
    }

    /**
     * Tests that `getWeather` returns a `ServerResponseException` when the weather API
     * responds with a 503 Service Unavailable status code. This ensures server-side errors are handled correctly.
     */
    @Test
    fun `getWeather returns ServerResponseException error when weather API returns status code 503`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel("Service Unavailable"),
                        status = HttpStatusCode.ServiceUnavailable,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherCoordinates("52.5200", "13.4050")
        assertTrue(result is Result.Error, "Expected error result")
        assertTrue(result.error is NetworkError.ServerResponseException, "Expected ServerResponseException")
    }

    /**
     * Tests that `getWeather` returns a `ClientRequestException` when the weather API
     * responds with a 400 Bad Request status code. This ensures client-side errors are handled correctly.
     */
    @Test
    fun `getWeather returns ClientRequestException error when weather API returns status code 400`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel("Bad Request"),
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherCoordinates("52.5200", "13.4050")
        assertTrue(result is Result.Error, "Expected error result")
        assertTrue(result.error is NetworkError.ClientRequestException, "Expected ClientRequestException")
    }

    /**
     * Tests that `getWeather` handles a 204 No Content response from the weather API.
     * In this case, the API returns no data, and the test verifies that the result is a `Result.Success`
     * with null data, indicating that no weather information was available but the request was not an error.
     */
    @Test
    fun `getWeather returns parsing error when weather API returns status code 204`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel(""),
                        status = HttpStatusCode.NoContent,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherCoordinates("52.5200", "13.4050")
        assertTrue(result is Result.Success, "Expected sucess result")
        // NoContent with empty body will likely cause a parsing error
        assertEquals(null, result.data)
    }
}
