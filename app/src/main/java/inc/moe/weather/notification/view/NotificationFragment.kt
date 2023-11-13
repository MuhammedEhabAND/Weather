package inc.moe.weather.notification.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import inc.moe.notesapp.database.WeatherLocalSource
import inc.moe.weather.R
import inc.moe.weather.databinding.FragmentNotificationBinding
import inc.moe.weather.favourite.view.FavAdapter
import inc.moe.weather.favourite.viewmodel.FavViewModel
import inc.moe.weather.favourite.viewmodel.FavViewModelFactory
import inc.moe.weather.favourite.viewmodel.OnItemClickListener
import inc.moe.weather.model.DatabaseWeather
import inc.moe.weather.network.DatabaseState
import inc.moe.weather.network.WeatherClient
import inc.moe.weather.notification.model.Alarm
import inc.moe.weather.notification.model.AlarmItem
import inc.moe.weather.notification.model.AlarmScheduler
import inc.moe.weather.repo.Repo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class NotificationFragment : Fragment() ,OnItemClickListener{

    private lateinit var scheduler: AlarmScheduler
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var favViewModel:FavViewModel
    private lateinit var favAdapter: FavAdapter
    private var isStartSelected : Boolean  =false
    private var resultDateTime: LocalDateTime? = null
//    private lateinit var datePicker: MaterialDatePicker

    private lateinit var calendar: Calendar
    private lateinit var time :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        animateViews()
        return binding.root
    }

    private fun animateViews() {

        binding.selectTitle.postDelayed({
            binding.selectTitle.translationY = binding.selectTitle.height.toFloat()
            binding.selectTitle.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
        binding.setAlarmBtn.postDelayed({
            binding.setAlarmBtn.translationY =  -binding.setAlarmBtn.height.toFloat()
            binding.setAlarmBtn.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)


        binding.dataRv.postDelayed({
            binding.dataRv.translationY = binding.dataRv.height.toFloat()
            binding.dataRv.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)
        binding.alertAlarmRadioGroup.postDelayed({
            binding.alertAlarmRadioGroup.translationY = -binding.alertAlarmRadioGroup.height.toFloat()
            binding.alertAlarmRadioGroup.animate()
                .translationY(0f)
                .setDuration(500)
                .start()
        }, 100)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favAdapter = FavAdapter(requireContext() , onItemClickListener = this , isNotification = true)
        binding.dataRv.adapter = favAdapter
        favViewModel = ViewModelProvider(
            requireActivity(),
            FavViewModelFactory(
                Repo.getInstance(
                    WeatherClient(),
                    WeatherLocalSource.getInstance(requireContext())
                )

            )
        ).get(FavViewModel::class.java)

        scheduler = Alarm(requireContext())
        lifecycleScope.launch {
            favViewModel.weatherData.collectLatest {
                when(it){
                    is DatabaseState.Failure -> {
                        showFailure(it)
                    }

                    DatabaseState.Loading -> {
                        showLoading()
                    }
                    is DatabaseState.Success -> {
                        showSuccess(it)
                    }

                }
            }
        }
        binding.setAlarmBtn.setOnClickListener {
            showDatePicker()
        }


    }

    private fun showDatePicker() {
        // Create and show the MaterialDatePicker for selecting a date
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.choose_the_date))
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Convert selected date to LocalDateTime
            val selectedDate = Instant.ofEpochMilli(selection as Long)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            // Show the MaterialTimePicker for selecting a time
            showTimePicker(selectedDate)
        }

        datePicker.show(requireFragmentManager(), "datePicker")
    }

    private fun showTimePicker(selectedDateTime: LocalDateTime) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(selectedDateTime.hour)
            .setMinute(selectedDateTime.minute)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            // Get the selected time from MaterialTimePicker
            val hour = timePicker.hour
            val minute = timePicker.minute

            // Combine date and time to create LocalDateTime
            resultDateTime = selectedDateTime.withHour(hour).withMinute(minute)

            time = "${resultDateTime?.dayOfMonth}/${resultDateTime?.month}${resultDateTime?.year}  ${resultDateTime?.hour}:${resultDateTime?.minute}:${resultDateTime?.second}"
            Snackbar.make(requireView() ,time,Snackbar.LENGTH_SHORT).show()
            isStartSelected= true

            // Now you have the resultDateTime as LocalDateTime
            // Do something with the selected date and time
            // For example, you can display it or use it in your app logic
        }

        timePicker.show(requireFragmentManager(), "timePicker")
    }


    private fun showSuccess(databaseState: DatabaseState.Success) {
        disableLayout()
        binding.loadingProgressBar.visibility = View.GONE
        Log.i("TAG", "Notification showSuccess: ${databaseState.weatherResponse.size} ")
         binding.root.postDelayed({

            binding.dataRv.visibility = View.VISIBLE
             favAdapter.submitList(databaseState.weatherResponse)

         }, 100)

        if (databaseState.weatherResponse.isEmpty()) {


            binding.lottieAnim.postDelayed({
                binding.lottieAnim.animate()
                binding.lottieAnim.visibility = View.VISIBLE
            }, 90)
            binding.lottieAnim.postDelayed({


                binding.lottieAnim.translationY = binding.lottieAnim.height.toFloat()
                binding.lottieAnim.animate()

                    .translationY(0f)
                    .setDuration(500)
                    .start()

            }, 100)
        } else {

            binding.lottieAnim.visibility = View.GONE
            binding.lottieAnim.isActivated = false

        }
    }

    private fun disableLayout() {
        binding.favLayout.isEnabled = false

    }


    private fun showFailure(databaseState: DatabaseState.Failure) {
        Log.i("TAG", "Notification showFailure: ")
        Snackbar.make(binding.root , databaseState.message ,Snackbar.LENGTH_SHORT).show()
        binding.dataRv.visibility =View.GONE
        binding.lottieAnim.visibility =View.GONE
        binding.loadingProgressBar.visibility =View.GONE

    }

    private fun showLoading() {
        Log.i("TAG", " Notification showLoading: ")
        binding.dataRv.visibility =View.GONE
        binding.lottieAnim.visibility =View.GONE
        binding.loadingProgressBar.visibility =View.VISIBLE

    }

    override fun onItemClickListener(weather: DatabaseWeather) {
        if(weather.isScheduled){
            if(isStartSelected){
                favViewModel.setScheduler(weather , time)
                scheduler.schedule(createAlarmItem(weather))

            }else{
                favViewModel.disableScheduler(weather)
//                scheduler.cancel(createAlarmItem(weather))
            }
        }else{
            if(isStartSelected){
                favViewModel.setScheduler(weather,time)
                scheduler.schedule(createAlarmItem(weather))
            }else{
                showSnackBar()
            }
        }



    }
    private fun createAlarmItem(weather: DatabaseWeather):AlarmItem =
        AlarmItem( resultDateTime!!, weather.lat.toString() , weather.lon.toString())
    private fun showSnackBar(){
        Snackbar.make(binding.root , getString(R.string.please_select_date_first) ,Snackbar.LENGTH_SHORT).show()
    }
}