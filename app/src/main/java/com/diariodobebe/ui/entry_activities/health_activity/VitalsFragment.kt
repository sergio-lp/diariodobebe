package com.diariodobebe.ui.entry_activities.health_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentVitalsBinding
import com.diariodobebe.models.Health
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class VitalsFragment : Fragment() {
    var mood: Int? = null
    private var _binding: FragmentVitalsBinding? = null
    val binding get() = _binding!!

    var finalDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVitalsBinding.inflate(layoutInflater)
        val root = binding.root
        changeMood()

        binding.btnMoodGood.setOnClickListener {
            this.mood = Health.MOOD_GOOD
            changeMood()
        }

        binding.btnMoodNormal.setOnClickListener {
            this.mood = Health.MOOD_NORMAL
            changeMood()
        }

        binding.btnMoodBad.setOnClickListener {
            this.mood = Health.MOOD_BAD
            changeMood()
        }

        binding.edVitalDate.setOnClickListener {
            binding.edVitalDate.error = null
            binding.edVitalDate.requestFocus()
            val calendarConstraints = CalendarConstraints.Builder().setValidator(
                DateValidatorPointBackward.now()
            ).build()
            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_hint))
                .setCalendarConstraints(calendarConstraints)
                .build()

            datePickerDialog.show(requireActivity().supportFragmentManager, "DATEPICKER")

            datePickerDialog.addOnPositiveButtonClickListener { date ->
                var dateToSet: Long = date

                if (!binding.edVitalTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edVitalTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edVitalDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edVitalTime.setOnClickListener {
            binding.edVitalTime.error = null
            val timePickerDialog = MaterialTimePicker.Builder()
                .setTitleText(getString(R.string.pick_time_hint))
                .build()


            timePickerDialog.show(requireActivity().supportFragmentManager, "TIMEPICKER")

            timePickerDialog.addOnPositiveButtonClickListener {
                val hour = timePickerDialog.hour
                val minute = timePickerDialog.minute

                val hourInMillis = hour * 3600000
                val minuteInMillis = minute * 60000

                var dateToSet: Long = hourInMillis.toLong() + minuteInMillis.toLong()

                if (!binding.edVitalDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edVitalDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edVitalTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        return root
    }

    private fun changeMood() {
        when (this.mood) {
            Health.MOOD_GOOD -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.good_mood).lowercase())
            }
            Health.MOOD_NORMAL -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.normal_mood).lowercase())
            }
            Health.MOOD_BAD -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.bad_mood).lowercase())
            }
            else -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.not_selected).lowercase())
            }
        }
    }
}