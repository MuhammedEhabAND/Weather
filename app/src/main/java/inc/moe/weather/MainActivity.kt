package inc.moe.weather

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import inc.moe.weather.databinding.ActivityMainBinding

const val PERMISSION_ID = 44

class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var homeActivityViewBinding:ActivityMainBinding
    private lateinit var navController :NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(homeActivityViewBinding.root)

        navController = findNavController(this, R.id.nav_host_fragment)
        val bottomNavBar = homeActivityViewBinding.navigatorBar  as NavigationBarView
        setupWithNavController(bottomNavBar, navController)
        navController.addOnDestinationChangedListener(OnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
            Log.d(
                "Navigation",
                "Navigated to destination: " + destination.label
            )
        })





    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}








