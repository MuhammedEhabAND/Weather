package inc.moe.weather.network

import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse

interface RemoteSource {
    suspend fun getWeather(lon:Double , lat: Double):WeatherResponse

}
