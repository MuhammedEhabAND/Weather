package inc.moe.weather.repo

import inc.moe.weather.model.WeatherResponse

interface IRepo {
    suspend fun getWeather(lon: Double ,lat:Double):WeatherResponse
}
