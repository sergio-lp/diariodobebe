package com.diariodobebe.ui.entry_activities.feeding_activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddFeedingBinding
import com.diariodobebe.helpers.PremiumStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator

class AddFeedingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFeedingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(BreastFragment())
        fragmentList.add(BottleFragment())
        fragmentList.add(FoodFragment())

        binding.viewPager.adapter =
            FeedingPagerAdapter(fragmentList, supportFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = getString(R.string.breast)
                1 -> tab.text = getString(R.string.feeding_bottle)
                2 -> tab.text = getString(R.string.food)
            }
        }.attach()

        if (PremiumStatus.isPremium(this)) {
            PremiumStatus.processPremium(binding.adView, binding.root)
        } else {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private class FeedingPagerAdapter(
        val fragmentList: List<Fragment>,
        fragManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragManager, lifecycle) {
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }
}