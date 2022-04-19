package com.diariodobebe.ui.entry_activities.sleep_activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.diariodobebe.EXTRA_ENTRY
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddSleepBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Sleep
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddSleepActivity : AppCompatActivity() {
    private var finalDate: Long = 0
    private var entryId: Int? = null
    private lateinit var binding: ActivityAddSleepBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSleepBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.edSleepDate.setOnClickListener {
            binding.edSleepDate.requestFocus()
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

                if (!binding.edSleepTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edSleepTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edSleepDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edSleepTime.setOnClickListener {
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

                if (!binding.edSleepDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edSleepDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edSleepTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        binding.btnAddSleep.setOnClickListener {
            if (checkEditTexts(binding.edSleepTime, binding.edSleepDate, binding.edSleepDuration)) {
                val sleep = Sleep(
                    entryId,
                    finalDate,
                    Entry.EntryType.ENTRY_SLEEP,
                    binding.edSleepComment.text.toString(),
                    binding.edSleepDuration.text.toString().toIntOrNull()
                )

                GetBaby.insertEntry(sleep, this)
            }
        }

        if (PremiumStatus.isPremium(this)) {
            PremiumStatus.processPremium(binding.adView, binding.root)
        } else {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
        }

        intent.extras?.getParcelable<Sleep>(EXTRA_ENTRY).let {
            val sleep = it ?: run {
                return@let
            }

            supportActionBar?.title = getString(R.string.edit_entry)

            entryId = it.id

            binding.edSleepDuration.setText(sleep.duration.toString())
            binding.edSleepComment.setText(sleep.comment)

            val dfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
            val dfHour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            dfDate.timeZone = TimeZone.getTimeZone("UTC")
            dfHour.timeZone = TimeZone.getTimeZone("UTC")

            binding.edSleepDate.setText(dfDate.format(sleep.date))
            binding.edSleepTime.setText(dfHour.format(sleep.date))

            finalDate = sleep.date ?: 0

            binding.btnAddSleep.text = getString(R.string.edit_entry)
        }
    }

    private fun checkEditTexts(
        edTime: EditText,
        edDate: EditText,
        edDuration: EditText,
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edTime.text.isNullOrBlank()) {
            edTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (edDuration.text.isNullOrBlank()) {
            edDuration.error = getString(R.string.please_fill_field)
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