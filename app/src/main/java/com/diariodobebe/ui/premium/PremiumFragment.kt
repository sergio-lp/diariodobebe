package com.diariodobebe.ui.premium

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentPremiumBinding
import com.diariodobebe.helpers.SendMail
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PremiumFragment : Fragment() {

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            binding.btnPremium.visibility = View.VISIBLE
            binding.progressbar.visibility = View.GONE
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (!purchases.isNullOrEmpty()) {
                    purchases.forEach { purchase ->
                        Log.e("TAG", purchase.orderId)
                        Snackbar.make(
                            binding.root,
                            getString(R.string.successful_purchase),
                            Snackbar.LENGTH_LONG
                        ).show()

                        //TODO: Process purchase

                        binding.tvBePremium.text = "You are Premium!"
                        binding.tvPremiumDesc.text = "Thank you a lot for supporting the app!"
                        binding.tvPremiumAdvantages.visibility = View.INVISIBLE
                        binding.btnPremium.visibility = View.INVISIBLE
                    }
                }
            }
        }

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val premiumViewModel =
            ViewModelProvider(this)[PremiumViewModel::class.java]
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnPremium.visibility = View.GONE
        binding.progressbar.visibility = View.VISIBLE

        val billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Snackbar.make(
                    binding.root,
                    getString(R.string.billing_api_error),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.send_email)) {
                        SendMail.SendMail.sendSupportMail(requireContext())
                    }.show()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val skuList = ArrayList<String>()
                    skuList.add("android.test.purchased")
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

                    lifecycleScope.launch {
                        val skuDetailsResult = withContext(Dispatchers.IO) {
                            billingClient.querySkuDetails(params.build())
                        }

                        if (skuDetailsResult.billingResult.responseCode != BillingClient.BillingResponseCode.OK
                        ) {
                            Snackbar.make(
                                binding.root,
                                getString(R.string.billing_api_error),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(getString(R.string.send_email)) {
                                    SendMail.SendMail.sendSupportMail(requireContext())
                                }.show()
                        } else {
                            binding.btnPremium.setOnClickListener {
                                binding.btnPremium.visibility = View.GONE
                                binding.progressbar.visibility = View.VISIBLE

                                val flowParams = BillingFlowParams.newBuilder()
                                    .setSkuDetails(
                                        skuDetailsResult.skuDetailsList?.get(0)
                                            ?: SkuDetails("android.test.purchased")
                                    )
                                    .build()
                                val responseCode = billingClient.launchBillingFlow(
                                    requireActivity(),
                                    flowParams
                                ).responseCode
                            }

                            binding.btnPremium.visibility = View.VISIBLE
                            binding.progressbar.visibility = View.GONE
                        }
                    }

                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.billing_api_error),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.send_email)) {
                            SendMail.SendMail.sendSupportMail(requireContext())
                        }.show()
                }
            }

        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}