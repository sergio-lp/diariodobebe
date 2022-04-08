package com.diariodobebe.ui.home

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diariodobebe.R
import com.diariodobebe.adapters.EntryAdapter
import com.diariodobebe.databinding.FragmentHomeBinding
import com.diariodobebe.models.Baby
import com.diariodobebe.models.Entry
import com.diariodobebe.ui.add_baby_activity.AddBabyActivity
import com.diariodobebe.ui.entry_activities.feeding_activity.AddFeedingActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val entryList: MutableList<Entry> = mutableListOf()
    private val entryIdList: MutableList<Int> = mutableListOf()
    private lateinit var viewModel: HomeViewModel
    private var isFabExpanded = false
    private var isBabyAlreadySet = false

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

        binding.btnAddBaby.setOnClickListener {
            startActivity(Intent(activity, AddBabyActivity::class.java))
        }

        binding.fab.setOnClickListener {
            this.isFabExpanded = !isFabExpanded
            toggleFab()
        }

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

        binding.fabExpandedLayout.visibility = visibility

        binding.fabExpandedLayout.startAnimation(animationChild)
        (binding.fab.icon as AnimatedVectorDrawable).start()

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
        MainScope().launch {
            viewModel.getEntries(entryList, entryIdList)
            while (viewModel.isLoading.value) {
                continue
            }

            if (entryIdList.size > 0) {
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
                    binding.rvBabyTimeline.adapter!!.notifyDataSetChanged()
                }
            } else {
                binding.rvBabyTimeline.visibility = View.GONE
                binding.llNoBaby.visibility = View.GONE
                binding.llNoEntry.visibility = View.VISIBLE
            }

            val baby = viewModel.baby
            if (!isBabyAlreadySet) {
                if (baby != null) {
                    setUpFABs(baby)
                } else {
                    binding.fab.visibility = View.GONE
                    binding.rvBabyTimeline.visibility = View.GONE
                    binding.llNoBaby.visibility = View.VISIBLE
                    binding.llNoEntry.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpFABs(baby: Baby) {
        binding.fabAddFeeding.setOnClickListener {
            binding.fab.performClick()
            val intent = Intent(requireContext(), AddFeedingActivity::class.java)
            intent.putExtra(EXTRAS.EXTRA_BABY, baby)
            startActivity(intent)
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
        binding.fab.visibility = View.VISIBLE
    }

    object EXTRAS {
        const val EXTRA_BABY = "baby"
    }
}