package com.example.weatherapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import api.WeatherApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        lifecycleScope.launch(Dispatchers.Main) {
            // Lokasyon izni kontrolü
            if (ActivityCompat.checkSelfPermission(
                    this@SplashScreenActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@SplashScreenActivity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@SplashScreenActivity,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
                return@launch
            }


            // Lokasyonu alma
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        fetchWeatherData(latitude, longitude)
                    } else {
                        Toast.makeText(
                            this@SplashScreenActivity,
                            "Location information could not be obtained.",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response =
                    WeatherApi.service.getWeatherTimelineByCoordinates(latitude, longitude)
                        .execute()
                if (response.isSuccessful) {
                    val weatherData = response.body()?.currentConditions
                    weatherData?.let {
                        launch(Dispatchers.Main) {
                            navigateToMainActivity(it.temp, it.conditions)
                        }
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@SplashScreenActivity,
                            "Weather data could not be retrieved.",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToMainActivity()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@SplashScreenActivity,
                        "A mistake has been made.",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToMainActivity()
                }
            }
        }
    }

    private fun navigateToMainActivity(temperature: Double? = null, condition: String? = null) {
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java).apply {
            putExtra("TEMPERATURE", temperature)
            putExtra("CONDITION", condition)
        }
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // İzin alındıktan sonra tekrar lokasyon alma ve veri çekme işlemleri başlatılabilir.
            recreate()
        }
    }

}
