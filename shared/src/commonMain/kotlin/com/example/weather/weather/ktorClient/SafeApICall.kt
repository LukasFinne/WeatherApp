package com.example.weather.weather.ktorClient

import co.touchlab.kermit.Logger
import com.example.weather.weather.ktorClient.models.NetworkError
import com.example.weather.weather.ktorClient.models.Result
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.utils.io.CancellationException
import kotlinx.io.IOException

/**
 * Safely executes API calls and wraps them in a Result type to handle network errors.
 *
 * This function provides a centralized way to handle network exceptions and convert them
 * into domain-specific error types. It ensures that all network-related exceptions are
 * caught and logged appropriately while preserving cancellation semantics.
 *
 * @param T The expected return type of the API call
 * @param apiCall A suspend function that performs the actual API call
 * @return Result.Success<T> if the API call succeeds, or Result.Error<NetworkError> if it fails
 */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T, NetworkError> = try {
    Result.Success(apiCall())
} catch (e: CancellationException) {
    // Re-throw cancellation exceptions to preserve coroutine cancellation semantics
    throw e
} catch (e: IOException) {
    // Handle network connectivity issues (no internet, connection timeout, etc.)
    Logger.e(e) { "IOException" }
    Result.Error(NetworkError.NoInternetConnection)
} catch (e: ClientRequestException) {
    // Handle 4xx HTTP status codes (bad request, unauthorized, not found, etc.)
    Logger.e(e) { "ClientRequestException" }
    Result.Error(NetworkError.ClientRequestException)
} catch (e: ServerResponseException) {
    // Handle 5xx HTTP status codes (internal server error, service unavailable, etc.)
    Logger.e(e) { "ServerResponseException" }
    Result.Error(NetworkError.ServerResponseException)
} catch (e: NoTransformationFoundException) {
    // Handle cases where the response cannot be deserialized to the expected type
    // This typically occurs when the API response format doesn't match our data models
    Logger.e(e) { "NoTransformationFoundException" }
    Result.Error(NetworkError.NoTransformationFoundException)
} catch (e: Exception) {
    Logger.e(e) { "UnknownException" }
    Result.Error(NetworkError.UnknownError)
}
