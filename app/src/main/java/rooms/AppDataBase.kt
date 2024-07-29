package rooms

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [City::class, FavoriteCity::class, HistoricalData::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteCityDao(): FavoriteCityDao
    abstract fun cityDao(): CityDao
    abstract fun historicalDataDao(): HistoricalDataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "city_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(private val scope: CoroutineScope) :
            RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.cityDao(), database.favoriteCityDao(),database.historicalDataDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Database was recreated due to destructive migration, repopulate it.
                if (db.version == 4) {
                    scope.launch(Dispatchers.IO) {
                        INSTANCE?.let { database ->
                            populateDatabase(database.cityDao(), database.favoriteCityDao(),database.historicalDataDao())
                        }
                    }
                }
            }
        }

        private fun populateDatabase(
            cityDao: CityDao, favoriteCityDao: FavoriteCityDao, historicalDataDao: HistoricalDataDao
        ) {
            val cities = listOf(
                City("Barcelona", 41.3851, 2.1734),
                City("Madrid", 40.4168, -3.7038),
                City("Pamplona", 42.8125, -1.6458),
                City("Valencia", 39.4699, -0.3763),
                City("Granada", 37.1773, -3.5986),
                City("Malaga", 36.7213, -4.4214),
                City("Sevilla", 37.3886, -5.9823),
                City("San Sebastian", 43.3183, -1.9812),
                City("Bilbao", 43.2630, -2.9350),
                City("Oviedo", 43.3619, -5.8494)
            )

            cityDao.insertAll(cities)
            Log.d("Db", cityDao.getAllCities().toString())

            val favoriteCities = listOf<FavoriteCity>()
            favoriteCities.forEach { favoriteCity ->
                val existingFavoriteCity =
                    favoriteCityDao.getFavoriteCityByName(favoriteCity.favCityName)
                if (existingFavoriteCity == null) {
                    favoriteCityDao.insert(favoriteCity)
                }
            }
        }

    }
}




