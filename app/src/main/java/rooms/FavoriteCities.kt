package rooms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCity(
    @PrimaryKey val favCityName: String,
    val latitude: Double,
    val longitude: Double
)