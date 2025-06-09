package com.example.bloo.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bloo.R
import com.example.bloo.viewModel.AutomaticViewModel
import com.example.bloo.viewModel.SensorViewModel
import com.google.android.material.button.MaterialButtonToggleGroup

class HomePage : AppCompatActivity() {
    private lateinit var sensorViewModel: SensorViewModel
    private lateinit var automaticViewModel: AutomaticViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_page)

        sensorViewModel = ViewModelProvider(this).get(SensorViewModel::class.java)

        val precipitationView = findViewById<TextView>(R.id.Precipitation)
        val airTemperatureView = findViewById<TextView>(R.id.AirTemperature)
        val soilMoistureView = findViewById<TextView>(R.id.SoilMoisture)
        val humidityView = findViewById<TextView>(R.id.Humidity)

        sensorViewModel.sensorData.observe(this) { sensorList ->
            sensorList.firstOrNull()?.let { sensor ->
                precipitationView.text = sensor.precipitationText
                airTemperatureView.text = String.format("%.1f°C", sensor.AirTemperature ?: 0f)
                soilMoistureView.text = String.format("%.1f%%", sensor.SoilMoisture ?: 0f)
                humidityView.text = String.format("%.1f%%", sensor.Humidity ?: 0f)
            }
        }


        val precipitationStatus = findViewById<TextView>(R.id.PrecipitationStatus)
        sensorViewModel.precipitationStatus.observe(this) { status ->
            precipitationStatus.text = status
            if (status == "Open") {
                precipitationStatus.setBackgroundResource(R.drawable.rounded_red_bg)
                precipitationStatus.setTextColor(ContextCompat.getColor(this, R.color.cream3))
            } else {
                precipitationStatus.setBackgroundResource(R.drawable.rounded_cream2_bg)
                precipitationStatus.setTextColor(ContextCompat.getColor(this, R.color.blue1))
            }
        }

        val temperatureStatus = findViewById<TextView>(R.id.TemperatureStatus)
        sensorViewModel.temperatureStatus.observe(this) { status ->
            temperatureStatus.text = status
            if (status == "Bad") {
                temperatureStatus.setBackgroundResource(R.drawable.rounded_red_bg)
                temperatureStatus.setTextColor(ContextCompat.getColor(this, R.color.cream3))
            } else {
                temperatureStatus.setBackgroundResource(R.drawable.rounded_cream2_bg)
                temperatureStatus.setTextColor(ContextCompat.getColor(this, R.color.green1))
            }
        }

        val soilMoistureStatus = findViewById<TextView>(R.id.SoilMoistureStatus)
        sensorViewModel.soilMoistureStatus.observe(this) {
                status ->
            soilMoistureStatus.text = status
            if (status == "Bad") {
                soilMoistureStatus.setBackgroundResource(R.drawable.rounded_red_bg)
                soilMoistureStatus.setTextColor(ContextCompat.getColor(this, R.color.cream3))
            } else {
                soilMoistureStatus.setBackgroundResource(R.drawable.rounded_green1_bg)
                soilMoistureStatus.setTextColor(ContextCompat.getColor(this, R.color.cream2))
            }
        }

        val humidityStatus = findViewById<TextView>(R.id.HumidityStatus)
        sensorViewModel.humidityStatus.observe(this) {
                status ->
            humidityStatus.text = status
            if (status == "Bad") {
                humidityStatus.setBackgroundResource(R.drawable.rounded_red_bg)
                humidityStatus.setTextColor(ContextCompat.getColor(this, R.color.cream3))
            } else {
                humidityStatus.setBackgroundResource(R.drawable.rounded_cream2_bg)
                humidityStatus.setTextColor(ContextCompat.getColor(this, R.color.green2))
            }
        }

        automaticViewModel = ViewModelProvider(this).get(AutomaticViewModel::class.java)
        val automaticRoof = findViewById<MaterialButtonToggleGroup>(R.id.OnOffAutoButtonRoof)
        val btnOnRoof = findViewById<Button>(R.id.btnONRoof)
        val btnOffRoof = findViewById<Button>(R.id.btnOFFRoof)
        val btnAutoRoof = findViewById<Button>(R.id.btnAUTORoof)

        val automaticPump = findViewById<MaterialButtonToggleGroup>(R.id.OnOffAutoButtonPump)
        val btnOnPump = findViewById<Button>(R.id.btnONPump)
        val btnOffPump = findViewById<Button>(R.id.btnOFFPump)
        val btnAutoPump = findViewById<Button>(R.id.btnAUTOPump)

        var currentRoofState = "AUTO"
        var currentPumpState = "AUTO"
        var isUpdatingFromFirebase = false

        automaticViewModel.AutomaticData.observe(this) { automaticList ->
            if (!isUpdatingFromFirebase) {
                automaticList.firstOrNull()?.let { automatic ->
                    println(automatic.AutomaticPump)
                    currentRoofState = automatic.AutomaticRoof ?: "AUTO"
                    currentPumpState = automatic.AutomaticPump ?: "AUTO"

                    when (currentRoofState) {
                        "ON" -> automaticRoof.check(R.id.btnONRoof)
                        "OFF" -> automaticRoof.check(R.id.btnOFFRoof)
                        "AUTO" -> automaticRoof.check(R.id.btnAUTORoof)
                    }
                    updateButtonColors(btnOnRoof, btnOffRoof, btnAutoRoof, currentRoofState)

                    when (currentPumpState) {
                        "ON" -> automaticPump.check(R.id.btnONPump)
                        "OFF" -> automaticPump.check(R.id.btnOFFPump)
                        "AUTO" -> automaticPump.check(R.id.btnAUTOPump)
                    }
                    updateButtonColors(btnOnPump, btnOffPump, btnAutoPump, currentPumpState)
                }
            }
        }

        automaticRoof.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                isUpdatingFromFirebase = true
                when (checkedId) {
                    R.id.btnONRoof -> {
                        currentRoofState = "ON"
                        updateButtonColors(btnOnRoof, btnOffRoof, btnAutoRoof, "ON")
                    }
                    R.id.btnOFFRoof -> {
                        currentRoofState = "OFF"
                        updateButtonColors(btnOnRoof, btnOffRoof, btnAutoRoof, "OFF")
                    }
                    R.id.btnAUTORoof -> {
                        currentRoofState = "AUTO"
                        updateButtonColors(btnOnRoof, btnOffRoof, btnAutoRoof, "AUTO")
                    }
                }
                automaticViewModel.updateAutomatic(currentRoofState, currentPumpState)
                isUpdatingFromFirebase = false
            }
        }

        automaticPump.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                isUpdatingFromFirebase = true
                when (checkedId) {
                    R.id.btnONPump -> {
                        currentPumpState = "ON"
                        updateButtonColors(btnOnPump, btnOffPump, btnAutoPump, "ON")
                    }
                    R.id.btnOFFPump -> {
                        currentPumpState = "OFF"
                        updateButtonColors(btnOnPump, btnOffPump, btnAutoPump, "OFF")
                    }
                    R.id.btnAUTOPump -> {
                        currentPumpState = "AUTO"
                        updateButtonColors(btnOnPump, btnOffPump, btnAutoPump, "AUTO")
                    }
                }
                automaticViewModel.updateAutomatic(currentRoofState, currentPumpState)
                isUpdatingFromFirebase = false // Reset flag after update
            }
        }

    }

    private fun updateButtonColors(onButton: Button, offButton: Button, autoButton: Button, selectedState: String) {
        val selectedColor = ContextCompat.getColor(this, R.color.cream1)
        val defaultColor = ContextCompat.getColor(this, R.color.cream2)

        when (selectedState) {
            "ON" -> {
                onButton.backgroundTintList = ColorStateList.valueOf(selectedColor)
                offButton.backgroundTintList = ColorStateList.valueOf(defaultColor)
                autoButton.backgroundTintList = ColorStateList.valueOf(defaultColor)
            }
            "OFF" -> {
                onButton.backgroundTintList = ColorStateList.valueOf(defaultColor)
                offButton.backgroundTintList = ColorStateList.valueOf(selectedColor)
                autoButton.backgroundTintList = ColorStateList.valueOf(defaultColor)
            }
            "AUTO" -> {
                onButton.backgroundTintList = ColorStateList.valueOf(defaultColor)
                offButton.backgroundTintList = ColorStateList.valueOf(defaultColor)
                autoButton.backgroundTintList = ColorStateList.valueOf(selectedColor)
            }
        }
    }
}
