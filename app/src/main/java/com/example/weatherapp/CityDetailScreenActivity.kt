package com.example.weatherapp;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import api.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rooms.AppDatabase
import rooms.FavoriteCity
import rooms.HistoricalData

class CityDetailScreenActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private var cityName: String? = null
    private var temperature: Double? = null
    private var condition: String? = null
    private var windSpeed: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_detail_screen)

        db = AppDatabase.getDatabase(this, lifecycleScope)

        val cityNameTextView = findViewById<TextView>(R.id.txtCityName)
        val txtDegree = findViewById<TextView>(R.id.txtDegree)
        val txtWeatherTyp = findViewById<TextView>(R.id.txtWeatherTyp)
        val checkBoxFavorite = findViewById<CheckBox>(R.id.checkBoxFavorite)

        cityName = intent.getStringExtra("city_name")
        lifecycleScope.launch {
            checkBoxFavorite.isChecked = isCityFavorite(cityName)
        }


        checkBoxFavorite.setOnCheckedChangeListener { _, isChecked ->
            cityName?.let { city ->
                lifecycleScope.launch {
                    if (isChecked) {
                        val favoriteCity =
                            FavoriteCity(favCityName = city, latitude = 0.0, longitude = 0.0)
                        withContext(Dispatchers.IO) {
                            db.favoriteCityDao().insert(favoriteCity)
                        }
                    } else {
                        withContext(Dispatchers.IO) {
                            db.favoriteCityDao().deleteByCityName(city)
                        }
                    }
                }
            }
        }

        cityName?.let {
            // Set city name in TextView
            cityNameTextView.text = it

            // Fetch additional information from the API based on city coordinates
            lifecycleScope.launch(Dispatchers.IO) {
                val cityInfo = db.cityDao().getCityByName(it)
                val weatherResponse = cityInfo?.let { it1 ->
                    WeatherApi.service.getWeatherTimelineByCoordinates(
                        it1.latitude,
                        it1.longitude
                    ).execute().body()
                }

                // Update UI with fetched information
                withContext(Dispatchers.Main) {
                    weatherResponse?.let { response ->
                        val currentConditions = response.currentConditions
                        currentConditions?.let { conditions ->
                            txtDegree.text = "${conditions.temp}° F"
                            txtWeatherTyp.text = conditions.conditions

                            // Assign fetched values to variables for later use
                            temperature = conditions.temp
                            condition = conditions.conditions
                            windSpeed = conditions.windSpeed
                        }
                    }
                }
            }
        }

        val btnAddHistoricalData = findViewById<Button>(R.id.btnAddHistoricalData)
        btnAddHistoricalData.setOnClickListener {
            addHistoricalData()

        }

        val btnHistoricalData = findViewById<Button>(R.id.btnHistoricalData)
        btnHistoricalData.setOnClickListener {
            val intent = Intent(this, HistoricalDataActivity::class.java)
            intent.putExtra("history_city_name", cityName)
            startActivity(intent)
        }
    }


    private suspend fun isCityFavorite(cityName: String?): Boolean {
        return withContext(Dispatchers.IO) {
            cityName?.let {
                db.favoriteCityDao().getFavoriteCityByName(it) != null
            } ?: false
        }
    }


    private fun addHistoricalData() {
        temperature?.let { temperature ->
            lifecycleScope.launch(context = Dispatchers.IO){
                val historicalData = HistoricalData(
                    cityName = cityName ?: "Jersey",
                    temperature = temperature ?: 0.0,
                    condition = condition ?: "Awful",
                    windSpeed = windSpeed ?: 0.0
                )
                db.historicalDataDao().insertHistoricalData(historicalData)
                launch(context = Dispatchers.Main){
                    Toast.makeText(
                        applicationContext,
                        "Historical data added for $cityName",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }
    }

}

