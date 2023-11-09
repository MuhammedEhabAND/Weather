package inc.moe.notesapp.database

import inc.moe.weather.model.DatabaseWeather
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalSource {
    suspend fun addWeather(weather: DatabaseWeather):Long

    suspend fun deleteWeather(weather: DatabaseWeather):Int
     fun  getAllFavWeathers(): Flow<List<DatabaseWeather>>

}