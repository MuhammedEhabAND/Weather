package inc.moe.weather.notification.model

import java.time.LocalDateTime

data class AlarmItem(
    val time : LocalDateTime,
    val lat : String,
    val lon : String,


)
