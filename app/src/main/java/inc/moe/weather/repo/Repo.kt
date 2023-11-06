package inc.moe.weather.repo

import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.RemoteSource

class Repo private constructor(var remoteSource: RemoteSource)  : IRepo {

    companion object{
        @Volatile
        private var INSTANCE : Repo? = null
        fun getInstance (remoteSource: RemoteSource): Repo {
            return INSTANCE ?: synchronized(this){
                val instance = Repo( remoteSource)
                INSTANCE= instance
                instance
            }

        }
    }

    override suspend fun getWeather(lon: String, lat: String, units:String) = remoteSource.getWeather(lon,lat,units)


}