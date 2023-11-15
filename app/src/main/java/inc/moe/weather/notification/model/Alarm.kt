package inc.moe.weather.notification.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import inc.moe.weather.model.DatabaseWeather
import java.time.ZoneId

class Alarm(private val context: Context):AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: AlarmItem , weather:DatabaseWeather) {
        Log.i("TAG", "schedule: ")
        val intent = Intent(context ,AlarmReceiver::class.java).apply {
            putExtra("notification_lat" , item.lat)
            putExtra("notification_lon" , item.lon)
            putExtra("id" , item.id)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Log.i("TAG", "schedule: Failure ")
                Intent().also { mIntent ->
                    mIntent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    context.startActivity(mIntent)
                }
            }
        }
        Log.i("TAG", "schedule: Success")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                item.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem?, weather: DatabaseWeather) {
        if(item!=null){
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    context,
                    item.id,
                    Intent(context, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

    }
}




