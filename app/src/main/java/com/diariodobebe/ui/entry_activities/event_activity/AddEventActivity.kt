package com.diariodobebe.ui.entry_activities.event_activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.diariodobebe.EXTRA_ENTRY
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddEventBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Event
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private var finalDate: Long = 0
    private var entryId: Int? = null
    private lateinit var binding: ActivityAddEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.edEventDate.setOnClickListener {
            binding.edEventDate.requestFocus()
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

                if (!binding.edEventTime.text.isNullOrEmpty()) {
                    val hour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)
                    hour.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += hour.parse(binding.edEventTime.text.toString())!!.time
                }

                val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                df.timeZone = TimeZone.getTimeZone("UTC")
                binding.edEventDate.setText(
                    df.format(Date(dateToSet))
                )
                finalDate = dateToSet
            }
        }

        binding.edEventTime.setOnClickListener {
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

                if (!binding.edEventDate.text.isNullOrEmpty()) {
                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    dateToSet += df.parse(binding.edEventDate.text.toString())!!.time
                }

                val strHour: String =
                    if (hour > 9) hour.toString() else "0$hour"
                val strMinute: String =
                    if (minute > 9) minute.toString() else "0$minute"

                binding.edEventTime.setText(
                    getString(
                        R.string.template_hour,
                        strHour,
                        strMinute
                    )
                )

                finalDate = dateToSet
            }

        }

        binding.btnAddEvent.setOnClickListener {
            if (checkEditTexts(
                    binding.edEventTime,
                    binding.edEventDate,
                    binding.edEventDescription
                )
            ) {
                val event = Event(
                    entryId,
                    finalDate,
                    Entry.EntryType.ENTRY_EVENT,
                    binding.edEventComment.text.toString(),
                    binding.edEventDescription.text.toString()
                )

                GetBaby.insertEntry(event, this)
            }
        }

        if (PremiumStatus.isPremium(this)) {
            PremiumStatus.processPremium(binding.adView, binding.root)
        } else {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
        }

        intent.extras?.getParcelable<Event>(EXTRA_ENTRY).let {
            val event = it ?: run {
                return@let
            }

            supportActionBar?.title = getString(R.string.edit_entry)

            entryId = it.id

            binding.edEventDescription.setText(event.activityType)
            binding.edEventComment.setText(event.comment)

            val dfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
            val dfHour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            dfDate.timeZone = TimeZone.getTimeZone("UTC")
            dfHour.timeZone = TimeZone.getTimeZone("UTC")

            binding.edEventDate.setText(dfDate.format(event.date))
            binding.edEventTime.setText(dfHour.format(event.date))

            finalDate = event.date ?: 0

            binding.btnAddEvent.text = getString(R.string.edit_entry)
        }
    }

    private fun checkEditTexts(
        edTime: EditText,
        edDate: EditText,
        edActivityDescription: EditText
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edTime.text.isNullOrBlank()) {
            edTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (edActivityDescription.text.isNullOrEmpty()) {
            edActivityDescription.error = getString(R.string.please_fill_field)
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