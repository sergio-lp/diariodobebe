package com.diariodobebe.ui.entry_activities.measurement_activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddMeasureBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Measure
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddMeasureActivity : AppCompatActivity() {
    private var finalDate: Long = 0
    private var wantsToProceed: Boolean = true

    private lateinit var binding: ActivityAddMeasureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMeasureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.edMeasureDate.setOnClickListener {
            binding.edMeasureDate.requestFocus()
            val calendarConstraints = CalendarConstraints.Builder().setValidator(
                DateValidatorPointBackward.now()
            ).build()
            val datePickerDialog = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date_hint))
                .setCalendarConstraints(calendarConstraints)
                .build()

            datePickerDialog.show(supportFragmentManager, "DATEPICKER")

            datePickerDialog.addOnPositiveButtonClickListener { date ->
                var dateToSet: Long = date

                if (!binding.edMeasureTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat("HH:mm")
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edMeasureTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edMeasureDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edMeasureTime.setOnClickListener {
            val timePickerDialog = MaterialTimePicker.Builder()
                .setTitleText(getString(R.string.pick_time_hint))
                .build()


            timePickerDialog.show(supportFragmentManager, "TIMEPICKER")

            timePickerDialog.addOnPositiveButtonClickListener {
                val hour = timePickerDialog.hour
                val minute = timePickerDialog.minute

                val hourInMillis = hour * 3600000
                val minuteInMillis = minute * 60000

                var dateToSet: Long = hourInMillis.toLong() + minuteInMillis.toLong()

                if (!binding.edMeasureDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edMeasureDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edMeasureTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }


        binding.btnAddMeasure.setOnClickListener {
            wantsToProceed =
                checkEditTexts(
                    binding.edMeasureTime,
                    binding.edMeasureDate,
                    binding.edMeasureWeight,
                    binding.edMeasureHeight
                )

            if (wantsToProceed) {
                val measure = Measure(
                    null,
                    finalDate,
                    Entry.EntryType.ENTRY_MEASUREMENT,
                    binding.edMeasureComment.text.toString(),
                    binding.edMeasureHeight.text.toString().toFloatOrNull(),
                    binding.edMeasureWeight.text.toString().toFloatOrNull()
                )

                GetBaby.insertEntry(measure, this)
            }
        }
    }

    private fun checkEditTexts(
        edTime: EditText,
        edDate: EditText,
        edWeight: EditText,
        edHeight: EditText
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edTime.text.isNullOrBlank()) {
            edTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (edHeight.text.isNullOrBlank()) {
            edHeight.error = getString(R.string.please_fill_field)
            return false
        }

        if (edWeight.text.isNullOrEmpty()) {
            edWeight.error = getString(R.string.please_fill_field)
            return false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}