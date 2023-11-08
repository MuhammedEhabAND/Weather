package inc.moe.notesapp.database

import inc.moe.weather.model.DatabaseWeather

interface IWeatherLocalSource {
    suspend fun addWeather(weather: DatabaseWeather)

    suspend fun deleteWeather(weather: DatabaseWeather)
    suspend fun  getAllFavWeathers():List<DatabaseWeather>
    suspend fun  getCacheWeathers():DatabaseWeather
}