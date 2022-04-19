package com.diariodobebe.ui.main_activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityMainBinding
import com.diariodobebe.helpers.CHANNEL_ID
import com.diariodobebe.helpers.MESSAGE_EXTRA
import com.diariodobebe.helpers.Notification
import com.diariodobebe.helpers.TITLE_EXTRA
import com.diariodobebe.ui.IntroActivity
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel(application)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.title = ""

        installSplashScreen().setKeepOnScreenCondition {
            createNotificationChannel()
            scheduleNotifications()

            if (viewModel.isLoading.value) {
                return@setKeepOnScreenCondition true
            } else {
                if (!viewModel.hasAlreadyLogged.value) {
                    startActivity(Intent(this, IntroActivity::class.java))
                    finish()
                }
                /* else {
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
                }*/

                //getBabyData(viewModel)
                return@setKeepOnScreenCondition false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        getBabyData(viewModel)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
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

            binding.appBarMain.imgBaby.setPadding(0, 0, 0, 0)
            val baby = viewModel.baby
            if (baby != null) {
                binding.appBarMain.imgBaby.setImageBitmap(BitmapFactory.decodeStream(baby.picPath?.let {
                    File(
                        it
                    ).inputStream()
                }))

                binding.appBarMain.tvBabyName.text = baby.name
            }
        }
        /*
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

        }*/
    }

    private fun scheduleNotifications() {
        val cal = Calendar.getInstance()
        var currentTimeLong = cal.timeInMillis
        val tempoEmMiliSegundos =
            86400000 //86400000 //INTERVALO ENTRE AS NOTIFICAÇÕES. PADRÃO = 1 DIA

        //TEXTO DAS NOTIFICAÇÕES
        val msg1 = getString(R.string.notification_message_template, 1)
        val msg2 = getString(R.string.notification_message_template, 2)
        val msg3 = getString(R.string.notification_message_template, 3)
        val msg4 = getString(R.string.notification_message_template, 4)
        val msg5 = getString(R.string.notification_message_template, 5)
        val msg6 = getString(R.string.notification_message_template, 6)
        val msg7 = getString(R.string.notification_message_template, 7)
        val msg8 = getString(R.string.notification_message_template, 8)
        val msg9 = getString(R.string.notification_message_template, 9)
        val msg10 = getString(R.string.notification_message_template, 10)
        //TITULO DAS NOTIFICAÇÕES
        val titulo1 = getString(R.string.notification_title_template)
        val titulo2 = getString(R.string.notification_title_template)
        val titulo3 = getString(R.string.notification_title_template)
        val titulo4 = getString(R.string.notification_title_template)
        val titulo5 = getString(R.string.notification_title_template)
        val titulo6 = getString(R.string.notification_title_template)
        val titulo7 = getString(R.string.notification_title_template)
        val titulo8 = getString(R.string.notification_title_template)
        val titulo9 = getString(R.string.notification_title_template)
        val titulo10 = getString(R.string.notification_title_template)

        val notificationMap = mutableMapOf<Int, Pair<String, String>>()
        notificationMap[0] = Pair(titulo1, msg1)
        notificationMap[1] = Pair(titulo2, msg2)
        notificationMap[2] = Pair(titulo3, msg3)
        notificationMap[3] = Pair(titulo4, msg4)
        notificationMap[4] = Pair(titulo5, msg5)
        notificationMap[5] = Pair(titulo6, msg6)
        notificationMap[6] = Pair(titulo7, msg7)
        notificationMap[7] = Pair(titulo8, msg8)
        notificationMap[8] = Pair(titulo9, msg9)
        notificationMap[9] = Pair(titulo10, msg10)

        for (i in 0..notificationMap.size) {
            val titulo = notificationMap[i]?.first
            val msg = notificationMap[i]?.second

            if (!titulo.isNullOrBlank() && !msg.isNullOrBlank()) {
                val intent = Intent(this, Notification::class.java)
                intent.putExtra(TITLE_EXTRA, titulo)
                intent.putExtra(MESSAGE_EXTRA, msg)

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    i,
                    intent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
                )

                (getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)

                currentTimeLong += tempoEmMiliSegundos

                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    getSystemService(
                        ALARM_SERVICE
                    ) as AlarmManager, AlarmManager.RTC_WAKEUP, currentTimeLong, pendingIntent
                )
            }
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