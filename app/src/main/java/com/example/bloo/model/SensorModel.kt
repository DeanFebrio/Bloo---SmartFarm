package com.example.bloo.model

data class SensorModel(
    val Precipitation: Float? = null,
    val AirTemperature: Float? = null,
    val SoilMoisture: Float? = null,
    val Humidity: Float? = null
) {
    val precipitationText: String
        get() = when {
            Precipitation == 0f -> "Dry"
            Precipitation == 1f -> "Rain"
            else -> Precipitation?.toString() ?: "-"
        }
}
