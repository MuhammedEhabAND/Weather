package inc.moe.weather.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import inc.moe.weather.Constants
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentSettingsBinding
import inc.moe.weather.model.SettingsData

class SettingsFragment : Fragment() {
    private lateinit var viewBindingSettings: FragmentSettingsBinding
    private val settingsData = SettingsData.settingsList
    private lateinit var settingsAdapter:SettingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewBindingSettings = FragmentSettingsBinding.inflate(inflater, container, false)
        animateViews()
        return viewBindingSettings.root
    }

    private fun animateViews() {
        viewBindingSettings.settingsRv.postDelayed({
            viewBindingSettings.settingsRv.translationY = viewBindingSettings.settingsRv.height.toFloat()
            viewBindingSettings.settingsRv.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
        viewBindingSettings.settingsTitle.postDelayed({
            viewBindingSettings.settingsTitle.translationX = viewBindingSettings.settingsTitle.width.toFloat()
            viewBindingSettings.settingsTitle.animate()
                .translationX(0f)
                .setDuration(500)
                .start()
        }, 100)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsAdapter =SettingsAdapter(requireContext())
        settingsAdapter.submitList(settingsData)
        viewBindingSettings.settingsRv.adapter = settingsAdapter





    }


}