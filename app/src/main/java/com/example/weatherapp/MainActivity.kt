package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCityList = findViewById<Button>(R.id.btnCityList)
        val btnFavCityList = findViewById<Button>(R.id.btnFavCityList)

        btnCityList.setOnClickListener {
            val intent = Intent(this, CityListActivity::class.java)
            startActivity(intent)
        }

        btnFavCityList.setOnClickListener {
            val intent = Intent(this, FavoriteCitiesListActivity::class.java)
            startActivity(intent)
        }

        // Intent'ten verileri al
        val temperature = intent.getDoubleExtra("TEMPERATURE", 0.0)
        val condition = intent.getStringExtra("CONDITION")

        // UI'yi güncelle
        updateUI(temperature, condition)
    }

    private fun updateUI(temperature: Double?, condition: String?) {
        val txtDegree = findViewById<TextView>(R.id.txtDegree)
        val txtWeatherTyp = findViewById<TextView>(R.id.txtWeatherTyp)

        txtDegree.text = "${temperature ?: ""}°F"
        txtWeatherTyp.text = condition ?: ""
    }
}
