package inc.moe.weather.network

import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse

sealed class DatabaseState{
    class Success(val weatherResponse: List<DatabaseWeather> ):DatabaseState()
    class Failure(val message: String):DatabaseState()
    object Loading:DatabaseState()
}
