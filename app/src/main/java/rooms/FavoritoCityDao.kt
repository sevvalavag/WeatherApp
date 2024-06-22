package rooms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities")
    fun getAllFavoriteCities(): List<FavoriteCity>

    @Query("SELECT * FROM favorite_cities WHERE favCityName = :favCityName LIMIT 1")
    fun getFavoriteCityByName(favCityName: String): FavoriteCity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(city: FavoriteCity)

    @Delete
    fun delete(city: FavoriteCity)

    @Query("DELETE FROM favorite_cities WHERE favCityName = :cityName")
    fun deleteByCityName(cityName: String)
}
