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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepoTest {
    private var weatherResponse: WeatherResponse = WeatherResponse(2.5, 2.5, "Alexandria", 25L,
        Current(15L, 15L, 15, 15.5, 15.5, 15L, 15L, 15.5, 15.5, 15L, 15L, 15.5, 15L, 15.5,
            listOf(Weather(1L ,"" , "" , ""))
        ), listOf(Minutely(15L,15L)),
        null,
        listOf(Hourly(15L , 15.5, 15.5, 15L , 15L , 15.5, 15.5, 15L, 15L, 15.5, 15L, 15.5,
            listOf(Weather(1L,"","","")), 15.5))
        , listOf(Daily(1L, 15.5, 15.5, 15.15, 15.5, 15.5,
            Temp(15.5,15.5,15.5,15.5,15.5,15.5),
            FeelsLike(15.5,15.5,15.5,15.5),
            15.5, 15.5,15.5,15.5,15.5,15.5,
            listOf(Weather(1L,"", "", "")), 15.5,15.15,15.5)))

    private val databaseWeather1 = DatabaseWeather(12.2 , 12.2 ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather2 = DatabaseWeather(12.2 , 12.12 ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather3 = DatabaseWeather(12.12 , 12.12 ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather5 = DatabaseWeather(12.12 , 12.12 ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather4 = DatabaseWeather(12.12 , 12.12 ,"Alexandria" , "Clear", 2.2,"image")
    private val localWeather = mutableListOf<DatabaseWeather>(databaseWeather1 ,databaseWeather2 , databaseWeather3 , databaseWeather4 ,databaseWeather5)
    private lateinit var fakeDatasource: FakeDatasource
    private lateinit var fakeRemoteDatasource: FakeRemoteDatasource
    private lateinit var repo:Repo
    @Before
    fun setup(){
        fakeDatasource = FakeDatasource(localWeather)
        fakeRemoteDatasource = FakeRemoteDatasource(weatherResponse)
        repo = Repo.getInstance(fakeRemoteDatasource , fakeDatasource  )
    }
    @Test
     fun  addWeather_Not_Exist() = runBlocking{
        val testingWeather= DatabaseWeather(12.2 , 12.2 ,"Alexandria" , "Clear", 2.2,"image")
        val result = repo.addWeather(testingWeather)
        assertEquals(result , 1L)
    }

    @Test
    fun  addWeather_Exist() = runBlocking{
        val result = repo.addWeather(databaseWeather1)
        assertEquals(result , 1L)
    }

    @Test
     fun  delete_Exist()= runBlocking{
        val result = repo.deleteWeather(databaseWeather4)
        assertEquals(result , 1)
    }
    @Test
     fun  delete_Not_Exist()= runBlocking{
        val testingWeather= DatabaseWeather(12.12 , 22.2 ,"Alexandria" , "Clear", 2.2,"image")
        val result = repo.deleteWeather(testingWeather)
        assertEquals(result , 0)
    }
    @Test
    fun getAllFavWeather() = runBlocking{
        val flow = flowOf(fakeDatasource)
        val result = flow.toList()
        assertEquals(listOf(fakeDatasource) , result)
    }

    @Test
    fun getWeather() = runBlocking {
        val flow = flowOf(fakeRemoteDatasource)
        val result = flow.toList()
        assertEquals(listOf(fakeRemoteDatasource) ,result)
    }


}