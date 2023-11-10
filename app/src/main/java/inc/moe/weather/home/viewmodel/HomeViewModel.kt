package inc.moe.weather.home.viewmodel

import android.hardware.biometrics.BiometricManager.Strings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.google.android.gms.common.api.Api
import inc.moe.weather.R
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.API
import inc.moe.weather.network.ApiState
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(private val iRepo: IRepo) : ViewModel() {
    var isPermissionGranted: Boolean = false
    private var _weatherResponse: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)



    val weatherResponse: StateFlow<ApiState>
        get() = _weatherResponse
    private  var _weatherCachedResponse:MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val weatherCachedResponse: StateFlow<ApiState>
        get()=_weatherCachedResponse


    fun getCurrentWeather(lat:String ,lon:String , unit :String) {
        viewModelScope.launch(Dispatchers.IO) {
           resetWeatherResponse()
            if (isPermissionGranted) {


                iRepo.getWeather(lat = lat, lon = lon, units = unit)
                .catch { e ->
                        _weatherResponse.value = ApiState.Failure(e.message.toString())
                    }
                .collect {
                        data ->
                        _weatherResponse.value = ApiState.Success(data)
                        iRepo.cacheWeatherData(weatherData =  data)

                    }
            } else {
                resetWeatherResponse()
                _weatherResponse.value = ApiState.Failure(R.string.no_location_permission.toString())
            }


        }
    }

    fun getCachedWeatherResponse(){
        viewModelScope.launch(Dispatchers.IO) {
            if(iRepo.readCachedWeatherData()!=null) {
                _weatherCachedResponse.value = ApiState.Success(iRepo.readCachedWeatherData()!!)
            }else{
                _weatherCachedResponse.value = ApiState.Failure("there is no internet to fetch the weather data")

            }
        }
    }


    fun resetWeatherResponse() {
        _weatherResponse = MutableStateFlow(ApiState.Loading)

    }


}