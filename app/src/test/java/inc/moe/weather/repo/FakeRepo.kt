package inc.moe.weather.repo

import inc.moe.weather.datasource.FakeDatasource
import inc.moe.weather.datasource.FakeRemoteDatasource
import inc.moe.weather.model.Current
import inc.moe.weather.model.Daily
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.FeelsLike
import inc.moe.weather.model.Hourly
import inc.moe.weather.model.Minutely
import inc.moe.weather.model.Temp
import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before

class FakeRepo:IRepo {


    private val databaseWeather1 = DatabaseWeather("123" , "321" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather2 = DatabaseWeather("123" , "123" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather3 = DatabaseWeather("123" , "2021" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather5 = DatabaseWeather("123" , "3201" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather4 = DatabaseWeather("123" , "32001" ,"Alexandria" , "Clear", 2.2,"image")
    private val localWeather = mutableListOf<DatabaseWeather>(databaseWeather1 ,databaseWeather2 , databaseWeather3 , databaseWeather4 ,databaseWeather5)


    private val weatherResponse: WeatherResponse = WeatherResponse(2.5, 2.5, "Alexandria", 25L,
        Current(15L, 15L, 15, 15.5, 15.5, 15L, 15L, 15.5, 15.5, 15L, 15L, 15.5, 15L, 15.5,
            listOf(Weather(1L ,"" , "" , ""))
        ), listOf(Minutely(15L,15L)),
        listOf(
            Hourly(15L , 15.5, 15.5, 15L , 15L , 15.5, 15.5, 15L, 15L, 15.5, 15L, 15.5,
            listOf(Weather(1L,"","","")), 15.5)
        )
        , listOf(
            Daily(1L, 15.5, 15.5, 15.15, 15.5, 15.5,
            Temp(15.5,15.5,15.5,15.5,15.5,15.5),
            FeelsLike(15.5,15.5,15.5,15.5),
            15.5, 15.5,15.5,15.5,15.5,15.5,
            listOf(Weather(1L,"", "", "")), 15.5,15.15,15.5)
        ))



    override suspend fun addWeather(weather: DatabaseWeather): Long {
        val fakeLocal = FakeDatasource(localWeather)
        return fakeLocal.addWeather(weather)
    }

    override suspend fun deleteWeather(weather: DatabaseWeather): Int {
        val fakeLocal = FakeDatasource(localWeather)
        return fakeLocal.deleteWeather(weather)
    }


    override fun getAllFavWeathers(): Flow<List<DatabaseWeather>> = flow {
        emit(localWeather)
    }



    override fun getWeather(
        lon: String,
        lat: String,
        units: String,
        language: String,
    ): Flow<WeatherResponse> = flow{

        emit(weatherResponse)
    }

    override suspend fun cacheWeatherData(weatherData: WeatherResponse?) {
        TODO("Not yet implemented")
    }

    override suspend fun readCachedWeatherData(): WeatherResponse? {
        TODO("Not yet implemented")
    }

}