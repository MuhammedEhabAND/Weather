package inc.moe.weather.settings.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yariksoffice.lingver.Lingver
import inc.moe.weather.MainActivity
import inc.moe.weather.R
import inc.moe.weather.databinding.ExpandableItemLayoutBinding
import inc.moe.weather.model.Current
import inc.moe.weather.model.Settings
import inc.moe.weather.utils.Constants
import inc.moe.weather.utils.LanguageHelper
import inc.moe.weather.utils.SettingsData
import java.util.Locale

class SettingsAdapter(val activity: Activity , val onNotificationClickListener: OnNotificationClickListener) : ListAdapter<Settings, SettingsAdapter.ViewHolder>(
    SettingsDiffUtil()
) {


    private lateinit var settingsAdapterViewBinding: ExpandableItemLayoutBinding

    class ViewHolder(var binding: ExpandableItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        settingsAdapterViewBinding = ExpandableItemLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(settingsAdapterViewBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentSettings = getItem(position)
        var isExpanded = currentSettings.isExpanded
        showAndHideData(holder, isExpanded)
        holder.binding.let {
            it.titleTv.text = activity.applicationContext.getString(currentSettings.title)
            it.option1Text.text = activity.applicationContext.getString(currentSettings.option1)
            it.option2Text.text = activity.applicationContext.getString(currentSettings.option2)
            if (currentSettings.isOptionOne) {
                it.option1.isChecked = true
            } else {
                it.option2.isChecked = true

            }
            val sharedPreferences = activity.applicationContext.getSharedPreferences("Wind", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            when (currentSettings.title) {
                R.string.language -> {
                    it.radioGroupItems.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {
                            R.id.option1 -> {
                                editor.putString("current language" ,"en")
                                editor.apply()
                                currentSettings.isOptionOne = true
                                Constants.CURRENT_LANGUAGE ="en"
                                Lingver.getInstance().setLocale(activity.applicationContext , "en")
                                activity.recreate()

                            }

                            R.id.option2 -> {
                                editor.putString("current language" ,"ar")
                                editor.apply()
                                currentSettings.isOptionOne = false
                                Constants.CURRENT_LANGUAGE ="ar"
                                Lingver.getInstance().setLocale(activity.applicationContext , "ar")
                                activity.recreate()

                            }

                        }
                        SettingsData.saveSettings(activity.applicationContext)


                    }

                }

                R.string.wind_speed_unit -> {

                    it.radioGroupItems.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {

                            R.id.option1 -> {

                                currentSettings.isOptionOne = true
                                SettingsData.saveSettings(activity.applicationContext)
                                editor.putString("current unit" , Constants.UNITS_METER)
                                editor.putString("current weather" , Constants.CELISUIS)
                                editor.putString("current wind" , Constants.METER_PER_SECONDS)
                                editor.apply()
                            }

                            R.id.option2 -> {
                                currentSettings.isOptionOne = false
                                SettingsData.saveSettings(activity.applicationContext)
                                editor.putString("current unit" , Constants.UNITS_IMPERIAL)
                                editor.putString("current weather" , Constants.FAHERNHEIT)
                                editor.putString("current wind" , Constants.MILE_PER_HOUR)
                                editor.apply()


                            }

                        }
                        val message = activity.getString(R.string.wind_speed_changing)
                        Toast.makeText(activity.applicationContext , message,Toast.LENGTH_SHORT).show()



                    }


                }

                R.string.temp -> {
                    it.radioGroupItems.setOnCheckedChangeListener { group, checkedId ->
                        when (checkedId) {
                            R.id.option1 -> {
                                currentSettings.isOptionOne = true
                                SettingsData.saveSettings(activity.applicationContext)
                                editor.putString("current unit" , Constants.UNITS_METER)
                                editor.putString("current weather" , Constants.CELISUIS)
                                editor.putString("current wind" , Constants.METER_PER_SECONDS)
                                editor.apply()

                            }

                            R.id.option2 -> {
                                currentSettings.isOptionOne = false
                                SettingsData.saveSettings(activity.applicationContext)
                                editor.putString("current unit" , Constants.UNITS_IMPERIAL)
                                editor.putString("current weather" , Constants.FAHERNHEIT)
                                editor.putString("current wind" , Constants.MILE_PER_HOUR)
                                editor.apply()

                            }

                        }

                        val message = activity.getString(R.string.temp_changing)
                        Toast.makeText(activity.applicationContext , message,Toast.LENGTH_SHORT).show()


                    }


                }

            }


            it.logoIv.setImageResource(currentSettings.logo)
            if (currentSettings.title != R.string.notification) {

                it.card.setOnClickListener {

                    isExpanded = !isExpanded

                    showAndHideData(holder, isExpanded)

                }
            } else {
                it.expandImage.visibility = View.GONE
                it.card.setOnClickListener {
                    onNotificationClickListener.onCLick()
                }
            }

        }
    }


    private fun showAndHideData(holder: ViewHolder, isExpanded: Boolean) {
        if (isExpanded) {
            expand(holder)
        } else {
            hide(holder)
        }
    }

    private fun expand(holder: ViewHolder) {
        holder.binding.let {
            it.radioGroupItems.visibility = View.VISIBLE
            it.expandableView.visibility = View.VISIBLE
            it.expandableView.translationY = it.expandableView.height.toFloat()
            it.expandableView.animate()
                .setDuration(300)
                .translationY(0f)
            it.expandImage.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        }
    }

    private fun hide(holder: ViewHolder) {
        holder.binding.let {
            it.radioGroupItems.visibility = View.GONE
            it.expandableView.visibility = View.GONE

            it.expandImage.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
        }
    }


    class SettingsDiffUtil
        : DiffUtil.ItemCallback<Settings>() {
        override fun areItemsTheSame(oldItem: Settings, newItem: Settings): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Settings, newItem: Settings): Boolean {
            return oldItem == newItem
        }

    }


}