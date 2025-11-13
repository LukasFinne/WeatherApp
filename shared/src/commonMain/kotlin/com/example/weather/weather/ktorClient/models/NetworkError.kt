package com.example.weather.weather.ktorClient.models

/**
 * A sealed class representing different types of network errors that can occur during API operations.
 *
 * This provides a type-safe way to handle various network failure scenarios,
 * allowing for appropriate error handling and user feedback.
 */
sealed class NetworkError {
    /**
     * Represents a network connectivity issue, such as no internet connection or DNS resolution failure.
     */
    object NoInternetConnection : NetworkError()

    /**
     * Represents a server-side error (5xx HTTP status codes).
     * Indicates that the server encountered an error while processing the request.
     */
    data object ServerResponseException : NetworkError()

    /**
     * Represents a client-side error (4xx HTTP status codes).
     * Indicates that the request was malformed, unauthorized, or otherwise invalid.
     */
    data object ClientRequestException : NetworkError()

    /**
     * Represents a serialization/deserialization error.
     * Occurs when the response data cannot be transformed into the expected data model.
     */
    data object NoTransformationFoundException : NetworkError()

    /**
     * Represents any other unexpected error that doesn't fall into the above categories.
     */
    data object UnknownError : NetworkError()
}
