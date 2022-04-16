package com.diariodobebe.ui.entry_activities.health_activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddHealthBinding
import com.diariodobebe.helpers.GetBaby
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Health
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator

class AddHealthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddHealthBinding
    private var wantsToProceed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragmentList = mutableListOf<Fragment>()

        val healthFragment = HealthConditionFragment()
        val medFragment = MedicationFragment()
        val vitalsFragment = VitalsFragment()

        fragmentList.add(healthFragment)
        fragmentList.add(medFragment)
        fragmentList.add(vitalsFragment)

        binding.viewPager.adapter =
            HealthPagerAdapter(fragmentList, supportFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = getString(R.string.condition)
                1 -> tab.text = getString(R.string.medication)
                2 -> tab.text = getString(R.string.vital_signs)
            }
        }.attach()

        binding.btnAddHealth.setOnClickListener {
            wantsToProceed = checkInput(healthFragment)

            if (wantsToProceed) {
                val healthEvent = Health(
                    null,
                    healthFragment.finalDate,
                    Entry.EntryType.ENTRY_HEALTH,
                    healthFragment.binding.edHealthComment.text.toString(),
                    healthFragment.binding.edHealthCondition.text.toString(),
                    medFragment.binding.edMedName.text.toString(),
                    medFragment.binding.edMedAmount.text.toString().toIntOrNull(),
                    vitalsFragment.binding.edVitalsTemperature.text.toString().toIntOrNull(),
                    vitalsFragment.mood,
                    healthFragment.symptoms.toTypedArray()
                )

                GetBaby.insertEntry(healthEvent, this)
            }
        }

        if (PremiumStatus.isPremium(this)) {
            (binding.btnAddHealth.layoutParams as RelativeLayout.LayoutParams).bottomMargin = 0
            PremiumStatus.processPremium(binding.adView, binding.root)
        } else {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
        }
    }

    private fun checkInput(
        conditionFragment: HealthConditionFragment
    ): Boolean {
        //Condition fragment checking
        if (conditionFragment.binding.edHealthDate.text.isNullOrEmpty()) {
            conditionFragment.binding.edHealthDate.error = getString(R.string.please_fill_field)
            binding.viewPager.currentItem = 0
            return false
        }

        if (conditionFragment.binding.edHealthTime.text.isNullOrEmpty()) {
            conditionFragment.binding.edHealthTime.error = getString(R.string.please_fill_field)
            binding.viewPager.currentItem = 0
            return false
        }

        if (conditionFragment.binding.edHealthCondition.text.isNullOrEmpty()) {
            conditionFragment.binding.edHealthTime.error = getString(R.string.please_fill_field)
            binding.viewPager.currentItem = 0
            return false
        }

        return true
    }

    private fun showConfirmationDialog(emptyField: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.field_empty)
            .setMessage(
                getString(
                    R.string.field_empty_template,
                    emptyField.lowercase()
                )
            )
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                wantsToProceed = false
                dialog.dismiss()
            }.create().show()
    }

    private class HealthPagerAdapter(
        val fragmentList: MutableList<Fragment>,
        supportFragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(supportFragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}