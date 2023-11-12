package inc.moe.weather.network

import inc.moe.weather.model.WeatherResponse

class WeatherClient:RemoteSource {
    override suspend fun getWeather(lon: String, lat: String,unit:String , language:String) = API.retrofitService.getWeather(lat ,lon, units = unit , lang = language)
}