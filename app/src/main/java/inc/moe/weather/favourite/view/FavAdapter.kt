package inc.moe.weather.favourite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import com.bumptech.glide.Glide
import inc.moe.weather.R
import inc.moe.weather.databinding.FavItemLayoutBinding
import inc.moe.weather.getImage
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.model.DummyFav
import inc.moe.weather.model.Weather


class FavAdapter : ListAdapter<DatabaseWeather, FavAdapter.ViewHolder>(
    FavDiffUtil()
) {

    private lateinit var favAdapterViewBinding: FavItemLayoutBinding
    private val animatedPositions = HashSet<Int>()

    class ViewHolder(var binding: FavItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavAdapter.ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        favAdapterViewBinding = FavItemLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(favAdapterViewBinding)

    }

    override fun onBindViewHolder(holder: FavAdapter.ViewHolder, position: Int) {
        val dummyFav = getItem(position)

        showData(holder , dummyFav)
        if (!animatedPositions.contains(position)) {
            startAnimation(holder, position)
            animatedPositions.add(position)
        }

    }

    private fun showData(holder: ViewHolder, dummyFav: DatabaseWeather) {
        holder.binding.let {
            it.titleTv.text = dummyFav.timeZone
            it.weatherType.text = dummyFav.weatherType
            it.contentTv.text="${dummyFav.temp.toInt()}℃"
            Glide.with(it.logoIv)
                .load(getImage(dummyFav.image, 4))
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error_weather)
                .into(it.logoIv)
        }
    }


    private fun startAnimation(holder: ViewHolder, position:Int) {

        if (position % 2 == 0) {
            animateToLeft(holder)
        }else {
            animateToRight(holder)
        }

    }

    private fun animateToRight(holder: ViewHolder) {

        holder.binding.card.postDelayed({
            holder.binding.card.translationX = -holder.binding.card.width.toFloat()
            holder.binding.card.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)
       }

    private fun animateToLeft(holder: ViewHolder) {

        holder.binding.card.postDelayed({
            holder.binding.card.translationX = holder.binding.card.width.toFloat()
            holder.binding.card.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)
    }



    class FavDiffUtil
        : DiffUtil.ItemCallback<DatabaseWeather>() {
        override fun areItemsTheSame(oldItem: DatabaseWeather, newItem: DatabaseWeather): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DatabaseWeather, newItem: DatabaseWeather): Boolean {
            return oldItem == newItem
        }

    }

}