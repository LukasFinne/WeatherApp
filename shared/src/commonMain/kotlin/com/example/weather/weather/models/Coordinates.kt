package com.example.weather.weather.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing geographic coordinates and location information from the OpenStreetMap Nominatim API.
 *
 * This class contains detailed information about a geographic location, including coordinates,
 * administrative details, and metadata from OpenStreetMap.
 *
 * @param placeId Unique identifier for this place in the geocoding service
 * @param licence License information for the data usage
 * @param osmType Type of OpenStreetMap object (node, way, relation)
 * @param osmId OpenStreetMap object identifier
 * @param lat Latitude coordinate as a string
 * @param lon Longitude coordinate as a string
 * @param category General category of the place (e.g., "place", "amenity")
 * @param type Specific type within the category (e.g., "city", "town", "village")
 * @param placeRank Importance ranking of this place
 * @param importance Calculated importance score (0.0 to 1.0)
 * @param addressType Primary address type of this location
 * @param name Primary name of the location
 * @param displayName Full formatted display name of the location
 * @param boundingBox Geographic bounding box coordinates as strings [min_lat, max_lat, min_lon, max_lon]
 */
@Serializable
data class Coordinates(
    @SerialName("place_id")
    val placeId: Long,
    val licence: String,
    @SerialName("osm_type")
    val osmType: String,
    @SerialName("osm_id")
    val osmId: Long,
    val lat: String,
    val lon: String,
    val category: String,
    val type: String,
    @SerialName("place_rank")
    val placeRank: Long,
    val importance: Double,
    @SerialName("addresstype")
    val addressType: String,
    val name: String,
    @SerialName("display_name")
    val displayName: String,
    @SerialName("boundingbox")
    val boundingBox: List<String>,
)
