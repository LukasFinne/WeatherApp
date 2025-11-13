package com.example.weather.weather.repository.implementation

import com.example.weather.weather.ktorClient.KtorClient
import com.example.weather.weather.ktorClient.models.NetworkError
import com.example.weather.weather.ktorClient.models.Result
import com.example.weather.weather.models.openStreetJSONData
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
 * Test suite for [WeatherRepository.getCoordinates].
 *
 * This class tests the behavior of the `getCoordinates` method in the [WeatherImpl] repository implementation.
 * It covers success scenarios, handling of non-existent cities, and various network error conditions.
 */
class GetCoordinatesTest {
    private lateinit var weatherRepository: WeatherRepository

    /**
     * Tests that `getCoordinates` successfully returns a list of coordinates for a valid city name.
     * It uses a mock engine to simulate a successful API response from the geocoding service.
     */
    @Test
    fun `getCoordinates returns list of coordinates for valid city`() = runTest {
        val mockEngine = MockEngine { request ->
            when {
                request.url.toString().contains("nominatim") -> {
                    respond(
                        content = ByteReadChannel(openStreetJSONData),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }

        weatherRepository = WeatherImpl(KtorClient.createClient(mockEngine))

        val result = weatherRepository.getCoordinatesByCity("Berlin")
        assertTrue(result is Result.Success, "Expected success result")
        assertEquals("52.5200", result.data?.first()?.lat)
        assertEquals("13.4050", result.data?.first()?.lon)
    }

    /**
     * Tests that `getCoordinates` returns an empty list when the provided city name is not found.
     * The mock engine simulates an API response with an empty JSON array.
     */
    @Test
    fun `getCoordinates returns empty list when city not found`() = runTest {
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

        val result = weatherRepository.getCoordinatesByCity("NonExistentCity")
        assertTrue(result is Result.Success, "Expected success result")
        assertEquals(result.data?.isEmpty(), true, "Expected empty list for non-existent city")
    }

    /**
     * Tests that `getCoordinates` returns a [NetworkError.ServerResponseException] when the geocoding API
     * returns an HTTP 500 Internal Server Error.
     */
    @Test
    fun `getCoordinates returns ServerResponseException error when geocoding API returns status code 500`() = runTest {
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

        val result = weatherRepository.getCoordinatesByCity("Berlin")
        assertTrue(result is Result.Error, "Expected error result")
        assertTrue(result.error is NetworkError.ServerResponseException, "Expected ServerResponseException")
    }

    /**
     * Tests that `getCoordinates` returns a [NetworkError.ClientRequestException] when the geocoding API
     * returns an HTTP 400 Bad Request error.
     */
    @Test
    fun `getCoordinates returns ClientRequestException error when geocoding API returns status code 400`() = runTest {
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

        val result = weatherRepository.getCoordinatesByCity("Berlin")
        assertTrue(result is Result.Error, "Expected error result")
        assertTrue(result.error is NetworkError.ClientRequestException, "Expected ClientRequestException")
    }
}
