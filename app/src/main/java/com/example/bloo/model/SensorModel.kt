package com.example.bloo.model

data class SensorModel(
    val Precipitation: Int? = null,
    val AirTemperature: Int? = null,
    val SoilMoisture: Int? = null,
    val Humidity: Int? = null
)  {
    val precipitationText: String
        get() = when (Precipitation) {
            0 -> "Dry"
            1 -> "Rain"
            else -> Precipitation?.toString() ?: "-"
        }
}
