package inc.moe.weather.settings.view



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import inc.moe.weather.R
import inc.moe.weather.databinding.DailyItemLayoutBinding
import inc.moe.weather.databinding.ExpandableItemLayoutBinding
import inc.moe.weather.getDateForDaily
import inc.moe.weather.getImage
import inc.moe.weather.model.Daily
import inc.moe.weather.model.Settings
import inc.moe.weather.model.Weather

class SettingsAdapter : ListAdapter<Settings, SettingsAdapter.ViewHolder>(
    SettingsDiffUtil()
) {
    var list : MutableList<Weather>? = null
    private lateinit var settingsAdapterViewBinding: ExpandableItemLayoutBinding
    class ViewHolder(var binding :ExpandableItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsAdapter.ViewHolder {
        val inflater :LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        settingsAdapterViewBinding = ExpandableItemLayoutBinding.inflate(inflater , parent , false)
        return ViewHolder(settingsAdapterViewBinding)

    }
    override fun onBindViewHolder(holder: SettingsAdapter.ViewHolder, position: Int) {

        val currentSettings = getItem(position)
        var isExpanded = currentSettings.isExpanded
        showAndHideData(holder , isExpanded)

        holder.binding.let {
            it.titleTv.text = currentSettings.title
            it.option1Text.text = currentSettings.option1
            it.option2Text.text = currentSettings.option2
            it.logoIv.setImageResource(currentSettings.logo)
            it.card.setOnClickListener{
                isExpanded = !isExpanded
                showAndHideData(holder , isExpanded)

            }
        }
    }

    private fun showAndHideData(holder: SettingsAdapter.ViewHolder , isExpanded:Boolean) {
        if(isExpanded){
            expand(holder)
        }else{
            hide(holder)
        }
    }

    private fun expand(holder: SettingsAdapter.ViewHolder){
        holder.binding.let {
            it.radioGroupItems.visibility = View.VISIBLE
            it.expandableView.visibility=View.VISIBLE
            it.expandableView.translationY = it.expandableView.height.toFloat()
            it.expandableView.animate()
                .setDuration(300)
                .translationY(0f)
            it.expandImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        }
    }
    private fun hide(holder: SettingsAdapter.ViewHolder){
        holder.binding.let {
            it.radioGroupItems.visibility = View.GONE
            it.expandableView.visibility=View.GONE

            it.expandImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
        }
    }


    class SettingsDiffUtil
        : DiffUtil.ItemCallback<Settings>(){
        override fun areItemsTheSame(oldItem: Settings, newItem: Settings): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: Settings, newItem: Settings): Boolean {
            return oldItem==newItem
        }

    }

}