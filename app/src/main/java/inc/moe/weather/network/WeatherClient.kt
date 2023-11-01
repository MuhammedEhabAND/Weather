package inc.moe.weather.network

import inc.moe.weather.model.WeatherResponse

class WeatherClient:RemoteSource {
    override suspend fun getWeather(lon: Double, lat: Double) = API.retrofitService.getWeather(lat ,lon)
}