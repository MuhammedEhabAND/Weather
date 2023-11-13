package inc.moe.notesapp.database

import android.content.Context
import inc.moe.weather.db.AppDatabase
import inc.moe.weather.db.WeatherDao
import inc.moe.weather.model.DatabaseWeather
import kotlinx.coroutines.flow.Flow

class WeatherLocalSource private constructor(var context: Context) : IWeatherLocalSource {

    private var weatherDao:WeatherDao

    init {
        val appDatabase = AppDatabase.getInstance(context)
        weatherDao = appDatabase.weatherDao()
    }

    companion object {
        private var instance: WeatherLocalSource? = null

        fun getInstance(context: Context): WeatherLocalSource {
            if (instance == null) {
                instance = WeatherLocalSource(context)
            }
            return instance!!
        }
    }

    override suspend fun addWeather(weather: DatabaseWeather):Long {
       return weatherDao.addWeather(weather)


    }

    override suspend fun deleteWeather(weather: DatabaseWeather):Int {
        return weatherDao.deleteWeather(weather)
    }

    override  fun getAllFavWeathers(): Flow<List<DatabaseWeather>> {
        return  weatherDao.getAllFavWeather()
    }

    override suspend fun updateWeather(weather: DatabaseWeather) {
        weatherDao.updateWeather(weather)
    }


}
