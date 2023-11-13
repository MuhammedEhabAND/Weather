package inc.moe.weather

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.getSystemService
import com.yariksoffice.lingver.Lingver

class LanguageApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name : CharSequence = "Weather App Alarm"
            val description  = "Channel for alarm weather"
            val importance  = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("WeatherApp" , name , importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}