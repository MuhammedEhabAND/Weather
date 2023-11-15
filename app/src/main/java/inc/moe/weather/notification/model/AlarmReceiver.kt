package inc.moe.weather.notification.model

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.MainActivity
import inc.moe.weather.R
import inc.moe.weather.home.viewmodel.HomeViewModel
import inc.moe.weather.home.viewmodel.HomeViewModelFactory
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import inc.moe.weather.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


class AlarmReceiver :BroadcastReceiver() {
    private var data: WeatherResponse? = null
    override fun onReceive(context: Context?, intent: Intent?)= goAsync {
        Log.i("TAG", "Alarm Trigger title:  \n Alarm Trigger title: ")


        val lat =  intent?.getStringExtra("notification_lat") ?: return@goAsync
        val lon =  intent.getStringExtra("notification_lon") ?: return@goAsync
        val id =  intent.getIntExtra("id" , 0 )
        Log.i("TAG", "Alarm Trigger title: $lat \n Alarm Trigger title: ")
        getWeather(lat ,lon , Constants.CURRENT_SELECTED_UNIT , Constants.CURRENT_LANGUAGE ,context)
        when(data){
            null->{
                Log.i("TAG", "onReceive: null")
            }
            else->{
                showNotification(context!! , data!! , id)

                Log.i("TAG", "onReceive: $data")

            }
        }




    }

    private suspend fun getWeather(
        lat: String,
        lon: String,
        currentSelectedUnit: String,
        currentLanguage: String,
        context: Context?
    ) {
        var databaseWeather :DatabaseWeather?= null
        val repo = Repo.getInstance(WeatherClient(), WeatherLocalSource.getInstance(context!!))
        repo.getWeather(lat ,lon ,currentSelectedUnit , currentLanguage).catch {

        }.collect{
            data = it
            databaseWeather = createDatabaseWeather(it)
            repo.updateWeather(databaseWeather!!)
        }



    }

    private fun createDatabaseWeather(weatherResponse: WeatherResponse) :DatabaseWeather{

        val title = data?.timezone
        val weatherType = data?.current?.weather?.first()?.description
        val weatherDegree = data?.current?.temp
        val weatherImage = data?.current?.weather?.first()?.icon
        return DatabaseWeather(data!!.lat ,
            data!!.lon , title!!, weatherType!!, weatherDegree!!, weatherImage!!, isScheduled = false, time = ""
        )

    }

    fun BroadcastReceiver.goAsync(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val pendingResult = goAsync()
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch(context) {
            try {
                block()
            } finally {
                pendingResult.finish()
            }
        }
    }
    private fun showNotification(appContext: Context , weatherResponse: WeatherResponse , id:Int) {
        Log.i("TAG", "showNotification: ")
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivities(
                appContext,
                0,
                arrayOf(intent),
                PendingIntent.FLAG_IMMUTABLE
            )
        val notification =
            NotificationCompat.Builder(appContext, "WeatherApp")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(weatherResponse.timezone)
                .setContentText("${weatherResponse.current.weather.first().description} : ${weatherResponse.current.temp.toInt()} ${Constants.CURRENT_WEATHER_UNIT}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setLargeIcon(largeIcon)
                .build()
        notificationManager.notify(1, notification)
    }

}