package inc.moe.weather.network

import inc.moe.weather.utils.Constants
import inc.moe.weather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") apiKey: String = Constants.APIKEY,
        @Query("lang") lang:String,
        @Query("units") units: String,
    ): WeatherResponse
}