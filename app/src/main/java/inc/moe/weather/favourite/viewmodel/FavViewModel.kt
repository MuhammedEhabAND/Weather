package inc.moe.weather.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.Api
import inc.moe.weather.R
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.Weather
import inc.moe.weather.network.ApiState
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavViewModel(private val iRepo: IRepo) : ViewModel() {
    private var _weatherData: MutableStateFlow<DatabaseState> = MutableStateFlow(DatabaseState.Loading)

    val weatherData: StateFlow<DatabaseState>
        get() = _weatherData

    init {
        getAllFavWeather()

    }

    fun getAllFavWeather() {
        _weatherData.value= DatabaseState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.getAllFavWeathers().catch {
                e->_weatherData.value = DatabaseState.Failure(e.message.toString())
            }
                .collect{
                    data->_weatherData.value = DatabaseState.Success(data)
                }
        }
    }
     fun deleteFavWeather(weather: DatabaseWeather){
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.deleteWeather(weather)
        }
    }
    fun addFavWeather(weather: DatabaseWeather){
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.addWeather(weather)
        }
    }


}