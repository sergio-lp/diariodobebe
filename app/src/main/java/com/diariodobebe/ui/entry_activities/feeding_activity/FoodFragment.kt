package com.diariodobebe.ui.entry_activities.feeding_activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentFoodBinding
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

class FoodFragment : Fragment() {
    private val calFeedingStart: Calendar = Calendar.getInstance()
    private var hourFeedingStart: Int? = null
    private var minuteFeedingStart: Int? = null

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodBinding.inflate(layoutInflater)
        val root = binding.root

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
            val check = checkEditTexts(
                binding.edFeedingStart,
                binding.edFeedingDate,
                binding.edFoodType,
            )

            val file = File(
                requireActivity().filesDir,
                requireActivity().getSharedPreferences(
                    getString(R.string.PREFS),
                    Context.MODE_PRIVATE
                ).getString("baby", "") + ".json"
            )
            val baby = GetBaby.getBaby(file)
            if (check) {
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
                    Feeding.FeedingType.FEEDING_FOOD,
                    foodType = binding.edFoodType.text.toString()
                )
                if (!binding.edFoodComment.text.isNullOrBlank()) {
                    feeding.comment = binding.edFoodComment.text.toString()
                } else {
                    feeding.comment = null
                }

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

        return root
    }

    private fun checkEditTexts(
        edStartTime: EditText,
        edDate: EditText,
        edFoodType: EditText,
    ): Boolean {
        var ret = false
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edStartTime.text.isNullOrBlank()) {
            edStartTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (edFoodType.text.isNullOrBlank()) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.no_food_type_alert))
                .setMessage(getString(R.string.no_food_type_message))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    ret = true
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    ret = false
                }
                .setCancelable(true)
                .show()
        }

        return ret
    }


}