package inc.moe.weather.db

import androidx.room.*
import inc.moe.weather.model.DatabaseWeather

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE isCached = 0 ORDER BY timeZone ASC")
    suspend fun getAllFavWeather():List<DatabaseWeather>
    @Query("SELECT * FROM weather WHERE isCached = 1  LIMIT 1")
    suspend fun getCachedWeather():DatabaseWeather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeather(weather: DatabaseWeather)
    @Delete
    suspend fun deleteWeather(weather: DatabaseWeather)
}