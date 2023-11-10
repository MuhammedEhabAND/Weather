package inc.moe.weather.datasource

import inc.moe.notesapp.database.IWeatherLocalSource
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.RemoteSource
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeDatasource(var favWeather:MutableList<DatabaseWeather>? = mutableListOf()) : IWeatherLocalSource ,RemoteSource {


    override suspend fun addWeather(weather: DatabaseWeather): Long {
        favWeather?.add(weather)
        if(favWeather?.contains(weather) == true){
            return 1L
        } else{
            return 0L
        }
    }

    override suspend fun deleteWeather(weather: DatabaseWeather): Int {
        favWeather?.remove(weather)
        if(favWeather?.contains(weather) == true){
            return 0
        } else{
            return 1
        }
    }



    override fun getAllFavWeathers(): Flow<List<DatabaseWeather>> = flow{
        val listFav:List<DatabaseWeather> = favWeather!!
        emit(listFav)

    }

    override suspend fun getWeather(lon: String, lat: String, units: String): WeatherResponse {
        TODO("Not yet implemented")
    }


}