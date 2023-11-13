package inc.moe.weather.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentSettingsBinding
import inc.moe.weather.utils.SettingsData

class SettingsFragment : Fragment()  ,OnNotificationClickListener {
    private lateinit var viewBindingSettings: FragmentSettingsBinding

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

        settingsAdapter =SettingsAdapter(requireActivity() , this)

        settingsAdapter.submitList(SettingsData.settingsList)


        viewBindingSettings.settingsRv.adapter = settingsAdapter





    }



    override fun onCLick() {
        findNavController().navigate(R.id.action_settingsFragment_to_notificationFragment)
    }


}