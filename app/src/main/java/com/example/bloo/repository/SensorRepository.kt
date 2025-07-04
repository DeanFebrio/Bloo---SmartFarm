package com.example.bloo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bloo.model.SensorModel
import com.google.firebase.database.*

class SensorRepository {
    private val databaseSensor: DatabaseReference = FirebaseDatabase.getInstance().getReference("Sensor")

    private val _sensorLiveData = MutableLiveData<List<SensorModel>>()
    val sensorLiveData: LiveData<List<SensorModel>> get() = _sensorLiveData

    fun getSensorData() {
        databaseSensor.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.firstOrNull()?.let { data ->
                    val sensor = SensorModel(
                        data.child("Precipitation/value").getValue(Float::class.java) ?: 0f,
                        data.child("AirTemperature/value").getValue(Float::class.java) ?: 0f,
                        data.child("SoilMoisture/value").getValue(Float::class.java) ?: 0f,
                        data.child("Humidity/value").getValue(Float::class.java) ?: 0f
                    )
                    _sensorLiveData.postValue(listOf(sensor))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}")
            }
        })
    }
}
