package inc.moe.weather.network

import inc.moe.weather.Constants
import inc.moe.weather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") part: String="",
        @Query("appid") apiKey: String = Constants.APIKEY
    ):WeatherResponse
}