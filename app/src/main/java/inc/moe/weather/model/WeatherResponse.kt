package inc.moe.weather.model

import java.io.Serializable


data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Long,
    val current: Current,
    val minutely: List<Minutely>,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    ):Serializable










