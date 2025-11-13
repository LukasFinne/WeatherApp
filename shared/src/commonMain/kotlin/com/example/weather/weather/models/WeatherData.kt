package com.example.weather.weather.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Root data class representing the complete weather API response from the MET Norway API.
 *
 * @param properties Container for the weather data properties
 */
@Serializable
data class WeatherData(val properties: Properties)

/**
 * Container for weather properties, including time series data.
 *
 * @param timeSeries List of weather data points across different time periods
 */
@Serializable
data class Properties(@SerialName("timeseries") val timeSeries: List<TimeSeries>)

/**
 * Represents a single point in time with associated weather data.
 *
 * @param time ISO 8601 timestamp for this weather data point
 * @param data Weather measurements and forecasts for this time period
 */
@Serializable
data class TimeSeries(val time: String, val data: Data)

/**
 * Container for weather measurements and short-term forecasts.
 *
 * @param instant Current weather conditions and measurements
 * @param nextOneHours Weather forecast for the next hour (optional)
 */
@Serializable
data class Data(val instant: Instant, @SerialName("next_1_hours") val nextOneHours: NextOneHour? = null)

/**
 * Container for instantaneous weather measurements.
 *
 * @param details Detailed weather measurements like temperature and wind speed
 */
@Serializable
data class Instant(val details: Details)

/**
 * Short-term weather forecast summary.
 *
 * @param summary Weather condition summary for the forecast period
 */
@Serializable
data class NextOneHour(val summary: Summary)

/**
 * Weather condition summary with symbolic representation.
 *
 * @param symbolCode Weather symbol code representing the condition (e.g., "clearsky", "rain")
 */
@Serializable
data class Summary(@SerialName("symbol_code") val symbolCode: String)

/**
 * Detailed weather measurements and conditions.
 *
 * @param airTemperature Air temperature in Celsius (optional)
 * @param windSpeed Wind speed in meters per second (optional)
 */
@Serializable
data class Details(
    @SerialName("air_temperature") val airTemperature: Double? = null,
    @SerialName("wind_speed") val windSpeed: Double? = null,
)

/**
 * Simplified weather data for a city, extracted from the complete weather API response.
 *
 * This represents the essential weather information that the application displays to users.
 *
 * @param temperature Current air temperature in Celsius
 * @param windSpeed Current wind speed in meters per second
 * @param summary Weather condition description or symbol code
 */
@Serializable
data class CityWeather(val temperature: Double, val windSpeed: Double, val summary: String)
