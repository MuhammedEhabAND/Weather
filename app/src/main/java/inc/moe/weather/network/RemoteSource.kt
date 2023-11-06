package inc.moe.weather.network

import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse
import retrofit2.Response

interface RemoteSource {
    suspend fun getWeather(lon:String , lat: String,units:String):Response<WeatherResponse>

}
