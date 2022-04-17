package com.diariodobebe.ui.main_activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivitySettingsBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.helpers.SendMail
import com.diariodobebe.models.Baby
import com.diariodobebe.ui.main_activity.home.HomeViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*
import kotlin.math.floor

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var picBitmap: Bitmap

    private val registerBabyPic =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    val uri = it.data?.data
                    val stream = contentResolver.openInputStream(uri!!)
                    picBitmap = BitmapFactory.decodeStream(stream)
                    binding.imgBabyPic.setPadding(0, 0, 0, 0)
                    val picFile =
                        File(filesDir, viewModel.baby?.name?.replace(" ", "-") + ".png")
                    binding.imgBabyPic.setImageBitmap(picBitmap)
                    viewModel.baby?.picPath = picFile.path
                    insertBabyPic(picBitmap, picFile)
                    GetBaby.updateBaby(
                        this,
                        viewModel.baby?.name,
                        viewModel.baby?.birthDate,
                        viewModel.baby?.picPath,
                        viewModel.baby?.sex
                    )
                } catch (e: Exception) {
                    Toast.makeText(
                        this, getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("TAG", "Error: AddBabyActivity PhotoPicker ", e)
                }
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                exportFile()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    private var importJson: String? = null
    private val registerForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                try {
                    val uri = it.data?.data
                    val stream = contentResolver.openInputStream(uri!!)
                    importJson = String(stream!!.readBytes())

                    if (viewModel.baby != null) {
                        val file = GetBaby.getBabyFile(this)
                        val baby = GetBaby.getBaby(file)
                        file.delete()

                        val gson = GsonBuilder().registerTypeAdapter(
                            Baby::class.java,
                            HomeViewModel.Deserializer()
                        )
                            .create()
                        val importedBaby = gson
                            .fromJson(
                                importJson,
                                Baby::class.java
                            )

                        if (baby.entryList != null) {
                            baby.entryList!!.addAll(importedBaby.entryList!!)
                        } else {
                            baby.entryList = mutableListOf()
                            baby.entryList!!.addAll(importedBaby.entryList!!)
                        }
                        file.writeText(gson.toJson(baby))

                        Toast.makeText(this, getString(R.string.success_import), Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this, getString(R.string.error),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("TAG", "Error: AddBabyActivity PhotoPicker ", e)
                }
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = MainViewModel(application)
        viewModel.loadBaby()

        if (viewModel.baby != null) {
            loadBabyData()
        }

        binding.cardBackup.setOnClickListener {
            exportData()
        }

        binding.importCard.setOnClickListener {
            importData()
        }

        binding.cardPremium.setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            intent.putExtra(EmptyActivity.FRAGMENT, EmptyActivity.PREMIUM)
            startActivity(intent)
        }

        binding.cardAbout.setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            intent.putExtra(EmptyActivity.FRAGMENT, EmptyActivity.INFO)
            startActivity(intent)
        }

        binding.reviewCard.setOnClickListener {
            val reviewManager = ReviewManagerFactory.create(this)
            val managerInfoTask = reviewManager.requestReviewFlow()
            managerInfoTask
                .addOnSuccessListener {
                    reviewManager.launchReviewFlow(this, it)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.e("TAG", "onCreate: Rating success")
                            }
                        }
                }
                .addOnFailureListener {
                    Snackbar.make(binding.root, getString(R.string.error), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.send_email)) {
                            SendMail.SendMail.sendSupportMail(this)
                        }.show()
                }

        }

        binding.app1.setOnClickListener {
            val url = getString(R.string.app_1_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.tvSupportDesc.text =
            "${binding.tvSupportDesc.text}\n\n${getString(R.string.support_email)}"

        binding.supportCard.setOnClickListener {
            val i = Intent(Intent.ACTION_SENDTO)
            i.type = "message/rfc822"
            i.data = Uri.parse("mailto:")
            i.putExtra(Intent.EXTRA_EMAIL, getString(R.string.support_email))
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            startActivity(Intent.createChooser(i, getString(R.string.send_email)))
        }

        binding.cardInfo.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.dialog_edit_baby, null)

            if (viewModel.baby != null) {
                var sex = viewModel.baby?.sex ?: 0
                val imgMale = view.findViewById<ImageView>(R.id.img_male)
                val imgFemale = view.findViewById<ImageView>(R.id.img_female)
                val tvMale = view.findViewById<TextView>(R.id.tv_male)
                val tvFemale = view.findViewById<TextView>(R.id.tv_female)
                val edBirth = view.findViewById<EditText>(R.id.ed_baby_birthdate)
                var birth = viewModel.baby?.birthDate
                edBirth.setOnClickListener {
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
                        edBirth.setText(dateFormat.format(cal.time))
                        birth = it
                    }

                    datePicker.show(supportFragmentManager, null)
                }

                view.findViewById<LinearLayout>(R.id.btn_male).setOnClickListener {
                    sex = Baby.BabyGender.BABY_GENDER_MALE
                    imgMale.background.mutate().colorFilter =
                        PorterDuffColorFilter(
                            ContextCompat.getColor(this, R.color.colorGenderMaleSelected),
                            PorterDuff.Mode.MULTIPLY
                        )
                    imgFemale.background.mutate().colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.colorGenderFemaleUnselected),
                        PorterDuff.Mode.MULTIPLY
                    )
                    tvMale.setTypeface(null, Typeface.BOLD)
                    tvFemale.setTypeface(null, Typeface.NORMAL)
                }
                view.findViewById<LinearLayout>(R.id.btn_female).setOnClickListener {
                    sex = Baby.BabyGender.BABY_GENDER_MALE
                    imgMale.background.mutate().colorFilter =
                        PorterDuffColorFilter(
                            ContextCompat.getColor(this, R.color.colorGenderMaleUnselected),
                            PorterDuff.Mode.MULTIPLY
                        )
                    imgFemale.background.mutate().colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(this, R.color.colorGenderFemaleSelected),
                        PorterDuff.Mode.MULTIPLY
                    )
                    tvMale.setTypeface(null, Typeface.NORMAL)
                    tvFemale.setTypeface(null, Typeface.BOLD)
                }
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.edit_data))
                    .setView(view)
                    .setPositiveButton(getString(R.string.OK)) { _, _ ->
                        val edName = view.findViewById<EditText>(R.id.ed_baby_name)

                        if (!edName.text.isNullOrBlank()) {
                            viewModel.baby?.name = edName.text.toString()
                        }

                        val picFile =
                            File(filesDir, viewModel.baby?.name?.replace(" ", "-") + ".png")
                        viewModel.baby?.sex = sex
                        viewModel.baby?.picPath = picFile.path
                        viewModel.baby?.birthDate = birth
                        viewModel.baby?.entryList = viewModel.baby?.entryList
                        viewModel.baby?.id = viewModel.baby?.id
                        viewModel.baby?.lastEntryId = viewModel.baby?.lastEntryId
                        insertBabyPic(picBitmap, picFile)
                        GetBaby.updateBaby(
                            this,
                            viewModel.baby?.name,
                            viewModel.baby?.birthDate,
                            viewModel.baby?.picPath,
                            viewModel.baby?.sex
                        )
                        loadBabyData()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                    }
                    .setCancelable(true)
                    .show()
            } else {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }

        binding.babyPicCard.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                registerBabyPic.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show()
                Log.e("TAG", "onCreate: ", e)
            }

        }
    }

    private fun loadBabyData() {
        binding.tvBabyName.text = viewModel.baby?.name.toString()
        val cal = Calendar.getInstance()
        cal.timeInMillis = viewModel.baby?.birthDate!!
        val diff: Long = Calendar.getInstance().time.time - cal.time.time
        var secondsAge = (diff / 1000).toDouble()

        val yearsAge = floor(secondsAge * 0.000000031689)
        secondsAge -= yearsAge / 0.000000031689
        val monthsAge = floor(secondsAge * 0.00000038026)
        secondsAge -= monthsAge / 0.00000038026
        val daysAge = floor(secondsAge * 0.000011574)

        binding.tvBabyAge.text = getString(
            R.string.age_template,
            yearsAge.toInt(),
            monthsAge.toInt(),
            daysAge.toInt()
        )

        try {
            val file = viewModel.baby?.picPath?.let { File(it) }
            picBitmap = BitmapFactory.decodeStream(file?.inputStream())
            binding.imgBabyPic.setImageBitmap(picBitmap)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
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

    private fun exportFile() {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            getString(R.string.app_name) + ".json"
        )
        try {
            file.writeText(GetBaby.getBabyFile(this).readText())
            Toast.makeText(
                this,
                getString(R.string.success_export, file.path),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Log.e("TAG", "exportFile: ", e)
            Toast.makeText(
                this, getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun exportData() {
        if (PremiumStatus.isPremium(this)) {
            if (checkPermission()) {
                exportFile()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    exportFile()
                }
            }
        } else {
            PremiumStatus.showPremiumOffer(layoutInflater)
        }
    }

    private fun importData() {
        if (PremiumStatus.isPremium(this)) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/json"
            registerForResult.launch(intent)
        } else
            PremiumStatus.showPremiumOffer(layoutInflater)

    }
}
