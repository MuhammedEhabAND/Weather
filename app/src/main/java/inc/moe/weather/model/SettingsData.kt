package inc.moe.weather.model

import inc.moe.weather.R

object SettingsData {
    var settingsList :List<Settings> = listOf(
        Settings("Location" , R.drawable.location_expanded , "GPS" , "Map"),
        Settings("Language" , R.drawable.language_expanded , "English" , "Arabic"),
        Settings("Wind Speed" , R.drawable.wind_expanded , "Meter/sec" , "Mile/Hour"),
        Settings("Notification" , R.drawable.notification_expanded , "Enable" , "Disable"),
        Settings("Temperature" , R.drawable.temp_expanded , "Celsius" , "Fahrenheit")
    )

}