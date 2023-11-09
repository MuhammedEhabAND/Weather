package inc.moe.weather.favourite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentFavBinding
import inc.moe.weather.favourite.viewmodel.FavViewModel
import inc.moe.weather.favourite.viewmodel.FavViewModelFactory
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavFragment : Fragment() {

    private lateinit var favBinding: FragmentFavBinding
    private lateinit var favViewModel: FavViewModel
    private lateinit var favAdapter: FavAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        favBinding = FragmentFavBinding.inflate(inflater, container, false)

        return favBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animate()
        favAdapter = FavAdapter()

        favBinding.favRv.adapter = favAdapter

        favViewModel = ViewModelProvider(
            requireActivity(),
            FavViewModelFactory(
                Repo.getInstance(
                    WeatherClient(),
                    WeatherLocalSource.getInstance(requireContext())
                )

            )
        ).get(FavViewModel::class.java)
        lifecycleScope.launch {

            favViewModel.weatherData.collectLatest {

                    result ->

                when (result) {

                    is DatabaseState.Loading -> {
                        favBinding.favRv.visibility = View.GONE

                        favBinding.swipeToRefresh.isRefreshing = true

                    }

                    is DatabaseState.Failure -> {

                        favBinding.swipeToRefresh.isRefreshing = false
                        showError(result)

                    }

                    is DatabaseState.Success -> {

                        favBinding.swipeToRefresh.isRefreshing = false
                        showData(result)
                    }

                }
            }
        }
        favBinding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.mapFragment)
        }
    }

    private fun showData(result: DatabaseState.Success) {
        favBinding.favRv.visibility = View.VISIBLE
        favAdapter.submitList(result.weatherResponse)

    }

    private fun showError(result: DatabaseState.Failure) {
        favBinding.favRv.visibility = View.GONE

        Snackbar.make(requireView(), result.message, Snackbar.LENGTH_SHORT).show()
    }

    private fun animate() {
        favBinding.favTitle.postDelayed({
            favBinding.favTitle.translationX = favBinding.favTitle.width.toFloat()
            favBinding.favTitle.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)


    }
}