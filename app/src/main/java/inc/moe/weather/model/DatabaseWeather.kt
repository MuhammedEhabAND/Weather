package inc.moe.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather" ,primaryKeys = ["lat", "lon"])
data class DatabaseWeather(
    var lon: String="",
    var lat: String="",

    var timeZone: String="",
    var weatherType: String="",
    var temp:Double=0.0,
    var image: String="",

)
