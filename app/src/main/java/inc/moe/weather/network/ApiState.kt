package inc.moe.weather.network

import inc.moe.weather.model.WeatherResponse

sealed class ApiState{
    class Success(val weatherResponse: WeatherResponse , val isCached :String=""):ApiState()
    class Failure(val message: String):ApiState()
    object Loading:ApiState()
}
