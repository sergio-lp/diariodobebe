package com.diariodobebe.ui.main_activity.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.diariodobebe.R
import com.diariodobebe.databinding.BlogFragmentBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class BlogFragment : Fragment() {

    private var _binding: BlogFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BlogFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        MainScope().launch {
            binding.webView.loadUrl(getString(R.string.blog_url))
        }

        return root
    }


}