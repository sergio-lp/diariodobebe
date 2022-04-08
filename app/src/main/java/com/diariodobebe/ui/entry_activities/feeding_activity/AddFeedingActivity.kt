package com.diariodobebe.ui.entry_activities.feeding_activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.diariodobebe.R
import com.diariodobebe.databinding.ActivityAddFeedingBinding
import com.diariodobebe.models.Baby
import com.diariodobebe.ui.home.HomeFragment
import com.google.android.material.tabs.TabLayoutMediator

class AddFeedingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFeedingBinding
    private var baby: Baby? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        baby = intent.extras.takeIf { it != null }!!.getParcelable(HomeFragment.EXTRAS.EXTRA_BABY)

        val fragmentList = mutableListOf<Fragment>()
        fragmentList.add(BreastFragment())
        fragmentList.add(BottleFragment())

        binding.viewPager.adapter =
            FeedingPagerAdapter(fragmentList, supportFragmentManager, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when (pos) {
                1 -> tab.text = getString(R.string.breast)
            }
        }.attach()


        baby = intent.extras.takeIf { it != null }!!
            .getParcelable(HomeFragment.EXTRAS.EXTRA_BABY)

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
            return 1
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }
}