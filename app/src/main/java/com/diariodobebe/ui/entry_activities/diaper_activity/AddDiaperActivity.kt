package com.diariodobebe.ui.entry_activities.diaper_activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddDiaperBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Diaper
import com.diariodobebe.models.Entry
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddDiaperActivity : AppCompatActivity() {
    private var finalDate: Long = 0

    private lateinit var binding: ActivityAddDiaperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDiaperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ArrayAdapter.createFromResource(
            this,
            R.array.diaper_states,
            android.R.layout.simple_spinner_dropdown_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDiaperState.adapter = it
        }

        binding.edDiaperDate.setOnClickListener {
            binding.edDiaperDate.requestFocus()
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

                if (!binding.edDiaperTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat("HH:mm")
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edDiaperTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edDiaperDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edDiaperTime.setOnClickListener {
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

                if (!binding.edDiaperDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edDiaperDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edDiaperTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        binding.btnAddDiaper.setOnClickListener {
            if (checkNecessaryEds(binding.edDiaperTime, binding.edDiaperDate)) {
                val diaper = Diaper(
                    null,
                    finalDate,
                    Entry.EntryType.ENTRY_DIAPER,
                    binding.edDiaperComment.text.toString(),
                    binding.edDiaperBrand.text.toString(),
                    binding.spinnerDiaperState.selectedItemPosition
                )

                GetBaby.insertEntry(diaper, this)
            }
        }

        if (PremiumStatus.isPremium(this)) {
            PremiumStatus.processPremium(binding.adView, binding.root)
        } else {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
        }
    }

    private fun checkNecessaryEds(
        edTime: EditText,
        edDate: EditText,
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edTime.text.isNullOrBlank()) {
            edTime.error = getString(R.string.please_fill_field)
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