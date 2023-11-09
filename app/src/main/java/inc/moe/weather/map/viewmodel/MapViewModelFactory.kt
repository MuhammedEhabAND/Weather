package inc.moe.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.moe.weather.repo.IRepo

class MapViewModelFactory (private val iRepo: IRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(iRepo) as T
        }else{
            throw IllegalArgumentException("View model class not found ")
        }
    }
}