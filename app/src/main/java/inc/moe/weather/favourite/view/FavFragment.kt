package inc.moe.weather.favourite.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentFavBinding
import inc.moe.weather.model.DummyFav

class FavFragment : Fragment() {

    private lateinit var favBinding: FragmentFavBinding
    private var favLists = listOf<DummyFav>(
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
        DummyFav("Alexandria" ,"10d" , 27.154 ,"Clear"),
    )
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
        favAdapter =FavAdapter()
        favAdapter.submitList(favLists)
        favBinding.favRv.adapter =favAdapter
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