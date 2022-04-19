package com.diariodobebe.ui.entry_activities.feeding_activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diariodobebe.EXTRA_ENTRY
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddFeedingBinding
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Feeding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.*

class AddFeedingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFeedingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (PremiumStatus.isPremium(this)) {
            PremiumStatus.processPremium(binding.adView, binding.root)
        } else {
            MobileAds.initialize(this)
            binding.adView.loadAd(AdRequest.Builder().build())
        }

        val feeding = intent.extras?.getParcelable<Feeding>(EXTRA_ENTRY)
        if (feeding != null) {
            setWithData(feeding)
        } else {
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
        }
    }

    private fun setWithData(feeding: Feeding) {
        val dfDate = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD)
        val dfHour = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

        supportActionBar?.title = getString(R.string.edit_entry)

        dfDate.timeZone = TimeZone.getTimeZone("UTC")
        dfHour.timeZone = TimeZone.getTimeZone("UTC")

        var title = ""

        var fragment: Fragment? = null
        when (feeding.feedingType) {
            Feeding.FeedingType.FEEDING_BREAST -> {
                fragment = BreastFragment()
                title = getString(R.string.breast)
                fragment.arguments?.putParcelable(EXTRA_ENTRY, feeding)
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_ENTRY, feeding)
                fragment.arguments = bundle
                fragment.finalDate = feeding.date
            }
            Feeding.FeedingType.FEEDING_BOTTLE -> {
                fragment = BottleFragment()
                title = getString(R.string.feeding_bottle)
                fragment.arguments?.putParcelable(EXTRA_ENTRY, feeding)
                val bundle = Bundle()
                bundle.putParcelable(EXTRA_ENTRY, feeding)
                fragment.arguments = bundle
                fragment.finalDate = feeding.date
            }
            Feeding.FeedingType.FEEDING_FOOD -> {
                fragment = FoodFragment()
                val bundle = Bundle()
                title = getString(R.string.food)
                bundle.putParcelable(EXTRA_ENTRY, feeding)
                fragment.arguments = bundle
                fragment.finalDate = feeding.date
            }
        }

        if (fragment != null) {
            val fragmentList = mutableListOf<Fragment>()
            fragmentList.add(fragment)
            binding.viewPager.adapter =
                FeedingPagerAdapter(fragmentList, supportFragmentManager, lifecycle)

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, _ ->
                tab.text = title

            }.attach()
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