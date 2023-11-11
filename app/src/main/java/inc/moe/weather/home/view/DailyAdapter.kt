package inc.moe.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import inc.moe.weather.R
import inc.moe.weather.databinding.DailyItemLayoutBinding
import inc.moe.weather.utils.getDateForDaily
import inc.moe.weather.utils.getImage
import inc.moe.weather.model.Daily
import inc.moe.weather.model.Weather
import inc.moe.weather.utils.Constants

class DailyAdapter : ListAdapter<Daily, DailyAdapter.ViewHolder>(
    DailyDiffUtil()
) {
    var list : MutableList<Weather>? = null
    private lateinit var forcastViewBindingAdapter: DailyItemLayoutBinding
    class ViewHolder(var binding :DailyItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyAdapter.ViewHolder {
        val inflater :LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        forcastViewBindingAdapter = DailyItemLayoutBinding.inflate(inflater , parent , false)
        return ViewHolder(forcastViewBindingAdapter)

    }
    override fun onBindViewHolder(holder: DailyAdapter.ViewHolder, position: Int) {
        val currentWeatherResponse = getItem(position)
        holder.binding.let {
            it.day.text = "${getDateForDaily(currentWeatherResponse.dt)}"
            it.weatherType.text = "${currentWeatherResponse.weather.first().main}"
            it.highestAndLowest.text = "${currentWeatherResponse.temp.max.toInt()}${Constants.CURRENT_WEATHER_UNIT} / ${currentWeatherResponse.temp.min.toInt()}${Constants.CURRENT_WEATHER_UNIT}"

        }

        Glide.with(holder.binding.icon)
            .load(getImage(currentWeatherResponse.weather.first().icon ,4))
            .placeholder(R.drawable.place_holder) // You can set a placeholder image
            .error(R.drawable.error_weather) // You can set an error image
            .into(holder.binding.icon)
        holder.binding.icon
    }

    class DailyDiffUtil
        : DiffUtil.ItemCallback<Daily>(){
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem==newItem
        }

    }

}