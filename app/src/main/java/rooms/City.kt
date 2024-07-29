package rooms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city")
data class City(
    @PrimaryKey val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double?=null,
    val highTemperature: Double?=null,
    val lowTemperature: Double?=null,
    val weatherType: String?=null
)



