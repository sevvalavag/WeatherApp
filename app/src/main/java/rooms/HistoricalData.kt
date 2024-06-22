package rooms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historical_data")
data class HistoricalData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val windSpeed: Double
)
