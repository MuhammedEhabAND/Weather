package inc.moe.weather.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.Constants
import inc.moe.weather.PERMISSION_ID
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentHomeBinding
import inc.moe.weather.getDate
import inc.moe.weather.getDateForHourly
import inc.moe.weather.getImage
import inc.moe.weather.home.viewmodel.HomeViewModel
import inc.moe.weather.home.viewmodel.HomeViewModelFactory
import inc.moe.weather.model.SettingsData
import inc.moe.weather.network.ApiState
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewBinding: FragmentHomeBinding
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var fragmentView: View
    private lateinit var lat: String
    private lateinit var lon: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        homeViewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        // Load the settings initially
        val settingsList = SettingsData.settingsList


        for (setting in settingsList) {
            when (setting.title) {
                R.string.location -> {
                    if (setting.isOptionOne) {

                    } else {

                    }
                }
                R.string.language -> {
                    if (setting.isOptionOne) {

                    } else {

                    }
                }
                R.string.wind_speed_unit , R.string.temp ->{
                    if(setting.isOptionOne){
                        Constants.CURRENT_SELECTED_UNIT = Constants.UNITS_METER
                        Constants.CURRENT_WIND_UNIT = Constants.METER_PER_SECONDS
                        Constants.CURRENT_WEATHER_UNIT = Constants.CELISUIS
                    }else{

                        Constants.CURRENT_SELECTED_UNIT = Constants.UNITS_IMPERIAL
                        Constants.CURRENT_WIND_UNIT = Constants.MILE_PER_HOUR
                        Constants.CURRENT_WEATHER_UNIT = Constants.FAHERNHEIT
                    }
                }

            }
        }

        return homeViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentView = view
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(
                Repo.getInstance(
                    WeatherClient(),
                    WeatherLocalSource.getInstance(requireContext())
                )

            )
        )


            .get(HomeViewModel::class.java)

        dailyAdapter = DailyAdapter()
        homeViewBinding.daysRv.adapter = dailyAdapter
        hourlyAdapter = HourlyAdapter()
        homeViewBinding.hourRv.adapter = hourlyAdapter
        homeViewBinding.root.postDelayed({ loadData(lat ,lon) }, 1000)
        homeViewBinding.swipeToRefresh.setOnRefreshListener {
            loadData(lat,lon)
        }
    }

    private fun loadData(lat : String , lon:String) {

        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo
        if(homeViewModel.isPermissionGranted){
            if (networkInfo != null && networkInfo.isConnected) {
                homeViewModel.getCurrentWeather(lat , lon , Constants.CURRENT_SELECTED_UNIT)

                lifecycleScope.launch {
                    homeViewModel.weatherResponse.collectLatest { result ->
                        when (result) {
                            is ApiState.Success -> {

                                homeViewBinding.swipeToRefresh.isRefreshing = false
                                showData(result)

                            }

                            is ApiState.Failure -> {
                                homeViewBinding.swipeToRefresh.isRefreshing = false
                                showError(result)


                            }

                            is ApiState.Loading -> {
                                homeViewBinding.swipeToRefresh.isRefreshing = true
                                showLoading()
                            }

                        }
                    }
                }


            } else {
                homeViewModel.getCachedWeatherResponse()
                lifecycleScope.launch {
                    homeViewModel.weatherCachedResponse.collectLatest {result->
                        when(result){
                            is ApiState.Loading->{
                                showLoading()
                            }
                            is ApiState.Failure->{
                                homeViewBinding.swipeToRefresh.isRefreshing = false

                                showError(result)
                            }
                            is ApiState.Success->{
                                homeViewBinding.swipeToRefresh.isRefreshing = false

                                showData(result , true)
                            }
                        }
                    }
                }


            }

        }
    }

    private fun showLoading() {

        homeViewBinding.swipeToRefresh.isRefreshing = true
        homeViewBinding.errorCardView.visibility = View.GONE
        homeViewBinding.scrollView.visibility = View.GONE


    }

    private fun showError(result: ApiState.Failure) {
        animateFailureView()
        homeViewBinding.errorCardView.visibility = View.VISIBLE
        homeViewBinding.mapIcon.setImageResource(R.drawable.baseline_error_24)
        homeViewBinding.timeRegion.text = "Something Wrong Happens"
        homeViewBinding.scrollView.visibility = View.GONE

        if (homeViewModel.isPermissionGranted) {

            homeViewBinding.errorMessage.text = result.message

            homeViewBinding.retryBtn.setOnClickListener {

                loadData(lat , lon)


            }
        } else {
            homeViewBinding.errorMessage.text = getString(result.message.toInt())
            homeViewBinding.retryBtn.text = getString(R.string.allow_btn)
            homeViewBinding.retryBtn.setOnClickListener {
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts("package", requireContext().packageName, null)
                startActivity(intent)
            }
        }
    }

    private fun animateFailureView() {

        homeViewBinding.cardView.postDelayed({
            homeViewBinding.cardView.translationY = homeViewBinding.cardView.height.toFloat()
            homeViewBinding.cardView.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)

        homeViewBinding.errorCardView.postDelayed({
            homeViewBinding.errorCardView.translationY =
                homeViewBinding.errorCardView.height.toFloat()
            homeViewBinding.errorCardView.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
    }

    private fun showData(result: ApiState.Success, isCached: Boolean = false) {

        animateSuccessViews()
        if (isCached) {
            Snackbar.make(
                requireView(),
                requireContext().getString(R.string.snack_bar_no_network),
                Snackbar.LENGTH_SHORT
            ).show()
        }
        homeViewBinding.mapIcon.setImageResource(R.drawable.baseline_my_location_24)
        homeViewBinding.errorCardView.visibility = View.GONE
        homeViewBinding.scrollView.visibility = View.VISIBLE
        homeViewBinding.weatherDegree.text =
            "${result.weatherResponse.current.temp.toInt()}${Constants.CURRENT_WEATHER_UNIT}"
        homeViewBinding.timeRegion.text = result.weatherResponse.timezone
        homeViewBinding.weatherType.text = result.weatherResponse.current.weather.first().main
        homeViewBinding.date.text = getDate(result.weatherResponse.current.dt)
        homeViewBinding.day.text = getDateForHourly(result.weatherResponse.current.dt)
        homeViewBinding.humidityText.text = "${result.weatherResponse.current.humidity} %"
        homeViewBinding.pressureText.text = "${result.weatherResponse.current.pressure} hpa"
        homeViewBinding.windText.text = "${result.weatherResponse.current.wind_speed} ${Constants.CURRENT_WIND_UNIT}"
        homeViewBinding.cloudText.text = "${result.weatherResponse.current.clouds} %"
        homeViewBinding.uviText.text = "${result.weatherResponse.current.uvi} "
        homeViewBinding.visibilityText.text = "${result.weatherResponse.current.visibility} m"
        Glide.with(requireContext())
            .load(getImage(result.weatherResponse.current.weather.first().icon, 4))
            .placeholder(R.drawable.place_holder)
            .error(R.drawable.error_weather)
            .into(homeViewBinding.weatherImage)
        dailyAdapter.submitList(result.weatherResponse.daily)
        hourlyAdapter.submitList(result.weatherResponse.hourly)

    }

    private fun animateSuccessViews() {
        homeViewBinding.cardView.postDelayed({
            homeViewBinding.cardView.translationY = homeViewBinding.cardView.height.toFloat()
            homeViewBinding.cardView.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)

        homeViewBinding.weatherType.postDelayed({
            homeViewBinding.weatherType.translationX =
                homeViewBinding.weatherType.width.toFloat()
            homeViewBinding.weatherType.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)


        homeViewBinding.daysRv.postDelayed({
            homeViewBinding.daysRv.translationY = homeViewBinding.daysRv.height.toFloat()
            homeViewBinding.daysRv.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
        homeViewBinding.moreDetails.postDelayed({
            homeViewBinding.moreDetails.translationX =
                homeViewBinding.moreDetails.width.toFloat()
            homeViewBinding.moreDetails.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)
        homeViewBinding.hourlyTitle.postDelayed({
            homeViewBinding.hourlyTitle.translationX =
                homeViewBinding.hourlyTitle.width.toFloat()
            homeViewBinding.hourlyTitle.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)
        homeViewBinding.cardView2.postDelayed({
            homeViewBinding.cardView2.translationY = homeViewBinding.cardView2.height.toFloat()
            homeViewBinding.cardView2.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
        homeViewBinding.dailyTitle.postDelayed({
            homeViewBinding.dailyTitle.translationX = homeViewBinding.dailyTitle.width.toFloat()
            homeViewBinding.dailyTitle.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)

        homeViewBinding.hourRv.postDelayed({
            homeViewBinding.hourRv.translationX = homeViewBinding.hourRv.width.toFloat()
            homeViewBinding.hourRv.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)

        homeViewBinding.weatherDegree.postDelayed({
            homeViewBinding.weatherDegree.translationX =
                homeViewBinding.weatherDegree.width.toFloat()
            homeViewBinding.weatherDegree.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)
        homeViewBinding.weatherImage.postDelayed({
            homeViewBinding.weatherImage.translationX =
                -homeViewBinding.weatherImage.width.toFloat()
            homeViewBinding.weatherImage.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)

    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
             lon = mLastLocation.longitude.toString()
             lat = mLastLocation.latitude.toString()
//            getStreetName(mLastLocation.latitude, mLastLocation.longitude)

            loadData(lat , lon)

        }

    }

    private fun getStreetName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val streetAddress = addresses[0].getAddressLine(0)
                } else {

                }
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()

    }

    private fun checkPermissions(): Boolean {
        val result = ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return result
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLocation()

            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                homeViewModel.isPermissionGranted = true
                requestNewLocationData()

            } else {
                homeViewModel.isPermissionGranted = false
                Toast.makeText(requireContext(), "Turn On Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        mLocationRequest.setInterval(60000)
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )


    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }


}