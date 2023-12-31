package inc.moe.weather.map.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.ApiState
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.repo.IRepo
import inc.moe.weather.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MapViewModel(private val iRepo: IRepo) : ViewModel() {
    private var _weatherResponse: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val weatherResponse:StateFlow<ApiState>
        get() = _weatherResponse


     fun getWeather(lat :String , lon:String) {

        viewModelScope.launch(Dispatchers.IO) {
            Log.i("TAG", "getWeatherMethod: ")
            iRepo.getWeather(lat = lat , lon = lon , units = Constants.CURRENT_SELECTED_UNIT , language = Constants.CURRENT_LANGUAGE).catch {

                e->
                Log.i("TAG", "getWeather: ${e.message.toString()} ")
                    _weatherResponse.value = ApiState.Failure(e.message.toString())

            }
                .collect{
                    data->Log.i("TAG", "weatherResponse: ${data}")
                    val weather = convertWeatherResponseToDatabaseWeather(data)
                         iRepo.addWeather(weather)
                        _weatherResponse.value = ApiState.Success(data)




                }
        }
    }

    private fun convertWeatherResponseToDatabaseWeather(data: WeatherResponse):DatabaseWeather {
        val title = data.timezone
        val weatherType = data.current.weather.first().description
        val weatherDegree = data.current.temp
        val weatherImage =data.current.weather.first().icon
        return DatabaseWeather(data.lat ,data.lon ,title ,weatherType ,weatherDegree ,weatherImage )

    }


}