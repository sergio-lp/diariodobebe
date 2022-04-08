package com.diariodobebe.ui.entry_activities.feeding_activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.diariodobebe.R
import com.diariodobebe.databinding.BreastFragmentBinding
import com.diariodobebe.models.Baby
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Feeding
import com.diariodobebe.ui.home.HomeViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.util.*

class BreastFragment : Fragment() {
    private val calFeedingStart: Calendar = Calendar.getInstance()
    private var hourFeedingStart: Int? = null
    private var minuteFeedingStart: Int? = null

    private var _binding: BreastFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BreastViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[BreastViewModel::class.java]
        _binding = BreastFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
                binding.timeBreastRight,
                binding.timeBreastLeft
            )

            val file = File(
                requireActivity().filesDir,
                requireActivity().getSharedPreferences(
                    getString(R.string.PREFS),
                    Context.MODE_PRIVATE
                ).getString("baby", "") + ".json"
            )
            val babyJson = String(file.readBytes(), StandardCharsets.UTF_8)
            val baby = GsonBuilder()
                .registerTypeAdapter(Baby::class.java, HomeViewModel.Deserializer())
                .create()
                .fromJson(babyJson, Baby::class.java)

            if (check) {
                var id = baby!!.lastEntryId
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
                    Feeding.FeedingType.FEEDING_BREAST,
                    if (binding.timeBreastRight.text.isEmpty()) 0 else binding.timeBreastRight.text.toString()
                        .toInt(),
                    if (binding.timeBreastLeft.text.isEmpty()) 0 else binding.timeBreastLeft.text.toString()
                        .toInt(),

                    )
                if (!binding.edBreastComment.text.isNullOrBlank()) {
                    feeding.comment = binding.edBreastComment.text.toString()
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