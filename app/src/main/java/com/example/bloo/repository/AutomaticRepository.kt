package com.example.bloo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bloo.model.AutomaticModel
import com.google.firebase.database.*

class AutomaticRepository {
    private val databaseAutomatic: DatabaseReference = FirebaseDatabase.getInstance().getReference("Automatic")

    private val _automaticLiveData = MutableLiveData<List<AutomaticModel>>()
    val automaticLiveData: LiveData<List<AutomaticModel>> get() = _automaticLiveData

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
}
