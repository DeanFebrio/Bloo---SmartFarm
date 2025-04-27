package com.example.bloo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bloo.model.AutomaticModel
import com.example.bloo.model.SensorModel
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

class SensorRepository {
    private val databaseSensor: DatabaseReference = FirebaseDatabase.getInstance().getReference("Sensor")


    private val _sensorLiveData = MutableLiveData<List<SensorModel>>()
    val sensorLiveData: LiveData<List<SensorModel>> get() = _sensorLiveData

    fun getSensorData() {
        databaseSensor.orderByKey().limitToLast(1).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.firstOrNull()?.let { data ->
                    val sensor = SensorModel(
                        data.child("Precipitation/value").getValue(Int::class.java) ?: 0,
                        data.child("AirTemperature/value").getValue(Int::class.java) ?: 0,
                        data.child("SoilMoisture/value").getValue(Int::class.java) ?: 0,
                        data.child("Humidity/value").getValue(Int::class.java) ?: 0
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
