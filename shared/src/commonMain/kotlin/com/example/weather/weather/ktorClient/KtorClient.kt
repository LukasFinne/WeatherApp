package com.example.weather.weather.ktorClient

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory object for creating configured Ktor HTTP clients.
 *
 * This object provides a centralized way to create HTTP clients with consistent configuration
 * including JSON serialization, logging, caching, and error handling settings.
 */
object KtorClient {
    /**
     * Creates a configured HTTP client with JSON serialization, logging, and caching support.
     *
     * The client is configured with:
     * - JSON content negotiation with support for unknown keys
     * - INFO level logging for debugging purposes
     * - HTTP response caching
     * - Strict success validation (throws exceptions for non-2xx responses)
     *
     * @param engine The HTTP engine to use (defaults to OkHttp)
     * @return A configured HttpClient instance ready for API calls
     */
    fun createClient(engine: HttpClientEngine = OkHttp.create()): HttpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
        install(Logging) {
            level = LogLevel.INFO
        }
        install(HttpCache.Companion)
        expectSuccess = true
    }
}
