package inc.moe.weather.datasource

import inc.moe.notesapp.database.IWeatherLocalSource
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.RemoteSource
import inc.moe.weather.repo.IRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeDatasource(var favWeather:MutableList<DatabaseWeather> = mutableListOf()) : IWeatherLocalSource {


    override suspend fun addWeather(weather: DatabaseWeather): Long {

        return if(favWeather.add(weather)){
            1L
        } else{
            0L
        }
    }

    override suspend fun deleteWeather(weather: DatabaseWeather): Int {

        return if(favWeather.remove(weather)){
            1
        } else{
            0
        }
    }



    override fun getAllFavWeathers(): Flow<List<DatabaseWeather>> = flow{
        val listFav:List<DatabaseWeather> = favWeather!!
        emit(listFav)

    }


}