package inc.moe.weather.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import inc.moe.weather.R
import inc.moe.weather.databinding.HourlyItemLayoutBinding
import inc.moe.weather.utils.getDateForHourly
import inc.moe.weather.utils.getImage
import inc.moe.weather.model.Hourly
import inc.moe.weather.model.Weather
import inc.moe.weather.utils.Constants

class HourlyAdapter : ListAdapter<Hourly, HourlyAdapter.ViewHolder>(
    HourlyDiffUtil()
) {
    var list : MutableList<Weather>? = null
    private lateinit var hourlyViewBindingAdapter: HourlyItemLayoutBinding
    class ViewHolder(var binding : HourlyItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyAdapter.ViewHolder {
        val inflater : LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        hourlyViewBindingAdapter = HourlyItemLayoutBinding.inflate(inflater , parent , false)
        return ViewHolder(hourlyViewBindingAdapter)

    }
    override fun onBindViewHolder(holder: HourlyAdapter.ViewHolder, position: Int) {
        val currentWeatherResponse = getItem(position)
        holder.binding.let {
            it.day.text = "${getDateForHourly(currentWeatherResponse.dt)}"
            it.weatherType.text = "${currentWeatherResponse.weather.first().description}"
            it.highestAndLowest.text = "${currentWeatherResponse.temp.toInt()}${Constants.CURRENT_WEATHER_UNIT} "

        }

        Glide.with(holder.binding.icon)
            .load(getImage(currentWeatherResponse.weather.first().icon ,4))
            .placeholder(R.drawable.place_holder) // You can set a placeholder image
            .error(R.drawable.error_weather) // You can set an error image
            .into(holder.binding.icon)
        holder.binding.icon
    }

    class HourlyDiffUtil
        : DiffUtil.ItemCallback<Hourly>(){
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem==newItem
        }

    }

}