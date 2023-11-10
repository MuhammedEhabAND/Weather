package inc.moe.weather.model

import android.content.Context
import inc.moe.weather.R

object SettingsData {

    var settingsList :List<Settings> = listOf(
        Settings(R.string.location , R.drawable.location_expanded , R.string.gps , R.string.maps , false ,false),
        Settings(R.string.language , R.drawable.language_expanded , R.string.english , R.string.arabic ,false , true),
        Settings(R.string.wind_speed_unit , R.drawable.wind_expanded , R.string.meter , R.string.mile , false , true),
        Settings(R.string.notification , R.drawable.notification_expanded , R.string.enable , R.string.disable ,false , true),
        Settings(R.string.temp , R.drawable.temp_expanded , R.string.celsius , R.string.fahrenheit ,false , true)
    )
    fun saveSettings(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        for (setting in settingsList) {
            val key = context.getString(setting.title)
            editor.putBoolean(key, setting.isOptionOne)
        }

        editor.apply()
    }

    fun loadSettings(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MySettings", Context.MODE_PRIVATE)

        for (setting in settingsList) {
            val key = context.getString(setting.title)
            val isEnabled = sharedPreferences.getBoolean(key, setting.isOptionOne)
            setting.isOptionOne = isEnabled
        }
    }

}