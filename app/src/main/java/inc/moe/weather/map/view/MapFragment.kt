package inc.moe.weather.map.view

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentMapBinding
import inc.moe.weather.map.viewmodel.MapViewModel
import inc.moe.weather.map.viewmodel.MapViewModelFactory
import inc.moe.weather.network.ApiState
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    lateinit var mMap: MapView
    private lateinit var mapController: IMapController
    private lateinit var mMyLocationOverlay: MyLocationNewOverlay
    private lateinit var mapViewModel: MapViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapBinding.inflate(
            layoutInflater,
            container,
            false
        )        // Inflate the layout for this fragment
        Configuration.getInstance()
            .load(requireContext(), requireActivity().getPreferences(MODE_PRIVATE))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)
        mapController = binding.map.controller
        mapViewModel = ViewModelProvider(
            requireActivity(),
            MapViewModelFactory(

                Repo.getInstance(
                    WeatherClient(),
                    WeatherLocalSource.getInstance(requireContext())

                )
            )
        ).get(MapViewModel::class.java)
        mapController.setZoom(10.0)
        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), binding.map)
        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.setPersonIcon(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.baseline_my_location_24
            )?.toBitmap(100, 100)
        )
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            requireActivity().runOnUiThread {
                mapController.setCenter(mMyLocationOverlay.myLocation)
                mapController.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        val compassOverlay =
            CompassOverlay(
                requireContext(),
                InternalCompassOrientationProvider(requireContext()),
                binding.map
            )
        compassOverlay.enableCompass()
        val rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled

        binding.map.overlays.add(compassOverlay)
        binding.map.overlays.add(mMyLocationOverlay)
        binding.map.overlays.add(rotationGestureOverlay)
        binding.map.invalidate()

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                val latitude = String.format("%.2f", p.latitude)
                val longitude = String.format("%.2f", p.longitude)

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Add location to favorite")
                    .setMessage("Add location on $latitude, $longitude to favorite?")
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ok") { dialog, which ->
                        mapViewModel.getWeather(lat = latitude , lon =latitude )
                        dialog.dismiss()
                        lifecycleScope.launch {
                            mapViewModel.weatherResponse.collectLatest {
                                result->when(result){
                                    is ApiState.Success -> {
                                        findNavController().navigateUp()
                                    }
                                    is ApiState.Failure -> {

                                        Toast.makeText(requireContext() , "Succeed" ,Toast.LENGTH_SHORT).show()

                                        Snackbar.make(requireView() , result.message , Snackbar.LENGTH_SHORT).show()}
                                    is ApiState.Loading -> {}
                                }
                            }
                        }
                    }
                    .show()
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        val overlay = MapEventsOverlay(mapEventsReceiver)
        binding.map.overlays.add(overlay)

    }
}