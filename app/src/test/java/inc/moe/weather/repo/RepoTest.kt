package inc.moe.weather.repo

import inc.moe.weather.datasource.FakeDatasource
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.Weather
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepoTest{
    val databaseWeather1 = DatabaseWeather("123" , "321" ,"Alexandria" , "Clear", 2.2,"image")
    val databaseWeather2 = DatabaseWeather("123" , "123" ,"Alexandria" , "Clear", 2.2,"image")
    val databaseWeather3 = DatabaseWeather("123" , "2021" ,"Alexandria" , "Clear", 2.2,"image")
    val databaseWeather5 = DatabaseWeather("123" , "3201" ,"Alexandria" , "Clear", 2.2,"image")
    val databaseWeather4 = DatabaseWeather("123" , "32001" ,"Alexandria" , "Clear", 2.2,"image")
    val localWeather = mutableListOf<DatabaseWeather>(databaseWeather1 ,databaseWeather2 , databaseWeather3 , databaseWeather4 ,databaseWeather5)
    val remoteSource = mutableListOf<DatabaseWeather>(databaseWeather1 ,databaseWeather2 , databaseWeather3 , databaseWeather4 ,databaseWeather5)
    lateinit var fakeDatasource: FakeDatasource
    lateinit var fakeRemoteDatasource: FakeDatasource
    lateinit var repo:Repo
    @Before
    fun setup(){
        fakeDatasource = FakeDatasource(localWeather)
        fakeRemoteDatasource =FakeDatasource(remoteSource)
        repo = Repo.getInstance(fakeRemoteDatasource , fakeDatasource  )
    }
    @Test
     fun  addWeather() = runBlocking{
        val testingWeather= DatabaseWeather("123" , "3200001" ,"Alexandria" , "Clear", 2.2,"image")
        val result = repo.addWeather(testingWeather)
        assertEquals(result , 1L)
    }

    @Test
     fun  delete()= runBlocking{


        val result = repo.deleteWeather(databaseWeather4)
        assertEquals(result , 1)
    }

}