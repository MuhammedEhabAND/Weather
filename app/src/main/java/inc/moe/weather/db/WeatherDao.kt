package inc.moe.weather.db

import androidx.room.*
import inc.moe.weather.model.DatabaseWeather
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather")
    fun getAllFavWeather(): Flow<List<DatabaseWeather>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWeather(weather: DatabaseWeather):Long
    @Delete
    suspend fun deleteWeather(weather: DatabaseWeather):Int
    @Update
    suspend fun updateWeather(weather: DatabaseWeather)
}