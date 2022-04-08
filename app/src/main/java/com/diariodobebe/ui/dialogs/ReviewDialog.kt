package com.diariodobebe.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.diariodobebe.R
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager


class ReviewDialog() : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launchWhenResumed {
            openReview()
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun openReview() {
        //TODO: Remove fake test
        val manager = ReviewManagerFactory.create(requireContext())
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener {
            if (it.isSuccessful) {
                manager.launchReviewFlow(requireActivity(), it.result).addOnCompleteListener { result ->
                    if (!result.isSuccessful) {
                        Toast.makeText(
                            requireContext(), getString(R.string.rating_api_error),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            } else {
                Toast.makeText(
                    requireContext(), getString(R.string.rating_api_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}