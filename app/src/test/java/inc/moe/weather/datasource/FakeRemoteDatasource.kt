package inc.moe.weather.datasource

import inc.moe.weather.model.Current
import inc.moe.weather.model.Daily
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.FeelsLike
import inc.moe.weather.model.Hourly
import inc.moe.weather.model.Minutely
import inc.moe.weather.model.Temp
import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.RemoteSource

class FakeRemoteDatasource(private var weatherResponse: WeatherResponse= WeatherResponse(
    2.5, 2.5, "Alexandria", 25L,
    Current(
        15L,
        15L,
        15,
        15.5,
        15.5,
        15L,
        15L,
        15.5,
        15.5,
        15L,
        15L,
        15.5,
        15L,
        15.5,
        listOf(Weather(1L ,"" , "" , ""))
    ),
    listOf(Minutely(15L,15L)),
    null,
    listOf(Hourly(
        15L ,
        15.5,
        15.5,
        15L ,
        15L ,
        15.5,
        15.5,
        15L,
        15L,
        15.5,
        15L,
        15.5,
        listOf(Weather(1L,"","","")),
        15.5))
    , listOf(Daily(
        1L,
        15.5,
        15.5,
        15.15,
        15.5,
        15.5,
        Temp(15.5,15.5,15.5,15.5,15.5,15.5),

        FeelsLike(15.5,15.5,15.5,15.5),
        15.5,
        15.5,15.5,15.5,15.5,15.5,
        listOf(Weather(1L,"", "", "")),
        15.5,15.15,15.5
    )))) : RemoteSource {


    override suspend fun getWeather(
        lon: String,
        lat: String,
        units: String,
        language: String,
    ): WeatherResponse {
        return weatherResponse

    }
}