package com.diariodobebe.ui.main_activity

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityMainBinding
import com.diariodobebe.ui.IntroActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel(application)
        val progressBar = ProgressBar(this)
        progressBar.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        setContentView(progressBar)
        binding = ActivityMainBinding.inflate(layoutInflater)

        installSplashScreen().setKeepOnScreenCondition {
            if (viewModel.isLoading) {
                return@setKeepOnScreenCondition true
            }

            if (viewModel.shouldShowIntro) {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            } else {
                setContentView(binding.root)
                setSupportActionBar(binding.appBarMain.toolbar)
                binding.appBarMain.fab.setOnClickListener { view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }

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

            return@setKeepOnScreenCondition false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}