package com.diariodobebe.ui.entry_activities.health_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diariodobebe.EXTRA_ENTRY
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentMedicationBinding
import com.diariodobebe.models.Health
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MedicationFragment : Fragment() {
    private var _binding: FragmentMedicationBinding? = null
    val binding get() = _binding!!

    var finalDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationBinding.inflate(layoutInflater)

        binding.edMedicationDate.setOnClickListener {
            binding.edMedicationDate.error = null
            binding.edMedicationDate.requestFocus()
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

                if (!binding.edMedicationTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edMedicationTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edMedicationDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edMedicationTime.setOnClickListener {
            binding.edMedicationTime.error = null
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

                if (!binding.edMedicationDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edMedicationDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edMedicationTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        arguments?.getParcelable<Health>(EXTRA_ENTRY)?.let { health ->
            val dfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
            val dfHour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            if (health.medAmount != null && health.medAmount != 0) {
                binding.edMedAmount.setText(
                    health.medAmount.toString()
                )
            }

            if (health.medTime != null && health.medTime != 0.toLong()) {
                finalDate = health.medTime
                binding.edMedicationDate.setText(dfDate.format(health.medTime))
                binding.edMedicationTime.setText(dfHour.format(health.medTime))
            }


            binding.edMedName.setText(health.medication)
        }

        return binding.root
    }
}