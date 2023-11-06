package inc.moe.weather.network

import inc.moe.weather.model.WeatherResponse

sealed class ApiState{
    class Success(val weatherResponse: WeatherResponse):ApiState()
    class Failure(val message: String):ApiState()
    object Loading:ApiState()
}
