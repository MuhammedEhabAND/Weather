package inc.moe.weather.network

import inc.moe.weather.Constants
import inc.moe.weather.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") part: String="",
        @Query("appid") apiKey: String = Constants.APIKEY,
        @Query("units") units: String,
    ):Response<WeatherResponse>
}