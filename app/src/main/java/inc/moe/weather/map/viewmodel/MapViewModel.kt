package inc.moe.weather.map.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.ApiState
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.repo.IRepo
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
            iRepo.getWeather(lat = lat , lon = lon , units = "metric").catch {

                e->
                Log.i("TAG", "getWeather: ${e.cause?.message.toString()} ")
                    _weatherResponse.value = ApiState.Failure(e.message.toString())

            }
                .collect{
                    data->Log.i("TAG", "weatherResponse: ${data}")
                    val weather =convertWeatherResponseToDatabaseWeather(data , lon , lat)
                         iRepo.addWeather(weather)
                        _weatherResponse.value = ApiState.Success(data)




                }
        }
    }

    private fun convertWeatherResponseToDatabaseWeather(data: WeatherResponse ,lon:String ,lat:String):DatabaseWeather {
        val title = data.timezone
        val weatherType = data.current.weather.first().main
        val weatherDegree = data.current.temp
        val weatherImage =data.current.weather.first().icon
        return DatabaseWeather(lon ,lat ,title ,weatherType ,weatherDegree ,weatherImage )

    }


}