package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rooms.AppDatabase

class FavoriteCitiesListActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_cities)

        db = AppDatabase.getDatabase(this, lifecycleScope)

        val listView = findViewById<ListView>(R.id.favCitiesListView)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = arrayAdapter

        refreshFavoriteCitiesList()

        var selectedCity: String? = null

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedCity = arrayAdapter.getItem(position)
        }

        val btnRmv = findViewById<Button>(R.id.btnRmv)
        btnRmv.setOnClickListener {
            if (selectedCity != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    db.favoriteCityDao().deleteByCityName(selectedCity!!)
                    refreshFavoriteCitiesList()
                }
            } else {
                Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshFavoriteCitiesList() {
        lifecycleScope.launch(Dispatchers.IO) {
            val favoriteCities = db.favoriteCityDao().getAllFavoriteCities()
            val favCitiesNames = favoriteCities.map { it.favCityName }

            withContext(Dispatchers.Main) {
                arrayAdapter.clear()
                arrayAdapter.addAll(favCitiesNames)
                arrayAdapter.notifyDataSetChanged()
            }
        }
    }
}
