package inc.moe.weather.notification.model

import inc.moe.weather.model.DatabaseWeather

interface AlarmScheduler {
    fun schedule(item: AlarmItem , weather: DatabaseWeather)
    fun cancel(item: AlarmItem? , weather: DatabaseWeather)
}