package inc.moe.weather.favourite.viewmodel

import inc.moe.weather.model.DatabaseWeather

interface OnItemClickListener {
    fun onItemClickListener(weather : DatabaseWeather)
}