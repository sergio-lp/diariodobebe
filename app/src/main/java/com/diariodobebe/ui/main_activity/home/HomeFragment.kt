package com.diariodobebe.ui.main_activity.home

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.core.view.iterator
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diariodobebe.R
import com.diariodobebe.adapters.EntryAdapter
import com.diariodobebe.databinding.FragmentHomeBinding
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.models.Entry
import com.diariodobebe.models.Photo
import com.diariodobebe.ui.add_baby_activity.AddBabyActivity
import com.diariodobebe.ui.entry_activities.diaper_activity.AddDiaperActivity
import com.diariodobebe.ui.entry_activities.event_activity.AddEventActivity
import com.diariodobebe.ui.entry_activities.feeding_activity.AddFeedingActivity
import com.diariodobebe.ui.entry_activities.health_activity.AddHealthActivity
import com.diariodobebe.ui.entry_activities.measurement_activity.AddMeasureActivity
import com.diariodobebe.ui.entry_activities.picture_activity.PictureActivity
import com.diariodobebe.ui.entry_activities.sleep_activity.AddSleepActivity
import com.diariodobebe.ui.main_activity.premium.PremiumViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var entryList: MutableList<Entry> = mutableListOf()
    private var entryIdList: MutableList<Int> = mutableListOf()
    private lateinit var viewModel: HomeViewModel
    private var isFabExpanded = false
    private var isBabyAlreadySet = false

    private lateinit var premiumViewModel: PremiumViewModel

    private val fromBottomAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottomAnim: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        MainScope().launch {
            binding.swipeRefresh.setOnRefreshListener {
                MainScope().launch {
                    delay(600)
                    binding.swipeRefresh.isRefreshing = false
                }
            }

            binding.btnAddBaby.setOnClickListener {
                startActivity(Intent(activity, AddBabyActivity::class.java))
            }

            binding.fab.setOnClickListener {
                isFabExpanded = !isFabExpanded
                toggleFab()
            }

            binding.btnAddEntry.setOnClickListener {
                binding.fab.performClick()
            }

            if (PremiumStatus.isPremium(requireContext())) {
                processPremium()
            } else {
                MobileAds.initialize(requireContext())
                binding.adView.loadAd(AdRequest.Builder().build())
            }
        }

        premiumViewModel = PremiumViewModel(requireActivity().application)

        return root
    }

    private fun toggleFab() {
        val visibility = if (isFabExpanded) View.VISIBLE else View.GONE
        val animationChild = if (isFabExpanded) fromBottomAnim else toBottomAnim
        val onClickListener =
            if (isFabExpanded) View.OnClickListener {
                isFabExpanded = !isFabExpanded
                toggleFab()
            }
            else View.OnClickListener {
                return@OnClickListener
            }

        binding.rvRoot.setOnClickListener(onClickListener)

        binding.fab.icon = if (isFabExpanded) ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_add_animated_open
        )
        else ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_add_animated_close
        )

        if (isFabExpanded) {
            for (c in binding.fabExpandedLayout.children) {
                for (view in c as ViewGroup) {
                    view.visibility = View.VISIBLE
                }
            }
        } else {
            for (c in binding.fabExpandedLayout.children) {
                for (view in c as ViewGroup) {
                    view.visibility = View.GONE
                }
            }
        }

        binding.fabExpandedLayout.visibility = visibility

        binding.fabExpandedLayout.startAnimation(animationChild)
        (binding.fab.icon as AnimatedVectorDrawable).start()

        if (binding.rvRoot.visibility == View.VISIBLE) {
            if (!isFabExpanded) {
                setUpFABs(false)
                val colorFrom =
                    ContextCompat.getColor(requireContext(), R.color.color_transparent)
                val colorTo = ContextCompat.getColor(requireContext(), R.color.color_mask)

                val animator = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
                animator.duration = 500
                animator.addUpdateListener {
                    binding.maskView.foreground = (it.animatedValue as Int).toDrawable()
                }

                animator.start()
            } else {
                setUpFABs(true)
                val colorFrom =
                    ContextCompat.getColor(requireContext(), R.color.color_mask)
                val colorTo =
                    ContextCompat.getColor(requireContext(), R.color.color_transparent)

                val animator = ValueAnimator.ofObject(ArgbEvaluator(), colorTo, colorFrom)
                animator.duration = 500
                animator.addUpdateListener {
                    binding.maskView.foreground = (it.animatedValue as Int).toDrawable()
                }

                animator.start()
            }
        }

        if (binding.llNoEntry.visibility == View.VISIBLE) {
            val animation = AlphaAnimation(1f, 0f)
            animation.duration = 500
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    binding.llNoEntry.visibility = View.INVISIBLE
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            binding.llNoEntry.startAnimation(animation)
        } else if (binding.llNoEntry.visibility == View.INVISIBLE) {
            val animation = AlphaAnimation(0f, 1f)
            animation.duration = 500
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                    binding.llNoEntry.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(p0: Animation?) {
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
            binding.llNoEntry.startAnimation(animation)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefresh.isRefreshing = true
        entryList = mutableListOf()
        entryIdList = mutableListOf()
        MainScope().launch {
            if (binding.adView.visibility == View.VISIBLE) {
                if (PremiumStatus.isPremium(requireContext())) {
                    processPremium()
                }
            }

            viewModel.getEntries(entryList, entryIdList, 0, binding.root)
            while (viewModel.isLoading.value) {
                continue
            }

            if (entryIdList.size > 0) {
                binding.progressBar.visibility = View.GONE
                if (binding.rvBabyTimeline.adapter == null) {
                    binding.rvBabyTimeline.apply {
                        layoutManager = LinearLayoutManager(context)
                        setHasFixedSize(false)
                        adapter = EntryAdapter(entryList)
                        visibility = View.VISIBLE
                    }
                    binding.llNoBaby.visibility = View.GONE
                    binding.llNoEntry.visibility = View.GONE
                } else {
                    binding.rvBabyTimeline.adapter =
                        EntryAdapter(entryList)

                    for (e in entryList) {
                        Log.e("TAG", "onResume: ${1}")
                    }
                }
            } else {
                binding.rvBabyTimeline.visibility = View.GONE
                binding.llNoBaby.visibility = View.GONE
                binding.llNoEntry.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }

            binding.nestedSv.scrollY = 0
            binding.nestedSv.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
                if (scrollY - oldScrollY > 20) {
                    binding.fab.hide()
                } else if (scrollY - oldScrollY < -20) {
                    binding.fab.show()
                }

                if (scrollY == ((v.getChildAt(0).measuredHeight - v.measuredHeight))) {
                    binding.swipeRefresh.isRefreshing = true
                    MainScope().launch {
                        val lastItem =
                            ((binding.rvBabyTimeline.layoutManager) as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                        val addedList =
                            viewModel.getEntries(entryList, entryIdList, lastItem, binding.root)
                        delay(800)

                        (binding.rvBabyTimeline.adapter as EntryAdapter).notifyItemRangeInserted(
                            entryList.size - 1,
                            addedList.size
                        )

                        binding.swipeRefresh.isRefreshing = false

                    }
                }

            })


            val baby = viewModel.baby
            if (!isBabyAlreadySet) {
                if (baby != null) {
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                    binding.rvBabyTimeline.visibility = View.GONE
                    binding.llNoBaby.visibility = View.VISIBLE
                    binding.llNoEntry.visibility = View.GONE
                }
            }

            binding.swipeRefresh.isRefreshing = false
        }

        premiumViewModel.checkPurchases()
    }

    private fun processPremium() {
        binding.adView.visibility = View.GONE
        (binding.fab.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        (binding.fab.layoutParams as RelativeLayout.LayoutParams).removeRule(RelativeLayout.ABOVE)
        binding.root.removeView(binding.adView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpFABs(clickable: Boolean) {
        binding.fabAddFeeding.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), AddFeedingActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.fabAddMeasure.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), AddMeasureActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.fabAddSleep.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), AddSleepActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.fabAddHealth.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), AddHealthActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.fabAddDiaper.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), AddDiaperActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.fabAddActivity.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), AddEventActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        binding.fabAddPicture.setOnClickListener {
            if (clickable) {
                MainScope().launch {
                    binding.fab.performClick()
                    val intent = Intent(requireContext(), PictureActivity::class.java)
                    intent.putExtra(Photo.EXTRA_HAS_PHOTO, false)
                    startActivity(intent)
                }
            }
        }
        binding.chipFeeding.setOnClickListener {
            binding.fabAddFeeding.performClick()
        }
        binding.chipActivity.setOnClickListener {
            binding.fabAddActivity.performClick()
        }
        binding.chipDiaper.setOnClickListener {
            binding.fabAddDiaper.performClick()
        }
        binding.chipHealth.setOnClickListener {
            binding.fabAddHealth.performClick()
        }
        binding.chipMeasure.setOnClickListener {
            binding.fabAddMeasure.performClick()
        }
        binding.chipSleep.setOnClickListener {
            binding.fabAddSleep.performClick()
        }
        binding.chipPicture.setOnClickListener {
            binding.fabAddPicture.performClick()
        }
        binding.fab.visibility = View.VISIBLE
    }

}