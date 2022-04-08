package com.diariodobebe.ui.entry_activities.feeding_activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentBottleBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Feeding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.gson.Gson
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.util.*

class BottleFragment : Fragment() {
    private val calFeedingStart = Calendar.getInstance()
    private var hourFeedingStart: Int? = null
    private var minuteFeedingStart: Int? = null

    private var _binding: FragmentBottleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottleBinding.inflate(layoutInflater)

        binding.edFeedingDate.setOnClickListener {
            val calendarConstraints = CalendarConstraints.Builder().setValidator(
                DateValidatorPointBackward.now()
            ).build()
            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.feeding_date))
                .setCalendarConstraints(calendarConstraints)
                .build()

            datePickerDialog.show(requireActivity().supportFragmentManager, "DATEPICKER")

            datePickerDialog.addOnPositiveButtonClickListener { date ->
                calFeedingStart.timeInMillis = date

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edFeedingDate.setText(
                    df.format(calFeedingStart.time)
                )
            }
        }

        binding.edFeedingStart.setOnClickListener {
            val timePickerDialog = MaterialTimePicker.Builder()
                .setTitleText(getString(R.string.time_feeding_start))
                .build()


            timePickerDialog.show(requireActivity().supportFragmentManager, "TIMEPICKER")

            timePickerDialog.addOnPositiveButtonClickListener {
                hourFeedingStart = timePickerDialog.hour
                minuteFeedingStart = timePickerDialog.minute

                calFeedingStart.set(Calendar.HOUR_OF_DAY, hourFeedingStart!!)
                calFeedingStart.set(Calendar.MINUTE, minuteFeedingStart!!)

                val strHour: String =
                    if (hourFeedingStart!! > 9) hourFeedingStart.toString() else "0$hourFeedingStart"
                val strMinute: String =
                    if (minuteFeedingStart!! > 9) minuteFeedingStart.toString() else "0$minuteFeedingStart"

                binding.edFeedingStart.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )
            }
        }

        binding.btnAddFeeding.setOnClickListener {
            val file = File(
                requireActivity().filesDir,
                requireActivity().getSharedPreferences(
                    getString(R.string.PREFS),
                    Context.MODE_PRIVATE
                ).getString("baby", "") + ".json"
            )

            val baby = GetBaby.getBaby(file)

            if (checkEditTexts(
                    binding.edFeedingStart,
                    binding.edFeedingDate,
                    binding.edBottleMilliliters
                )
            ) {
                var id = baby.lastEntryId
                if (id == null) {
                    id = 0
                } else {
                    id += 1
                }

                val feeding = Feeding(
                    id,
                    null,
                    Entry.EntryType.ENTRY_FEEDING,
                    null,
                    Feeding.FeedingType.FEEDING_BOTTLE,
                    milliliters = binding.edBottleMilliliters.text.toString().toInt()
                )

                feeding.date = calFeedingStart.timeInMillis

                baby.entryList!!.add(feeding)
                baby.lastEntryId = feeding.id
                file.delete()
                val gson = Gson()
                val json = gson.toJson(baby)

                file.writeText(json, StandardCharsets.UTF_8)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.activity_insert_successful),
                    Toast.LENGTH_SHORT
                )
                    .show()
                requireActivity().finish()
            }
        }

        return binding.root
    }

    private fun checkEditTexts(
        edStartTime: EditText,
        edDate: EditText,
        edMilliliters: EditText
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edStartTime.text.isNullOrBlank()) {
            edStartTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (edMilliliters.text.isNullOrBlank()) {
            edMilliliters.error = getString(R.string.please_inform_milliliters)
            return false
        }

        return true
    }
}