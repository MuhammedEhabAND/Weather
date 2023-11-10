package inc.moe.weather

import android.content.Context
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


fun main(){
    val long :Long= 1699203600
    print( getDate(long))


}


fun getDate(s: Long):String {
        val sdf = SimpleDateFormat("EEE,MMM dd ,yyyy ", Locale.ENGLISH)
        val netDate = Date(s * 1000)
        return sdf.format(netDate)

}
fun getDateForDaily(s: Long):String {
    val sdf = SimpleDateFormat("EEE", Locale.ENGLISH)
    val netDate = Date(s * 1000)
    return sdf.format(netDate)
}

fun getDateForHourly(s: Long):String {
    val sdf = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
    val netDate = Date(s * 1000)
    return sdf.format(netDate)
}

fun getImage(icon: String , number:Int) = "https://openweathermap.org/img/wn/$icon@${number}x.png"

