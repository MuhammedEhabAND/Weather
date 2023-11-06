package inc.moe.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import inc.moe.weather.home.viewmodel.HomeViewModel
import inc.moe.weather.home.viewmodel.HomeViewModelFactory
import java.io.IOException
import java.util.Locale

const val PERMISSION_ID = 44

class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }









//     fun onMapClicked(view: View){
//
//        var gmmIntentUri = Uri.parse("geo:0,0?q=${streetTxt.text}")
//        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
//        startActivity(mapIntent)
//    }
//    fun onSendClicked(view : View){
//        val phoneNumber = "+201274780145"
//        val messageText = "Hello, this is my address ${streetTxt.text}"
//
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse("smsto:$phoneNumber")
//        intent.putExtra("sms_body", messageText)
//        startActivity(intent)
//
//    }

}