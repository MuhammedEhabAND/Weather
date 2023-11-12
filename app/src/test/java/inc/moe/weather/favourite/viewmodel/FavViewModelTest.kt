package inc.moe.weather.favourite.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
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
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.repo.FakeRepo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavViewModelTest {
    private lateinit var repo: FakeRepo
    private lateinit var favViewModel: FavViewModel
    private val databaseWeather1 = DatabaseWeather("123" , "321" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather2 = DatabaseWeather("123" , "123" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather3 = DatabaseWeather("123" , "2021" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather5 = DatabaseWeather("123" , "3201" ,"Alexandria" , "Clear", 2.2,"image")
    private val databaseWeather4 = DatabaseWeather("123" , "32001" ,"Alexandria" , "Clear", 2.2,"image")
    private val localWeather = mutableListOf<DatabaseWeather>(databaseWeather1 ,databaseWeather2 , databaseWeather3 , databaseWeather4 ,databaseWeather5)

    private var weatherResponse: WeatherResponse = WeatherResponse(2.5, 2.5, "Alexandria", 25L,
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

    @Before
    fun setup(){
        repo = FakeRepo(  )
        favViewModel = FavViewModel(repo)
    }



    @Test
    fun getAllFavWeather() = runBlockingTest {

        var weatherData :List<DatabaseWeather> = emptyList()
        val job =launch {
            favViewModel.weatherData.collect{
                when(it){
                    is DatabaseState.Failure -> {

                    }

                    DatabaseState.Loading -> {

                    }
                    is DatabaseState.Success -> {
                        weatherData = it.weatherResponse
                    }


                }
            }
        }
        assertEquals(localWeather,weatherData)
        job.cancel()
    }

    @Test
    fun deleteFavWeather() = runBlockingTest {
        val job = launch {
            favViewModel.deleteFavWeather(databaseWeather1)
        }
        assertEquals(1, repo.deleteWeather(databaseWeather1))
        job.cancel()
    }

    @Test
    fun addFavWeather()=runBlockingTest {
        val job = launch {
            favViewModel.addFavWeather(databaseWeather1)
        }
        assertEquals(1L  , repo.addWeather(databaseWeather1))
        job.cancel()

    }
}