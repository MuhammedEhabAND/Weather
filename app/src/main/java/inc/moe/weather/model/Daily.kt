package inc.moe.weather.model


data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,

    val moon_phase: Double,
    val temp: Temp,

    val feels_like: FeelsLike,
    val pressure: Long,
    val humidity: Long,

    val dew_point: Double,

    val wind_speed: Double,

    val wind_deg: Long,

    val wind_gust: Double,
    val weather: List<Weather>,
    val clouds: Long,
    val pop: Long,
    val uvi: Double,
)