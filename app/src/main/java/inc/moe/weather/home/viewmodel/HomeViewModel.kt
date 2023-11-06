package inc.moe.weather.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.launch

class HomeViewModel(private val iRepo: IRepo) : ViewModel() {
    private var  _weatherResponse:MutableLiveData<WeatherResponse> = MutableLiveData()
    private var counter =  0
     var lat:String =String()
     var lon:String =String()
    val weatherResponse: LiveData<WeatherResponse>
        get() = _weatherResponse
    var isError = false
    var errorMessage:String = String()


    fun getCurrentWeather() {
    if(counter==0){
        viewModelScope.launch {
            if (iRepo.getWeather(lat = lat, lon = lon, units = "metric").isSuccessful&&counter==0){
                _weatherResponse.postValue(
                    iRepo.getWeather(lat = lat, lon = lon, units = "metric").body()
                )
                counter++
                isError=false
        }else{
                isError = true
                errorMessage = iRepo.getWeather(lat = lat , lon = lon, units="metric").message()
        }

        }
    }else{
        _weatherResponse.postValue(weatherResponse.value)
    }

    }


}