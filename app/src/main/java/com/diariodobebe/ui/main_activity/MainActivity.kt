package com.diariodobebe.ui.main_activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import com.diariodobebe.ui.IntroActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel(application)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        installSplashScreen().setKeepOnScreenCondition {
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
                header.findViewById<TextView>(R.id.tv_baby_name).text = getString(R.string.app_name)
                header.findViewById<TextView>(R.id.tv_baby_age).text =
                    getString(R.string.support_email)
            }
        }
    }

}