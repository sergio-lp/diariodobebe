package com.diariodobebe.ui.entry_activities.health_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diariodobebe.EXTRA_ENTRY
import com.diariodobebe.R
import com.diariodobebe.adapters.SymptomAdapter
import com.diariodobebe.databinding.FragmentHealthConditionBinding
import com.diariodobebe.models.Health
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HealthConditionFragment : Fragment() {
    var symptoms = mutableListOf<String>()
    var finalDate: Long? = 0

    private var _binding: FragmentHealthConditionBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthConditionBinding.inflate(layoutInflater)
        val root = binding.root

        symptoms.add(getString(R.string.no_symptom_added))

        binding.edHealthDate.setOnClickListener {
            binding.edHealthDate.error = null
            binding.edHealthDate.requestFocus()
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

                if (!binding.edHealthTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edHealthTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edHealthDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edHealthTime.setOnClickListener {
            binding.edHealthTime.error = null
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

                if (!binding.edHealthDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edHealthDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edHealthTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        binding.rvSymptoms.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = SymptomAdapter(symptoms)
            setHasFixedSize(false)
        }

        binding.btnAddSymptom.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_symptom, null)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.add_symptom))
                .setView(dialogView)
                .setCancelable(true)
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(R.string.add) { _, _ ->
                    val ed = dialogView.findViewById<EditText>(R.id.dialog_symptom_ed)
                    if (!ed.text.isNullOrEmpty()) {
                        if (symptoms.contains(getString(R.string.no_symptom_added))) {
                            symptoms.remove(getString(R.string.no_symptom_added))
                        }
                        symptoms.add(ed.text.toString())
                        binding.rvSymptoms.adapter?.notifyItemChanged(symptoms.indexOf(ed.text.toString()))
                    }
                }.create().show()
        }


        arguments?.getParcelable<Health>(EXTRA_ENTRY)?.let { health ->
            finalDate = health.date
            binding.edHealthCondition.setText(health.healthEvent)
            binding.edHealthComment.setText(health.comment)

            health.symptoms?.forEach { symptom ->
                symptoms.add(symptom)
                binding.rvSymptoms.adapter?.notifyItemChanged(symptoms.lastIndex)
            }

            if (symptoms.size > 1) {
                symptoms.removeAt(0)
                binding.rvSymptoms.adapter?.notifyItemRemoved(0)
            }

            val dfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
            val dfHour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            dfDate.timeZone = TimeZone.getTimeZone("UTC")
            dfHour.timeZone = TimeZone.getTimeZone("UTC")

            binding.edHealthDate.setText(dfDate.format(health.date))
            binding.edHealthTime.setText(dfHour.format(health.date))
        }

        return root
    }

}