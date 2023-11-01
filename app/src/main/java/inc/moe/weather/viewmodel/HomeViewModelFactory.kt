package inc.moe.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.moe.weather.repo.IRepo

class HomeViewModelFactory (private val iRepo: IRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(iRepo) as T
        }else{
            throw IllegalArgumentException("View model class not found ")
        }
    }
}