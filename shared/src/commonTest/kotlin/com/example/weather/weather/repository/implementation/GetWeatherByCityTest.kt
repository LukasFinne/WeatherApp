package com.example.weather.weather.repository.implementation

import com.example.weather.weather.ktorClient.KtorClient
import com.example.weather.weather.models.WeatherDataState
import com.example.weather.weather.models.noLonAndLanOpenStreetJSONData
import com.example.weather.weather.models.noWeatherInfoYrNoJSON
import com.example.weather.weather.models.openStreetJSONData
import com.example.weather.weather.models.timeSeriesIsEmptyYrNoJSON
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
 * Unit test class for testing the complete weather-by-city workflow.
 *
 * This class tests the end-to-end process of fetching weather data for a city,
 * which includes geocoding (city name to coordinates) and weather data retrieval.
 * It uses mocked HTTP responses to test various scenarios including successful
 * requests, missing data, and error conditions.
 */
class GetWeatherByCityTest {
    private lateinit var weatherRepository: WeatherRepository

    /**
     * Tests the successful retrieval of weather data for a valid city.
     * This test mocks both the geocoding and weather APIs to return successful responses
     * and verifies that the resulting weather data is correct.
     */
    @Test
    fun `getWeatherByCity returns success when city exists and weather data is available`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
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

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertTrue(result is WeatherDataState.Success)
        assertEquals(15.5, result.weatherData.temperature)
        assertEquals(3.2, result.weatherData.windSpeed)
        assertEquals("partlycloudy_day", result.weatherData.summary)
    }

    /**
     * Tests the case where the weather API returns a response with no weather information.
     * The test expects an `Error` state, ensuring that the absence of weather data is handled as an error.
     */
    @Test
    fun `getWeatherByCity returns error when there is no weather data`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel(noWeatherInfoYrNoJSON),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertTrue(result is WeatherDataState.Error)
    }

    /**
     * Tests the case where the geocoding API returns a response without latitude and longitude.
     * This test ensures that if coordinates are missing, the process results in an `Error` state.
     */
    @Test
    fun `getWeatherByCity returns error when there is no coords data`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(noLonAndLanOpenStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
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

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertTrue(result is WeatherDataState.Error)
    }

    /**
     * Tests the scenario where the geocoding API cannot find the specified city.
     * It expects a `CoordinatesIsEmpty` state, indicating that the city name could not be resolved to coordinates.
     */
    @Test
    fun `getWeatherByCity returns coordinates empty when city not found`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel("[]"),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherByCity("NonExistentCity")
        assertEquals(WeatherDataState.CoordinatesIsEmpty, result)
    }

    /**
     * Tests how the system handles a `NoContent` (204) response from the weather API.
     * This test verifies that such a response is correctly interpreted as `NoWeatherData`.
     */
    @Test
    fun `getWeatherByCity returns NoWeatherData when weather data is empty`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel("No Content"),
                        status = HttpStatusCode.NoContent,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherByCity("NonExistentCity")
        assertEquals(WeatherDataState.NoWeatherData, result)
    }

    /**
     * Tests the case where the weather API returns data but the `timeSeries` list is empty.
     * This is a critical data point, and its absence should result in a `NoWeatherData` state.
     */
    @Test
    fun `getWeatherByCity returns NoWeatherData when weather data timeSeries list is empty`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel(timeSeriesIsEmptyYrNoJSON),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherByCity("NonExistentCity")
        assertEquals(WeatherDataState.NoWeatherData, result)
    }

    /**
     * Tests the handling of a server error (5xx) from the weather API.
     * The test ensures that a server-side issue at the weather provider is correctly mapped to a `ServerError` state.
     */
    @Test
    fun `getWeatherByCity returns serverError when weather API has server error`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
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

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertEquals(WeatherDataState.ServerError, result)
    }

    /**
     * Tests the handling of a client error (4xx) from the weather API.
     * This verifies that client-side errors, such as a bad request, are correctly mapped to a `ClientError` state.
     */
    @Test
    fun `getWeatherByCity returns ClientError correct state when weather API has client error`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                request.url.toString().contains("api.met.no") -> {
                    respond(
                        content = ByteReadChannel("Internal Server Error"),
                        status = HttpStatusCode.BadRequest,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertEquals(WeatherDataState.ClientError, result)
    }

    /**
     * Tests the handling of a server error (5xx) from the geocoding API.
     * This ensures that if the geocoding service is down, the failure is gracefully handled as a `ServerError`.
     */
    @Test
    fun `getWeatherByCity returns server error when coordinates API has server error`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel("Internal Server Error"),
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "text/plain"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertEquals(WeatherDataState.ServerError, result)
    }

    /**
     * Tests the handling of a client error (4xx) from the geocoding API.
     * This test verifies that an invalid request to the geocoding service is handled as a `ClientError`.
     */
    @Test
    fun `getWeatherByCity returns client error when coordinates API has client error`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
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

        val result = weatherRepository.getWeatherByCity("Berlin")
        assertEquals(WeatherDataState.ClientError, result)
    }
}
