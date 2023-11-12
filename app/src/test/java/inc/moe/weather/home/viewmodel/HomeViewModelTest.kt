package inc.moe.weather.home.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import inc.moe.weather.model.Current
import inc.moe.weather.model.Daily
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.FeelsLike
import inc.moe.weather.model.Hourly
import inc.moe.weather.model.Minutely
import inc.moe.weather.model.Temp
import inc.moe.weather.model.Weather
import inc.moe.weather.model.WeatherResponse
import inc.moe.weather.network.ApiState
import inc.moe.weather.repo.FakeRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    private lateinit var repo:FakeRepo
    private lateinit var homeViewModel: HomeViewModel
    private val weatherShouldBe: WeatherResponse = WeatherResponse(2.5, 2.5, "Alexandria", 25L,
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
        repo = FakeRepo()
        homeViewModel = HomeViewModel(repo)

    }
    @Test
    fun getCurrentWeather() = runBlockingTest{
        var result:WeatherResponse? = null
        val job = launch {
            homeViewModel.weatherResponse.collect{
                when(it){
                    is ApiState.Failure -> {
                    }

                    ApiState.Loading -> {}
                    is ApiState.Success -> {
                        result = it.weatherResponse
                        assertEquals(weatherShouldBe ,result)
                    }
                }
            }
        }
        job.cancel()
    }


}