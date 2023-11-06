package inc.moe.weather.repo

import inc.moe.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IRepo {
     fun getWeather(lon: String ,lat:String , units:String=""): Flow<WeatherResponse>
}
