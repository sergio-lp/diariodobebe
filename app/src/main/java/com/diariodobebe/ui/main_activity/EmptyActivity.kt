package com.diariodobebe.ui.main_activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityEmptyBinding
import com.diariodobebe.ui.dialogs.ReviewDialog
import com.diariodobebe.ui.main_activity.about.AboutFragment
import com.diariodobebe.ui.main_activity.premium.PremiumFragment

class EmptyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmptyBinding

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmptyBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val type = (intent.extras?.getString(FRAGMENT))

        val fragment: Fragment?

        when (type) {
            PREMIUM -> {
                fragment = PremiumFragment()
                supportActionBar?.title = getString(R.string.be_premium)
            }
            INFO -> {
                fragment = AboutFragment()
                supportActionBar?.title = getString(R.string.menu_about)
            }
            REVIEW -> {
                fragment = ReviewDialog()
                supportActionBar?.title = getString(R.string.menu_rate)
            }
            else -> {
                fragment = null
            }
        }

        if (fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_view, fragment).commit()
            setContentView(binding.root)
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val FRAGMENT = "fragment"
        const val PREMIUM = "premium"
        const val INFO = "info"
        const val REVIEW = "review"
    }
}