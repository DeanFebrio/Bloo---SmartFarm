package com.example.bloo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.bloo.model.AutomaticModel
import com.example.bloo.repository.AutomaticRepository

class AutomaticViewModel: ViewModel() {
    private val repository = AutomaticRepository()

    val AutomaticData: LiveData<List<AutomaticModel>> get() = repository.automaticLiveData

    init {
        repository.getAutomaticData()
    }

    fun updateAutomatic(automaticRoof: String, automaticPump: String) {
        repository.updateAutomaticData(automaticRoof, automaticPump)
    }
}