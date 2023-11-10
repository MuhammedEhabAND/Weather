package inc.moe.weather.model

import java.io.Serializable


data class Hourly(
    val dt: Long,
    val temp: Double,

    val feels_like: Double,
    val pressure: Long,
    val humidity: Long,

    val dew_point: Double,
    val uvi: Double,
    val clouds: Long,
    val visibility: Long,

    val wind_speed: Double,

    val wind_deg: Long,
    val wind_gust: Double,
    val weather: List<Weather>,
    val pop: Double,
): Serializable