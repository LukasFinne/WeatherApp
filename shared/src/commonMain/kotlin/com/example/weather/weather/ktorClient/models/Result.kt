package com.example.weather.weather.ktorClient.models

/**
 * A discriminated union that represents the result of an operation that can either succeed or fail.
 *
 * This sealed class provides a type-safe way to handle operations that might fail,
 * eliminating the need for null checks or exception handling at the call site.
 *
 * @param T The type of the success value
 * @param E The type of the error value
 */
sealed class Result<out T, out E> {
    /**
     * Represents a successful operation result containing the expected data.
     *
     * @param T The type of the success data
     * @param data The successful result data
     */
    data class Success<out T>(val data: T) : Result<T, Nothing>()

    /**
     * Represents a failed operation result containing error information.
     *
     * @param E The type of the error data
     * @param error The error information describing what went wrong
     */
    data class Error<out E>(val error: E) : Result<Nothing, E>()
}
