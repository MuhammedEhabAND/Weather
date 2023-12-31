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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.PERMISSION_ID
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentHomeBinding
import inc.moe.weather.home.viewmodel.HomeViewModel
import inc.moe.weather.home.viewmodel.HomeViewModelFactory
import inc.moe.weather.network.ApiState
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import inc.moe.weather.utils.Constants
import inc.moe.weather.utils.SettingsData
import inc.moe.weather.utils.getDate
import inc.moe.weather.utils.getDateForHourly
import inc.moe.weather.utils.getImage
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment() {

    private var isLoadingData = false
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewBinding: FragmentHomeBinding
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var fragmentView: View
    private lateinit var receivedLat: String
    private lateinit var receivedLon: String
    private var lat: String = "null"
    private var lon: String = "null"
    private var isNetworkOn: Boolean = false
    private var isLocationGot: Boolean = false
    private var isLocationPermissionRequested: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        arguments?.let {
            receivedLat = it.getString("lat").toString()
            receivedLon = it.getString("lon").toString()
        }
        Log.i("TAG", "onViewCreated: $receivedLat ,$receivedLon")

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        homeViewBinding = FragmentHomeBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        // Load the settings initially
        homeViewBinding.editIcon.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), homeViewBinding.editIcon)


            popupMenu.menuInflater.inflate(R.menu.edit_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.mapFragment -> {
                        findNavController().navigate(item.itemId)
                        true
                    }
                    // Handle other menu items as needed
                    else -> false
                }
            }


            popupMenu.show()

            SettingsData.loadSettings(requireContext())
            val settingsList =SettingsData.settingsList

                for (setting in settingsList) {
                when (setting.title) {


                    R.string.language -> {
                        if (setting.isOptionOne) {
                            Constants.CURRENT_LANGUAGE ="en"
                        } else {

                            Constants.CURRENT_LANGUAGE ="ar"

                        }
                    }

                }
            }
        }


        return homeViewBinding.root
    }

    override fun onStart() {
        super.onStart()

        getLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =requireActivity().getSharedPreferences("Wind", Context.MODE_PRIVATE)
        Constants.CURRENT_LANGUAGE = sharedPreferences.getString("current language" ,"en" ).toString()
        Constants.CURRENT_SELECTED_UNIT = sharedPreferences.getString("current unit" , Constants.UNITS_METER).toString()
        Constants.CURRENT_WEATHER_UNIT = sharedPreferences.getString("current weather" , Constants.CELISUIS).toString()
        Constants.CURRENT_WIND_UNIT = sharedPreferences.getString("current wind" , Constants.METER_PER_SECONDS).toString()
        Log.i("TAG",  "selected weather ${Constants.CURRENT_SELECTED_UNIT}")
        Log.i("TAG", "selected weather ${Constants.CURRENT_WEATHER_UNIT}")
        Log.i("TAG",  "selected weather ${Constants.CURRENT_WIND_UNIT}")



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




        homeViewBinding.swipeToRefresh.setOnRefreshListener {
            loadData()
            if(isLocationEnabled()){
                isLocationGot = true
            }
        }

    }

    private fun loadData() {
        isLoadingData = true

        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo

        lifecycleScope.launch {

            if (networkInfo != null && networkInfo.isConnected) {
                isNetworkOn = true
                if (isLocationGot) {

                    homeViewModel.getCurrentWeather(lat, lon, Constants.CURRENT_SELECTED_UNIT , Constants.CURRENT_LANGUAGE)
                    Log.i("TAG", "getCurrentWeather: ")
                } else {
                    homeViewModel.getLocationPermission()
                }
            } else {
                isNetworkOn = false
                homeViewModel.getCachedWeatherResponse()
                Log.i("TAG", "getCachedWeather: ")
            }

            homeViewModel.weatherResponse.collect { result ->
                when (result) {
                    is ApiState.Success -> {
                        homeViewBinding.swipeToRefresh.isRefreshing = false
                        showData(result, result.isCached)
                        isLoadingData = false
                    }

                    is ApiState.Failure -> {
                        homeViewBinding.swipeToRefresh.isRefreshing = false
                        showError(result, result.isCached)
                        isLoadingData = true
                    }

                    is ApiState.Loading -> {
                        homeViewBinding.swipeToRefresh.isRefreshing = true
                        showLoading()
                    }
                }
            }
        }

        isLoadingData = false
    }

    private fun showLoading() {

        homeViewBinding.swipeToRefresh.isRefreshing = true
        homeViewBinding.errorCardView.visibility = View.GONE
        homeViewBinding.scrollView.visibility = View.GONE


    }

    private fun showError(result: ApiState.Failure, isNetworkOn: Int) {


        Log.i("TAG", "showError: ")
        val idOfErrorMessage = R.string.error_message
        val errorMessage = getString(idOfErrorMessage)

        animateFailureView()
        homeViewBinding.root.postDelayed({
            homeViewBinding.errorCardView.visibility = View.VISIBLE
            homeViewBinding.mapIcon.setImageResource(R.drawable.baseline_error_24)
            homeViewBinding.timeRegion.text = errorMessage
            homeViewBinding.scrollView.visibility = View.GONE

            when (isNetworkOn) {
                //api failure
                0 ->{
                    homeViewBinding.errorMessage.text = result.message
                    homeViewBinding.retryBtn.text = getString(R.string.try_again)
                    homeViewBinding.retryBtn.setOnClickListener {
                        Log.i("TAG", "calledFromRefresh: ")
                        loadData()
                    }


                }
                //location permission failure
                1 ->{
                    homeViewBinding.errorMessage.text = getString(R.string.no_location_permission)
                    homeViewBinding.retryBtn.text = getString(R.string.allow_btn)
                    homeViewBinding.retryBtn.setOnClickListener {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.data = Uri.fromParts("package", requireContext().packageName, null)
                        startActivity(intent)
                    }

                }
                //network failure
                2 ->{

                    homeViewBinding.errorMessage.text = getString(R.string.no_internet)
                    homeViewBinding.retryBtn.text = getString(R.string.check_btn)

                    homeViewBinding.retryBtn.setOnClickListener {
                        Log.i("TAG", "calledFromRefresh: ")
                        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        requireActivity().startActivity(intent)
                    }

                }
            }






        }, 500)
    }

    private fun animateFailureView() {


            homeViewBinding.cardView.translationY = homeViewBinding.cardView.height.toFloat()
            homeViewBinding.cardView.animate()
                .translationY(0f)
                .setDuration(500)
                .start()



            homeViewBinding.errorCardView.translationY =
                homeViewBinding.errorCardView.height.toFloat()
            homeViewBinding.errorCardView.animate()
                .translationY(0f)
                .setDuration(500)
                .start()

    }

    private fun showData(result: ApiState.Success, isCached: Boolean) {

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
            homeViewBinding.weatherType.text = result.weatherResponse.current.weather.first().description
            homeViewBinding.date.text = getDate(result.weatherResponse.current.dt)
            homeViewBinding.day.text = getDateForHourly(result.weatherResponse.current.dt)
            homeViewBinding.humidityText.text = "${result.weatherResponse.current.humidity} %"
            homeViewBinding.pressureText.text = "${result.weatherResponse.current.pressure} hpa"
            homeViewBinding.windText.text =
                "${result.weatherResponse.current.wind_speed} ${Constants.CURRENT_WIND_UNIT}"
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
            homeViewBinding.weatherType.translationX =
                homeViewBinding.weatherType.width.toFloat()
            homeViewBinding.weatherType.animate()
                .translationX(0f)
                .setDuration(500)
                .start()


        val layoutAnimationController =
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.slide_left_layout)
        homeViewBinding.hourRv.apply {

            layoutAnimation = layoutAnimationController
            scheduleLayoutAnimation()
        }

            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.fall_down_layout)
        homeViewBinding.daysRv.apply {

            layoutAnimation = layoutAnimationController
            scheduleLayoutAnimation()
        }
            AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.fall_down_layout)
        homeViewBinding.cardView.apply {

            layoutAnimation = layoutAnimationController
            scheduleLayoutAnimation()
        }




            homeViewBinding.moreDetails.translationX =
                homeViewBinding.moreDetails.width.toFloat()
            homeViewBinding.moreDetails.animate()
                .translationX(0f)
                .setDuration(500)
                .start()


            homeViewBinding.hourlyTitle.translationX =
                homeViewBinding.hourlyTitle.width.toFloat()
            homeViewBinding.hourlyTitle.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.fall_down_layout)
        homeViewBinding.cardView2.apply {

            layoutAnimation = layoutAnimationController
            scheduleLayoutAnimation()
        }




            homeViewBinding.dailyTitle.translationX = homeViewBinding.dailyTitle.width.toFloat()
            homeViewBinding.dailyTitle.animate()
                .translationX(0f)
                .setDuration(500)
                .start()


            homeViewBinding.weatherDegree.translationX =
                homeViewBinding.weatherDegree.width.toFloat()
            homeViewBinding.weatherDegree.animate()
                .translationX(0f)
                .setDuration(500)
                .start()



            homeViewBinding.weatherImage.translationX =
                -homeViewBinding.weatherImage.width.toFloat()
            homeViewBinding.weatherImage.animate()
                .translationX(0f)
                .setDuration(500)
                .start()


    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            var ActualLon = mLastLocation.longitude.toString()
            var ActualLat = mLastLocation.latitude.toString()
//            getStreetName(mLastLocation.latitude, mLastLocation.longitude)
            Log.i("TAG", "onLocationResult: not in if ")
            isLocationGot = true
            if (receivedLon.equals("default") && receivedLat.equals("default")) {
                lat = mLastLocation.longitude.toString()
                lon = mLastLocation.latitude.toString()

                Log.i("TAG", "onLocationResult: lat:$lat lon:$lon")

            } else {
                lon = receivedLon!!
                lat = receivedLat!!
            }


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
        homeViewBinding.root.postDelayed({

            loadData()
            Log.i("TAG", "onResume: loadingData")

        },500)


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

                requestNewLocationData()

            } else {

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