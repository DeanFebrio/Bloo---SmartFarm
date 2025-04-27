package com.example.bloo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bloo.model.AutomaticModel
import com.example.bloo.model.SensorModel
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

class Repository {
    private val databaseSensor: DatabaseReference = FirebaseDatabase.getInstance().getReference("Sensor")
    private val databaseAutomatic: DatabaseReference = FirebaseDatabase.getInstance().getReference("Automatic")

    private val _sensorLiveData = MutableLiveData<List<SensorModel>>()
    val sensorLiveData: LiveData<List<SensorModel>> get() = _sensorLiveData

    private val _automaticLiveData = MutableLiveData<List<AutomaticModel>>()
    val automaticLiveData: LiveData<List<AutomaticModel>> get() = _automaticLiveData

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

    fun getAutomaticData() {
        databaseAutomatic.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val automatic = AutomaticModel (
                snapshot.child("AutomaticRoof").getValue(String::class.java) ?: "AUTO",
                snapshot.child("AutomaticPump").getValue(String::class.java) ?: "AUTO"
                )
                _automaticLiveData.postValue(listOf(automatic))
            }
            override fun onCancelled(error: DatabaseError) {
                println("Error: ${error.message}");
            }
        })
    }

    fun updateAutomaticData(automaticRoof: String, automaticPump: String) {
        val automaticData = mapOf(
            "AutomaticRoof" to automaticRoof,
            "AutomaticPump" to automaticPump
        )
        databaseAutomatic.setValue(automaticData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Data updated successfully")
            } else {
                println("Error updating data: ${task.exception?.message}")
            }
        }
    }

    private fun convertTime(timestamp: Long): String {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(Date(timestamp))
    }
}
