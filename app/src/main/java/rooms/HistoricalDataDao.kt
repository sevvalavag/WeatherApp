package rooms
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoricalDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertHistoricalData(historicalData: HistoricalData)

    @Query("SELECT * FROM historical_data WHERE cityName = :cityName")
     fun getHistoricalDataForCity(cityName: String): List<HistoricalData>


}