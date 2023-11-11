package inc.moe.weather.favourite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.favourite.viewmodel.OnItemClickListener
import inc.moe.weather.R
import inc.moe.weather.favourite.viewmodel.SwipeToDeleteListener
import inc.moe.weather.databinding.FragmentFavBinding
import inc.moe.weather.favourite.viewmodel.FavViewModel
import inc.moe.weather.favourite.viewmodel.FavViewModelFactory
import inc.moe.weather.home.view.HomeFragment
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.repo.Repo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavFragment : Fragment(), SwipeToDeleteListener, OnItemClickListener {

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
        setupRecycler()
        favBinding.swipeToRefresh.setOnRefreshListener {
            favBinding.swipeToRefresh.isRefreshing = false
        }

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

                    else -> {}
                }
            }
        }
        favBinding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.mapFragment)
        }
    }

    private fun setupRecycler() {
        favAdapter = FavAdapter(requireContext(), this, this)
        favBinding.favRv.adapter = favAdapter
        setupSwipeToDelete()
    }

    private fun showData(result: DatabaseState.Success) {
        favBinding.root.postDelayed({
            favBinding.favRv.visibility = View.VISIBLE
        }, 100)


        favAdapter.submitList(result.weatherResponse)
        if (result.weatherResponse.isEmpty()) {


            favBinding.lottieAnim.postDelayed({
                favBinding.lottieAnim.animate()
                favBinding.lottieAnim.visibility = View.VISIBLE
            }, 90)
            favBinding.lottieAnim.postDelayed({


                favBinding.lottieAnim.translationY = favBinding.lottieAnim.height.toFloat()
                favBinding.lottieAnim.animate()

                    .translationY(0f)
                    .setDuration(500)
                    .start()

            }, 100)
        } else {
            favBinding.lottieAnim.visibility = View.GONE
            favBinding.lottieAnim.isActivated = false

        }
    }

    private fun showError(result: DatabaseState.Failure) {
        favBinding.root.postDelayed({
            favBinding.favRv.visibility = View.GONE
        }, 500)

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
        favBinding.addBtn.postDelayed({
            favBinding.addBtn.translationX = favBinding.addBtn.width.toFloat()
            favBinding.addBtn.translationY = favBinding.addBtn.height.toFloat()
            favBinding.addBtn.animate()
                .translationX(0f)
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)


    }

    private fun setupSwipeToDelete() {
        val swipeToDeleteCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                // Not used for swipe-to-delete
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Notify the fragment about the swipe
                onSwipeToDelete(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(favBinding.favRv)
    }

    override fun onSwipeToDelete(position: Int) {
        val item = favAdapter.currentList[position]
        favViewModel.deleteFavWeather(item)
        showUndoSnackbar(item, position)
    }

    private fun showUndoSnackbar(deletedItem: DatabaseWeather, position: Int) {
        val snackbar = Snackbar.make(
            requireView(),
            "Item deleted",
            Snackbar.LENGTH_LONG
        )

        snackbar.setAction("Undo") {

            undoDelete(deletedItem, position)
        }

        snackbar.show()
    }

    private fun undoDelete(deletedItem: DatabaseWeather, position: Int) {
        favViewModel.addFavWeather(deletedItem)
    }

    override fun onItemClickListener(weather: DatabaseWeather) {
        val bundle = Bundle()
        bundle.putString("lat", weather.lat)
        bundle.putString("lon", weather.lon)
        val fragment = HomeFragment()
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_favFragment_to_homeFragment, bundle)

    }
}