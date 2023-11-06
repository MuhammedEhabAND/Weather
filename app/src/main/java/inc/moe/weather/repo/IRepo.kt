package inc.moe.weather.repo

import inc.moe.weather.model.WeatherResponse
import retrofit2.Response

interface IRepo {
    suspend fun getWeather(lon: String ,lat:String , units:String=""):Response<WeatherResponse>
}
