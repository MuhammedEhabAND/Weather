package inc.moe.weather.utils

import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import java.io.IOException
import java.util.Locale

data class LocationInfo(val streetAddress: String, val cityName: String)

 fun getLocationInfo(latitude: Double, longitude: Double, context: Context): LocationInfo {
    val geocoder = Geocoder(context, Locale.getDefault())

    try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val streetAddress = addresses[0].getAddressLine(0)
                val cityName = addresses[0].locality
               if(!cityName.isNullOrEmpty())
                return LocationInfo(streetAddress, cityName)
            } else {
                // Handle the case when no address is found
            }
        }
    } catch (e: IOException) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }

    return LocationInfo("wrong" ,"wrong") // Return null if there's an error or no address is found
}
