package inc.moe.weather.repo

import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepo {
     suspend fun cacheWeatherData(weatherData: WeatherResponse?)
     suspend fun readCachedWeatherData(): WeatherResponse?
     suspend fun addWeather(weather: DatabaseWeather):Long
     suspend fun deleteWeather(weather: DatabaseWeather):Int
     fun  getAllFavWeathers():Flow<List<DatabaseWeather>>
     suspend fun updateWeather(weather:DatabaseWeather)
     fun getWeather(lon: String ,lat:String , units:String ,language:String ): Flow<WeatherResponse>

}
