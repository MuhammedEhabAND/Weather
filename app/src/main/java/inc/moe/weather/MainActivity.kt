package inc.moe.weather

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import inc.moe.weather.databinding.ActivityMainBinding
import inc.moe.weather.utils.SettingsData
import inc.moe.weather.utils.Constants

const val PERMISSION_ID = 44

class MainActivity : AppCompatActivity() {

    private lateinit var homeActivityViewBinding:ActivityMainBinding
    private lateinit var navController :NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsData.loadSettings(context = applicationContext)
        homeActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeActivityViewBinding.root)

        Constants.cacheDirectory = this.cacheDir.absolutePath.toString()
        navController = findNavController(this, R.id.nav_host_fragment)
        val bottomNavBar = homeActivityViewBinding.navigatorBar  as NavigationBarView
        setupWithNavController(bottomNavBar, navController)
        navController.addOnDestinationChangedListener(OnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
            Log.d(
                "Navigation",
                "Navigated to destination: " + destination.label
            )
        })

        animateNavBar()




    }

    private fun animateNavBar() {
        homeActivityViewBinding.navigatorBar.postDelayed({
            homeActivityViewBinding.navigatorBar.translationY = homeActivityViewBinding.navigatorBar.height.toFloat()
            homeActivityViewBinding.navigatorBar.animate()

                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}








