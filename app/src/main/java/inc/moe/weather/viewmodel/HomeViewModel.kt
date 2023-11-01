package inc.moe.weather.viewmodel

import androidx.lifecycle.ViewModel
import inc.moe.weather.repo.IRepo

class HomeViewModel(private val iRepo: IRepo) : ViewModel() {
    init{
        getCurrentWeather()
    }

    private fun getCurrentWeather() {

    }
}