package com.diariodobebe.ui.entry_activities.health_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentVitalsBinding
import com.diariodobebe.models.Health

class VitalsFragment : Fragment() {
    var mood: Int? = null
    private var _binding: FragmentVitalsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVitalsBinding.inflate(layoutInflater)
        val root = binding.root
        changeMood()

        binding.btnMoodGood.setOnClickListener {
            this.mood = Health.MOOD_GOOD
            changeMood()
        }

        binding.btnMoodNormal.setOnClickListener {
            this.mood = Health.MOOD_NORMAL
            changeMood()
        }

        binding.btnMoodBad.setOnClickListener {
            this.mood = Health.MOOD_BAD
            changeMood()
        }

        return root
    }

    private fun changeMood() {
        when (this.mood) {
            Health.MOOD_GOOD -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.good_mood).lowercase())
            }
            Health.MOOD_NORMAL -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.normal_mood).lowercase())
            }
            Health.MOOD_BAD -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.bad_mood).lowercase())
            }
            else -> {
                binding.tvHealthMood.text =
                    getString(R.string.mood_selector, getString(R.string.not_selected).lowercase())
            }
        }
    }
}