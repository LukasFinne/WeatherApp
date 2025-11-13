package com.example.weather.weather.models

val openStreetJSONData =
    """
    [
        {
            "place_id": 123456,
            "licence": "test",
            "osm_type": "relation",
            "osm_id": 654321,
            "lat": "52.5200",
            "lon": "13.4050",
            "category": "place",
            "type": "city",
            "place_rank": 16,
            "importance": 0.75,
            "addresstype": "city",
            "name": "Berlin",
            "display_name": "Berlin, Germany",
            "boundingbox": ["52.3382", "52.6755", "13.0883", "13.7611"]
        }
    ]
    """.trimIndent()

val noLonAndLanOpenStreetJSONData =
    """
    [
        {
            "place_id": 123456,
            "licence": "test",
            "osm_type": "relation",
            "osm_id": 654321,    
            "category": "place",
            "type": "city",
            "place_rank": 16,
            "importance": 0.75,
            "addresstype": "city",
            "name": "Berlin",
            "display_name": "Berlin, Germany",
            "boundingbox": ["52.3382", "52.6755", "13.0883", "13.7611"]
        }
    ]
    """.trimIndent()

val weatherYrNoJSON =
    """
    {
        "properties": {
            "timeseries": [
                {
                    "time": "2023-11-09T12:00:00Z",
                    "data": {
                        "instant": {
                            "details": {
                                "air_temperature": 15.5,
                                "wind_speed": 3.2
                            }
                        },
                        "next_1_hours": {
                            "summary": {
                                "symbol_code": "partlycloudy_day"
                            }
                        }
                    }
                }
            ]
        }
    }
    """.trimIndent()

val noWeatherInfoYrNoJSON =
    """
    {
        "properties": {
            "timeseries": [
                {
                    "time": "2023-11-09T12:00:00Z",
                    "data": {
                        "instant": {
                            "details": {
                             
                            }
                        },
                        "next_1_hours": {
                            "summary": {
                              
                            }
                        }
                    }
                }
            ]
        }
    }
    """.trimIndent()

val timeSeriesIsEmptyYrNoJSON =
    """
    {
        "properties": {
            "timeseries": []
        }
    }
    """.trimIndent()
