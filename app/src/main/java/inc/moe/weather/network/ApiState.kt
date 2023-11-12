package inc.moe.weather.network

import inc.moe.weather.model.WeatherResponse

sealed class ApiState{
    class Success(val weatherResponse: WeatherResponse , val isCached :Boolean=false):ApiState()
    class Failure(val message: String , val isCached: Int = 0 ):ApiState()
    object Loading:ApiState()
}
