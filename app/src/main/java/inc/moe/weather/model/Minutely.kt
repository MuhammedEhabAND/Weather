package inc.moe.weather.model

import java.io.Serializable

data class Minutely(
    val dt: Long,
    val precipitation: Long,
): Serializable