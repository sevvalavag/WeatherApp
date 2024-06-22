package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rooms.AppDatabase
import rooms.HistoricalDataDao

class HistoricalDataActivity : AppCompatActivity() {
    private lateinit var historicalDataDao: HistoricalDataDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historical_data)

        // Intent'ten cityName'i al
        val cityName = intent.getStringExtra("history_city_name") ?: "None"

        // Veritabanı örneğini al
        val db = AppDatabase.getDatabase(this, lifecycleScope)
        historicalDataDao = db.historicalDataDao()

        // TextView bileşenlerine verileri ata
        val txtCityName = findViewById<TextView>(R.id.txtHistoricalData)
        val txtCondition = findViewById<TextView>(R.id.txtCondition)
        val txtWindSpeed = findViewById<TextView>(R.id.txtWindSpeed)
        val txtTemperature = findViewById<TextView>(R.id.txtTemperature)

        txtCityName.text = cityName

        // Verileri arka planda al ve UI'yi güncelle
        lifecycleScope.launch {
            val averageTemperature = getAverageTemperatureForCity(cityName)
            val mostCommonCondition = getMostCommonConditionForCity(cityName)
            val averageWindSpeed = getAverageWindSpeedForCity(cityName)

            txtWindSpeed.text = averageWindSpeed?.toString() ?: "N/A"
            txtCondition.text = mostCommonCondition ?: "N/A"
            txtTemperature.text = averageTemperature?.toString() ?: "N/A"

        }
    }

    private suspend fun getAverageTemperatureForCity(cityName: String): Double? {
        return withContext(Dispatchers.IO) {
            val historicalDataList = historicalDataDao.getHistoricalDataForCity(cityName)
            var totalTemperature = 0.0
            for (data in historicalDataList) {
                totalTemperature += data.temperature
            }
            if (historicalDataList.isNotEmpty()) {
                totalTemperature / historicalDataList.size
            } else {
                null
            }
        }
    }
    private suspend fun getAverageWindSpeedForCity(cityName: String): Double? {
        return withContext(Dispatchers.IO) {
            val historicalDataList = historicalDataDao.getHistoricalDataForCity(cityName)
            var totalWindSpeed = 0.0
            for (data in historicalDataList) {
                totalWindSpeed += data.windSpeed
            }
            if (historicalDataList.isNotEmpty()) {
                totalWindSpeed/ historicalDataList.size
            } else {
                null
            }
        }
    }

    private suspend fun getMostCommonConditionForCity(cityName: String): String? {
        return withContext(Dispatchers.IO) {
            val historicalDataList = historicalDataDao.getHistoricalDataForCity(cityName)
            val conditionMap = HashMap<String, Int>()
            for (data in historicalDataList) {
                val count = conditionMap[data.condition] ?: 0
                conditionMap[data.condition] = count + 1
            }
            var maxCount = 0
            var mostCommonCondition: String? = null
            for ((condition, count) in conditionMap) {
                if (count > maxCount) {
                    maxCount = count
                    mostCommonCondition = condition
                }
            }
            mostCommonCondition
        }
    }
}


