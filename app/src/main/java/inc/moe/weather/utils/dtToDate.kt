package inc.moe.weather.utils

import java.text.SimpleDateFormat
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

