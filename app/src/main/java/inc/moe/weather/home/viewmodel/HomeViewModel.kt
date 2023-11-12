package inc.moe.weather.home.viewmodel

import android.hardware.biometrics.BiometricManager.Strings
import android.util.Log
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

    private var _weatherResponse: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)

    val weatherResponse: StateFlow<ApiState>
        get() = _weatherResponse



    fun getCurrentWeather(lat: String, lon: String, unit: String , language:String) {
        _weatherResponse.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.getWeather(lat, lon, unit ,language)
                .catch { e ->
                    _weatherResponse.value = ApiState.Failure(e.message.toString())
                }
                .collect {
                        data ->
                    _weatherResponse.value = ApiState.Success(data)
                    iRepo.cacheWeatherData(data)
                }
        }
    }

    fun getLocationPermission() {
        _weatherResponse.value = ApiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            iRepo.getWeather("null", "null", "null" ,"null")
                .catch { e ->
                    _weatherResponse.value = ApiState.Failure(e.message.toString() ,1)
                }
                .collect {
                        data ->
                    _weatherResponse.value = ApiState.Success(data)
                    iRepo.cacheWeatherData(data)
                }
        }
    }

    fun getCachedWeatherResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            if (iRepo.readCachedWeatherData() != null) {

                _weatherResponse.value = ApiState.Success(iRepo.readCachedWeatherData()!! ,true)
                Log.i("TAG", "getCachedWeatherResponse: success ")
            } else {
                _weatherResponse.value = ApiState.Failure(R.string.no_internet.toString() , 2)

                Log.i("TAG", "getCachedWeatherResponse: success ")
            }
        }
    }





}