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
        addSource(sensorData) {
            sensorList ->
            value = when {
                (sensorList.firstOrNull()?.AirTemperature ?: 0) > 35.0 -> "Bad"
                else -> "Good"
            }
        }
    }

    val precipitationStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) {
            sensorList ->
            value = when {
                (sensorList.firstOrNull()?.Precipitation ?: 0) == 1 -> "Close"
                else -> "Open"
            }
        }
    }

    val soilMoistureStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) {
            sensorList ->
            value = when {
                (sensorList.firstOrNull()?.SoilMoisture ?: 0) < 30.0 -> "Bad"
                else -> "Good"
            }
        }
    }

    val humidityStatus = MediatorLiveData<String>().apply {
        addSource(sensorData) {
                sensorList ->
            value = when {
                (sensorList.firstOrNull()?.Humidity ?: 0) < 50.0 -> "Bad"
                else -> "Good"
            }
        }
    }

    init {
        repository.getSensorData()
    }
}
