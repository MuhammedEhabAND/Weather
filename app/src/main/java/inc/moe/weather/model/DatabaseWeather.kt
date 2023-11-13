package inc.moe.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather" ,primaryKeys = ["lat", "lon"])
data class DatabaseWeather(
    var lon: Double=0.0,
    var lat: Double=0.0,
    var timeZone: String="",
    var weatherType: String="",
    var temp:Double=0.0,
    var image: String="",
    var time : String = "",
    var isScheduled :Boolean = false

)
