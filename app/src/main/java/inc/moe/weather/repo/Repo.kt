package inc.moe.weather.repo

import android.util.Log
import inc.moe.notesapp.database.IWeatherLocalSource
import inc.moe.weather.utils.Constants
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.RemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: IWeatherLocalSource,
) : IRepo {

    companion object {
        @Volatile
        private var INSTANCE: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: IWeatherLocalSource): Repo {
            return INSTANCE ?: synchronized(this) {
                val instance = Repo(remoteSource, localSource)
                INSTANCE = instance
                instance
            }

        }
    }

    override suspend fun addWeather(weather: DatabaseWeather):Long {
      return  localSource.addWeather(weather)
    }

    override suspend fun deleteWeather(weather: DatabaseWeather):Int {
       return localSource.deleteWeather(weather)
    }

    override fun getAllFavWeathers(): Flow<List<DatabaseWeather>> =localSource.getAllFavWeathers()




    override fun getWeather(lon: String, lat: String, units: String , language:String): Flow<WeatherResponse> = flow {
            val weatherResponse = remoteSource.getWeather(lon,lat,units ,language)
            emit(weatherResponse)


    }

    override suspend fun cacheWeatherData(weatherData: WeatherResponse?) {
        val file = File(Constants.cacheDirectory, Constants.CACHE_FILE_NAME)
        withContext(Dispatchers.IO) {
            val fileOutputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            try {
                objectOutputStream.writeObject(weatherData)
                objectOutputStream.flush()
                fileOutputStream.flush()
                objectOutputStream.close()
                fileOutputStream.close()
                Log.i("TAG", "cacheWeatherData: finished")
            } catch (e: Exception) {
                Log.i("TAG", "caching exception: $e")
            }
        }
    }

     override suspend fun readCachedWeatherData(): WeatherResponse? {
        val file = File(Constants.cacheDirectory, Constants.CACHE_FILE_NAME)
        return try {
            withContext(Dispatchers.IO) {
                val fileInputStream = FileInputStream(file)
                val objectInputStream = ObjectInputStream(fileInputStream)
                val weatherData = objectInputStream.readObject() as WeatherResponse
                Log.i("TAG", "readCachedWeatherData: success")
                weatherData
            }
        } catch (e: Exception) {
            Log.i("TAG", "readCachedWeatherData exception: $e")
            null
        }
    }

}