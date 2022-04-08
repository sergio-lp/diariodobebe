package com.diariodobebe.ui.blog

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.diariodobebe.R
import com.diariodobebe.databinding.BlogFragmentBinding
import com.diariodobebe.databinding.FragmentHomeBinding
import com.diariodobebe.ui.home.HomeViewModel

class BlogFragment : Fragment() {

    private lateinit var viewModel: BlogViewModel
    private var _binding: BlogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[BlogViewModel::class.java]

        _binding = BlogFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        lifecycleScope.launchWhenResumed {
            binding.webView.loadUrl(getString(R.string.blog_url))
        }

        return root
    }


}