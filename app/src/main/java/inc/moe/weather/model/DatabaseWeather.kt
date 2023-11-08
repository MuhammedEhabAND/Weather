package inc.moe.weather.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "weather", primaryKeys = ["timeZone", "isCached"])
data class DatabaseWeather(
    var long: Double=0.0,
    var lat: Double=0.0,
    var timeZone: String="",
    var weatherType: String="",
    var temp: Double=0.0,
    var image: String="",
    var isCached: Boolean = false
)
