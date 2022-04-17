package com.diariodobebe.ui.entry_activities.picture_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityPictureBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Photo
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PictureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPictureBinding
    private var finalDate: Long = 0
    private var picBitmap: Bitmap? = null
    private val registerForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    val uri = it.data?.data
                    val stream = contentResolver.openInputStream(uri!!)
                    picBitmap = BitmapFactory.decodeStream(stream)
                    binding.cardPic.setPadding(0, 0, 0, 0)
                    binding.babyPicture.setPadding(0, 0, 0, 0)
                    binding.babyPicture.setImageBitmap(picBitmap)
                    binding.tvPicHint.visibility = View.GONE
                } catch (e: Exception) {
                    Toast.makeText(
                        this, getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("TAG", "Error: AddBabyActivity PhotoPicker ", e)
                }
            }
        }

    private val fromBottomAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottomAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!intent.extras.takeIf { it != null }!!.getBoolean(Photo.EXTRA_HAS_PHOTO, false)) {
            binding.edPictureDate.setOnClickListener {
                binding.edPictureDate.requestFocus()
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

                    if (!binding.edPictureDate.text.isNullOrEmpty()) {
                        val hour = SimpleDateFormat("HH:mm")
                        hour.timeZone = TimeZone.getTimeZone("UTC")
                        dateToSet += hour.parse(binding.edPictureDate.text.toString())!!.time
                    }

                    val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                    df.timeZone = TimeZone.getTimeZone("UTC")
                    binding.edPictureDate.setText(
                        df.format(Date(dateToSet))
                    )
                    finalDate = dateToSet
                }
            }

            binding.edPictureTime.setOnClickListener {
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

                    if (!binding.edPictureDate.text.isNullOrEmpty()) {
                        val df = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                        df.timeZone = TimeZone.getTimeZone("UTC")
                        dateToSet += df.parse(binding.edPictureDate.text.toString())!!.time
                    }

                    val strHour: String =
                        if (hour > 9) hour.toString() else "0$hour"
                    val strMinute: String =
                        if (minute > 9) minute.toString() else "0$minute"

                    binding.edPictureTime.setText(
                        getString(
                            R.string.template_hour,
                            strHour,
                            strMinute
                        )
                    )

                    finalDate = dateToSet
                }

            }

            binding.cardPic.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                registerForResult.launch(intent)
            }

            binding.btnAddPicture.setOnClickListener {
                if (checkNecessaryEds(binding.edPictureTime, binding.edPictureDate, picBitmap)) {
                    val fDate = SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().time)
                    val file = File(filesDir, "PIC_$fDate.png")

                    insertBabyPic(this.picBitmap!!, file)
                    Toast.makeText(
                        this,
                        getString(R.string.entry_insert_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()

                    val photo = Photo(
                        null,
                        finalDate,
                        Entry.EntryType.ENTRY_PICTURE,
                        binding.edPictureComment.text.toString(),
                        file.path
                    )

                    GetBaby.insertEntry(photo, this)
                }
            }
        } else {
            val photo =
                intent.extras.takeIf { it != null }!!.getParcelable<Entry>(Photo.EXTRA_PHOTO)
            val path = intent.extras.takeIf { it != null }!!.getString(Photo.EXTRA_PATH)
            if (photo != null) {
                val cal = Calendar.getInstance()
                cal.timeZone = TimeZone.getTimeZone("UTC")
                cal.timeInMillis = photo.date!!
                val df = DateFormat.getDateTimeInstance(
                    DateFormat.DATE_FIELD,
                    DateFormat.SHORT
                )
                df.timeZone = TimeZone.getTimeZone("UTC")

                binding.edPictureDate.setText(
                    getString(
                        R.string.pic_date_template,
                        df.format(cal.time).replace(" UTC", "")
                    )
                )
                supportActionBar?.title = getString(R.string.view_photo)
                binding.edPictureTime.visibility = View.GONE
                binding.edPictureDate.setBackgroundResource(android.R.color.transparent)
                binding.tilPictureTime.visibility = View.GONE
                binding.edPictureComment.isFocusable = false
                binding.edPictureComment.isClickable = false
                binding.edPictureComment.background = null

                if (!photo.comment.isNullOrEmpty()) {
                    binding.edPictureComment.setText(photo.comment.toString())
                } else {
                    binding.edPictureComment.visibility = View.GONE
                }
                binding.btnAddPicture.visibility = View.GONE
                binding.tvPicHint.text = getString(R.string.click_to_view)
                (binding.babyPicture.layoutParams as LinearLayout.LayoutParams).height = 800
                val pic = getBabyPic(File(path.toString()))
                binding.babyPicture.setImageBitmap(pic)
                binding.babyPicture.setPadding(0, 0, 0, 0)


                binding.cardPic.setOnClickListener {
                    binding.photoView.setImageBitmap(pic)

                    if (binding.photoView.visibility == View.GONE) {
                        fromBottomAnim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationEnd(p0: Animation?) {
                            }

                            override fun onAnimationRepeat(p0: Animation?) {

                            }

                            override fun onAnimationStart(p0: Animation?) {
                                binding.photoView.visibility = View.VISIBLE
                                binding.contentView.visibility = View.GONE
                            }
                        })
                        binding.photoView.startAnimation(fromBottomAnim)
                    } else {
                        toBottomAnim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationEnd(p0: Animation?) {
                                binding.photoView.visibility = View.GONE
                                binding.contentView.visibility = View.VISIBLE
                            }

                            override fun onAnimationRepeat(p0: Animation?) {

                            }

                            override fun onAnimationStart(p0: Animation?) {

                            }
                        })
                        binding.photoView.startAnimation(toBottomAnim)
                    }
                }
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
        photo: Bitmap?
    ): Boolean {
        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.please_fill_field)
            return false
        }

        if (edTime.text.isNullOrBlank()) {
            edTime.error = getString(R.string.please_fill_field)
            return false
        }

        if (photo == null) {
            Toast.makeText(this, getString(R.string.no_pic_added), Toast.LENGTH_SHORT)
                .show()
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

    private fun insertBabyPic(bitmap: Bitmap, file: File) {
        val writeStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, writeStream)
        writeStream.flush()
        writeStream.close()
    }

    private fun getBabyPic(file: File): Bitmap {
        return BitmapFactory.decodeStream(file.inputStream())
    }
}