package com.diariodobebe.ui.add_baby_activity

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddBabyBinding
import com.diariodobebe.models.Baby
import com.diariodobebe.ui.IntroActivity
import com.diariodobebe.ui.main_activity.MainActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.text.DateFormat
import java.util.*

class AddBabyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBabyBinding
    private var gender: Int? = null
    private var babyBirthday: Long? = null
    private var picBitmap: Bitmap? = null
    private val registerForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    val uri = it.data?.data
                    val stream = contentResolver.openInputStream(uri!!)
                    picBitmap = BitmapFactory.decodeStream(stream)
                    binding.btnBabyPic.setPadding(0, 0, 0, 0)
                    binding.btnBabyPic.setImageBitmap(picBitmap)
                } catch (e: Exception) {
                    Toast.makeText(
                        this, getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("TAG", "Error: AddBabyActivity PhotoPicker ", e)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBabyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        if (intent.extras?.getBoolean(IntroActivity.HOME_DISABLE, true) == false) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.btnMale.setOnClickListener {
            this.gender = Baby.BabyGender.BABY_GENDER_MALE
            binding.tvBabySex.text = getString(R.string.sex_template, getString(R.string.boy))
            binding.imgMale.background.mutate().colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.colorGenderMaleSelected),
                    PorterDuff.Mode.MULTIPLY
                )
            binding.imgFemale.background.mutate().colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(this, R.color.colorGenderFemaleUnselected),
                PorterDuff.Mode.MULTIPLY
            )
            binding.tvMale.setTypeface(null, Typeface.BOLD)
            binding.tvFemale.setTypeface(null, Typeface.NORMAL)
        }
        binding.btnFemale.setOnClickListener {
            this.gender = Baby.BabyGender.BABY_GENDER_FEMALE
            binding.tvBabySex.text = getString(R.string.sex_template, getString(R.string.girl))
            binding.imgMale.background.mutate().colorFilter =
                PorterDuffColorFilter(
                    ContextCompat.getColor(this, R.color.colorGenderMaleUnselected),
                    PorterDuff.Mode.MULTIPLY
                )
            binding.imgFemale.background.mutate().colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(this, R.color.colorGenderFemaleSelected),
                PorterDuff.Mode.MULTIPLY
            )
            binding.tvMale.setTypeface(null, Typeface.NORMAL)
            binding.tvFemale.setTypeface(null, Typeface.BOLD)
        }


        binding.edBabyBirthdate.setOnClickListener {
            val calConstraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.baby_birth_date))
                .setCalendarConstraints(calConstraints)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                cal.timeInMillis = it
                val dateFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD)
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                binding.edBabyBirthdate.setText(dateFormat.format(cal.time))
                this.babyBirthday = it
            }

            datePicker.show(supportFragmentManager, null)
        }

        binding.btnBabyPic.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                registerForResult.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
                Log.e("TAG", "onCreate: ", e)
            }
        }

        binding.btnAddBaby.setOnClickListener {
            if (this.gender == null) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.select_baby_gender),
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                checkBabyData(binding.edBabyName, binding.edBabyBirthdate)
            }
        }
    }

    override fun onResume() {
        Snackbar.make(binding.root, getString(R.string.please_add_baby), Snackbar.LENGTH_LONG)
            .show()
        super.onResume()
    }

    private fun checkBabyData(edName: EditText, edDate: EditText) {
        if (edName.text.isNullOrBlank()) {
            edName.error = getString(R.string.select_baby_name)
            Snackbar.make(
                binding.root,
                getString(R.string.select_baby_name),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        if (edDate.text.isNullOrBlank()) {
            edDate.error = getString(R.string.select_baby_birth)
            return
        }

        val baby =
            Baby(
                0,
                edName.text.toString(),
                gender,
                babyBirthday,
                null,
                null,
                mutableListOf()
            )

        val picFile = File(filesDir, baby.name!!.replace(" ", "-") + ".png")

        if (this.picBitmap != null) {
            baby.picPath = picFile.path
            insertBabyPic(file = picFile, bitmap = this.picBitmap!!)
        } else {
            val babyBitmap = ContextCompat.getDrawable(this, R.drawable.baby_main)?.toBitmap()
            baby.picPath = picFile.path
            insertBabyPic(babyBitmap!!, picFile)
        }

        if (picBitmap == null) {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.no_pic_warning))
                .setMessage(getString(R.string.no_pic_message, baby.name))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    insertBaby(baby)
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } else {
            insertBaby(baby)
        }
    }

    private fun insertBaby(baby: Baby) {
        val filePath = filesDir.path

        val gson = Gson()
        val json = gson.toJson(baby)

        try {
            val file = File(
                filePath, baby.name.toString()
                    .replace(" ", "-")
                    .replace("-", "") + ".json"
            )

            file.writeText(json, StandardCharsets.UTF_8)
            Toast.makeText(this, getString(R.string.baby_insert_success), Toast.LENGTH_SHORT)
                .show()
            getSharedPreferences(getString(R.string.PREFS), Context.MODE_PRIVATE).edit {
                putString("baby", baby.name!!)
                apply()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.baby_insert_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertBabyPic(bitmap: Bitmap, file: File) {
        val writeStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, writeStream)
        writeStream.flush()
        writeStream.close()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}