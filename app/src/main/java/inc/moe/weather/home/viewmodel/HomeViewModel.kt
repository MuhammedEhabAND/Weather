package inc.moe.weather.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.API
import inc.moe.weather.network.ApiState
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val iRepo: IRepo) : ViewModel() {
    private var  _weatherResponse:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
     var unit:String ="metric"
     var lat:String =String()
     var lon:String =String()
    val weatherResponse: StateFlow<ApiState>
        get() = _weatherResponse



    fun getCurrentWeather() {
        viewModelScope.launch {
            iRepo.getWeather(lat=lat , lon = lon , units = unit)
                .catch {
                    e->_weatherResponse.value = ApiState.Failure(e.message.toString())
                }
                .collect{
                    data->_weatherResponse.value = ApiState.Success(data)
                }
        }

    }
    fun resetWeatherResponse(){
        _weatherResponse = MutableStateFlow(ApiState.Loading)
        getCurrentWeather()
    }


}