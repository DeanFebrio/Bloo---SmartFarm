package com.example.bloo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.bloo.model.SensorModel
import com.example.bloo.repository.SensorRepository

class SensorViewModel : ViewModel() {
    private val repository = SensorRepository()

    val sensorData: LiveData<List<SensorModel>> get() = repository.sensorLiveData

    val temperatureStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) { sensorList ->
            value = when {
                (sensorList.firstOrNull()?.AirTemperature ?: 0f) > 35.0f -> "Bad"
                else -> "Good"
            }
        }
    }

    val precipitationStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) { sensorList ->
            value = when {
                (sensorList.firstOrNull()?.Precipitation ?: 0f) == 1f -> "Close"
                else -> "Open"
            }
        }
    }

    val soilMoistureStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) { sensorList ->
            value = when {
                (sensorList.firstOrNull()?.SoilMoisture ?: 0f) < 30.0f -> "Bad"
                else -> "Good"
            }
        }
    }

    val humidityStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) { sensorList ->
            value = when {
                (sensorList.firstOrNull()?.Humidity ?: 0f) < 50.0f -> "Bad"
                else -> "Good"
            }
        }
    }

    init {
        repository.getSensorData()
    }
}
