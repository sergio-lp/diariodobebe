package com.diariodobebe.ui.entry_activities.health_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diariodobebe.databinding.FragmentMedicationBinding

class MedicationFragment : Fragment() {
    private var _binding: FragmentMedicationBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMedicationBinding.inflate(layoutInflater)

        return binding.root
    }
}