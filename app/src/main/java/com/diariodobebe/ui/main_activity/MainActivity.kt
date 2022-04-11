package com.diariodobebe.ui.main_activity

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityMainBinding
import com.diariodobebe.helpers.*
import com.diariodobebe.models.Baby
import com.diariodobebe.ui.IntroActivity
import com.diariodobebe.ui.main_activity.home.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

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

                        if (baby.entryList!= null) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel(application)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        installSplashScreen().setKeepOnScreenCondition {
            createNotificationChannel()
            scheduleNotifications()

            if (viewModel.isLoading.value) {
                return@setKeepOnScreenCondition true
            } else {
                if (!viewModel.hasAlreadyLogged.value) {
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                } else {
                    val drawerLayout: DrawerLayout = binding.drawerLayout
                    val navView: NavigationView = binding.navView
                    val navController = findNavController(R.id.nav_host_fragment_content_main)

                    appBarConfiguration = AppBarConfiguration(
                        setOf(
                            R.id.nav_home, R.id.nav_premium
                        ), drawerLayout
                    )
                    setupActionBarWithNavController(navController, appBarConfiguration)
                    navView.setupWithNavController(navController)
                }

                getBabyData(viewModel)
                return@setKeepOnScreenCondition false
            }
        }

    }

    override fun onResume() {
        getBabyData(viewModel)
        super.onResume()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                if (checkPermission()) {
                    exportFile()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        exportFile()
                    }

                }
            }
            R.id.action_import -> {
                if (getSharedPreferences(getString(R.string.PREFS), Context.MODE_PRIVATE)
                        .getBoolean(getString(R.string.PREMIUM), false)
                ) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/json"
                    registerForResult.launch(intent)
                } else
                    Toast.makeText(this, getString(R.string.premium_only), Toast.LENGTH_SHORT)
                        .show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exportFile() {
        if (getSharedPreferences(getString(R.string.PREFS), Context.MODE_PRIVATE)
                .getBoolean(getString(R.string.PREMIUM), false)
        ) {
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
                Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, getString(R.string.premium_only), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getBabyData(viewModel: MainViewModel) {
        lifecycleScope.launch {
            viewModel.loadBaby()
            while (viewModel.isLoading.value) {
                continue
            }
            val header = binding.navView.getHeaderView(0)
            val baby = viewModel.baby
            if (baby != null) {
                val bitmap = BitmapFactory.decodeStream(File(baby.picPath!!).inputStream())
                header.findViewById<ImageView>(R.id.img_baby_pic).setImageBitmap(bitmap)
                header.findViewById<ImageView>(R.id.img_baby_pic).setPadding(0, 0, 0, 0)
                header.findViewById<TextView>(R.id.tv_baby_name).text = baby.name

                val cal = Calendar.getInstance()
                cal.timeInMillis = baby.birthDate!!
                val diff: Long = Calendar.getInstance().time.time - cal.time.time
                var secondsAge = (diff / 1000).toDouble()

                val yearsAge = floor(secondsAge * 0.000000031689)
                secondsAge -= yearsAge / 0.000000031689
                val monthsAge = floor(secondsAge * 0.00000038026)
                secondsAge -= monthsAge / 0.00000038026
                val daysAge = floor(secondsAge * 0.000011574)

                header.findViewById<TextView>(R.id.tv_baby_age).text = getString(
                    R.string.age_template,
                    yearsAge.toInt(),
                    monthsAge.toInt(),
                    daysAge.toInt()
                )
            } else {
                header.findViewById<TextView>(R.id.tv_baby_name).text =
                    getString(R.string.app_name)
                header.findViewById<TextView>(R.id.tv_baby_age).text =
                    getString(R.string.support_email)
            }
        }
    }

    private fun scheduleNotifications() {
        /*val scheduledIds = getSharedPreferences(
            getString(R.string.PREFS),
            Context.MODE_PRIVATE
        ).getStringSet(getString(R.string.PREFS_NOTIFICATIONS_IDS), null)*/

        val cal = Calendar.getInstance()
        var currentTimeLong = cal.timeInMillis
        val dayInMillis = 60000 //86400000

        val notificationIds: MutableList<String> = mutableListOf()


        for (i in 1..10) {
            notificationIds.add(i.toString())
            val intent = Intent(this, Notification::class.java)
            val title = getString(R.string.notification_title_template)
            val message = getString(R.string.notification_message_template, i)

            intent.putExtra(TITLE_EXTRA, title)
            intent.putExtra(MESSAGE_EXTRA, message)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                i,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
            )

            currentTimeLong += dayInMillis

            AlarmManagerCompat.setExactAndAllowWhileIdle(
                getSystemService(
                    ALARM_SERVICE
                ) as AlarmManager, AlarmManager.RTC_WAKEUP, currentTimeLong, pendingIntent
            )
        }

        getSharedPreferences(getString(R.string.PREFS), Context.MODE_PRIVATE).edit {
            putStringSet(getString(R.string.PREFS_NOTIFICATIONS_IDS), notificationIds.toSet())
            apply()
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.app_name)
        val desc = getString(R.string.notification_channel_desc)
        val importance = NotificationManagerCompat.IMPORTANCE_DEFAULT

        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance)
        channel.setName(name)
        channel.setDescription(desc)

        NotificationManagerCompat.from(this).createNotificationChannel(channel.build())

    }
}