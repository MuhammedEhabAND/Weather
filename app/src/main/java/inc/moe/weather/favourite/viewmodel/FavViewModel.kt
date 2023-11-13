package inc.moe.weather.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.notification.model.AlarmItem
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class FavViewModel(private val iRepo: IRepo) : ViewModel() {
    private var _weatherData: MutableStateFlow<DatabaseState> =
        MutableStateFlow(DatabaseState.Loading)

    val weatherData: StateFlow<DatabaseState>
        get() = _weatherData

    init {
        getAllFavWeather()

    }

    fun getAllFavWeather() {
        _weatherData.value = DatabaseState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.getAllFavWeathers().catch { e ->
                _weatherData.value = DatabaseState.Failure(e.message.toString())
            }
                .collect { data ->
                    _weatherData.value = DatabaseState.Success(data)
                }
        }
    }

    fun deleteFavWeather(weather: DatabaseWeather) {
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.deleteWeather(weather)
        }
    }

    fun addFavWeather(weather: DatabaseWeather) {
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.addWeather(weather)
        }
    }

    fun disableScheduler(weather: DatabaseWeather) {
        viewModelScope.launch(Dispatchers.IO) {
            weather.isScheduled = false
            weather.time = ""
            iRepo.updateWeather(weather)
        }
    }
    fun setScheduler(weather: DatabaseWeather , time :String) {
        viewModelScope.launch(Dispatchers.IO) {
            weather.isScheduled = true
            weather.time = time

            iRepo.updateWeather(weather)
        }

    }
    fun getAlertItem(weather: DatabaseWeather):AlarmItem =  AlarmItem(
        LocalDateTime.now().plusSeconds(weather.time.toLong()),
        weather.lat.toString(),
        weather.lon.toString()
    )


}