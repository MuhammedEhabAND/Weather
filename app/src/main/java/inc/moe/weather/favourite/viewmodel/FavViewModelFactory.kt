package inc.moe.weather.favourite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import inc.moe.weather.repo.IRepo

class FavViewModelFactory (private val iRepo: IRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavViewModel::class.java)){
            FavViewModel(iRepo) as T
        }else{
            throw IllegalArgumentException("View model class not found ")
        }
    }
}