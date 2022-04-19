package com.diariodobebe.ui.entry_activities.feeding_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.diariodobebe.EXTRA_ENTRY
import com.diariodobebe.R
import com.diariodobebe.databinding.BreastFragmentBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Feeding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class BreastFragment : Fragment() {
    var finalDate: Long? = null
    var entryId: Int? = null
    private var _binding: BreastFragmentBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BreastFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.edFeedingDate.setOnClickListener {
            binding.edFeedingDate.requestFocus()
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

                if (!binding.edFeedingStart.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat("HH:mm")
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edFeedingStart.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edFeedingDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edFeedingStart.setOnClickListener {
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

                if (!binding.edFeedingDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edFeedingDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edFeedingStart.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        binding.btnAddFeeding.setOnClickListener {

            if (checkEditTexts(
                    binding.edFeedingStart,
                    binding.edFeedingDate,
                    binding.timeBreastRight,
                    binding.timeBreastLeft
                )
            ) {

                val feeding = Feeding(
                    entryId,
                    finalDate,
                    Entry.EntryType.ENTRY_FEEDING,
                    binding.edBreastComment.text.toString(),
                    Feeding.FeedingType.FEEDING_BREAST,
                    if (binding.timeBreastRight.text.isEmpty()) 0 else binding.timeBreastRight.text.toString()
                        .toIntOrNull(),
                    if (binding.timeBreastLeft.text.isEmpty()) 0 else binding.timeBreastLeft.text.toString()
                        .toIntOrNull(),

                    )

                GetBaby.insertEntry(feeding, requireActivity())
            }

        }

        arguments?.getParcelable<Feeding>(EXTRA_ENTRY)?.let {
            binding.timeBreastLeft.setText(it.leftBreastTime.toString())
            binding.timeBreastRight.setText(it.rightBreastTime.toString())

            binding.edBreastComment.setText(it.comment)

            entryId = it.id

            val dfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
            val dfHour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            dfDate.timeZone = TimeZone.getTimeZone("UTC")
            dfHour.timeZone = TimeZone.getTimeZone("UTC")

            binding.edFeedingDate.setText(dfDate.format(it.date))
            binding.edFeedingStart.setText(dfHour.format(it.date))

            finalDate = it.date ?: 0

            binding.btnAddFeeding.text = getString(R.string.edit_entry)
        }

        return root
    }


    private fun checkEditTexts(
        edStartTime: EditText,
        edDate: EditText,
        edRightTime: EditText,
        edLeftTime: EditText
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edStartTime.text.isNullOrBlank()) {
            edStartTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (edRightTime.text.isNullOrBlank() && edLeftTime.text.isNullOrBlank()) {
            edRightTime.error = getString(R.string.please_fill_one)
            edLeftTime.error = getString(R.string.please_fill_one)
            return false
        }

        return true
    }

}