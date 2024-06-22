package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rooms.AppDatabase
import rooms.City


class CityListActivity : AppCompatActivity() {

    private lateinit var citiesListView: ListView
    private lateinit var cities: List<City>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)

        citiesListView = findViewById(R.id.citiesListView)
        loadCities()

        citiesListView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = cities[position]
            val intent = Intent(this, CityDetailScreenActivity::class.java)
            intent.putExtra("city_name", selectedCity.cityName)
            startActivity(intent)
        }
    }


    private fun loadCities() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(this@CityListActivity, this)
            val cityDao = db.cityDao()

            cities = cityDao.getAllCities()
            val cityNames = mutableListOf<String>()
            cities.forEach {
                cityNames.add(it.cityName)

            }

            withContext(Dispatchers.Main) {
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(this@CityListActivity, android.R.layout.simple_list_item_1, cityNames)
                citiesListView.adapter = arrayAdapter
            }
        }
    }


}
