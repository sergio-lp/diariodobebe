package com.diariodobebe.ui.main_activity.premium

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.*
import com.diariodobebe.R
import com.diariodobebe.databinding.FragmentPremiumBinding
import com.diariodobebe.helpers.PremiumStatus
import com.diariodobebe.helpers.SendMail
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class PremiumFragment : Fragment() {
    private lateinit var billingClient: BillingClient

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            processPurchase(billingResult, purchases)
        }

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumBinding.inflate(layoutInflater)
        val root: View = binding.root

        binding.btnPremium.visibility = View.GONE
        binding.progressbar.visibility = View.VISIBLE

        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()

        return root
    }

    override fun onResume() {
        super.onResume()
        startBillingConnection(billingClient, true)
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { result, purchaseList ->
            processPurchase(result, purchaseList)
        }
    }

    private fun processPurchase(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        binding.btnPremium.visibility = View.VISIBLE
        binding.progressbar.visibility = View.GONE
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (!purchases.isNullOrEmpty()) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        Log.e("TAG", "processPurchase: 1")
                        Snackbar.make(
                            binding.root,
                            getString(R.string.successful_purchase),
                            Snackbar.LENGTH_LONG
                        ).show()

                        binding.tvBePremium.text = getString(R.string.you_are_premium)
                        binding.tvPremiumDesc.text = getString(R.string.premium_thanks)
                        binding.tvPremiumAdvantages.visibility = View.GONE
                        binding.btnPremium.visibility = View.GONE

                        PremiumStatus.setPremium(requireContext())

                        if (!purchase.isAcknowledged) {
                            val consumeParams = ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.purchaseToken).build()
                            MainScope().launch {
                                val acknowledgeResult = billingClient.acknowledgePurchase(
                                    AcknowledgePurchaseParams.newBuilder()
                                        .setPurchaseToken(purchase.purchaseToken).build()
                                )
                                if (acknowledgeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    billingClient.consumePurchase(consumeParams)
                                } else {
                                    Log.e("TAG", "processPurchase: 2")

                                    Snackbar.make(
                                        binding.root,
                                        getString(R.string.billing_api_error),
                                        Snackbar.LENGTH_LONG
                                    )
                                        .setAction(getString(R.string.send_email)) {
                                            SendMail.SendMail.sendSupportMail(requireContext())
                                        }.show()
                                    Log.e(
                                        "TAG",
                                        "Billing error: ${billingResult.debugMessage}: "
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startBillingConnection(billingClient: BillingClient, isResuming: Boolean) {
        lifecycleScope.launchWhenResumed {
            delay(600)
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    Log.e("TAG", "processPurchase: 3")
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
                                Log.e("TAG", "processPurchase: 4")

                                Snackbar.make(
                                    binding.root,
                                    getString(R.string.billing_api_error),
                                    Snackbar.LENGTH_LONG
                                )
                                    .setAction(getString(R.string.send_email)) {
                                        SendMail.SendMail.sendSupportMail(requireContext())
                                    }.show()

                                if (skuDetailsResult.billingResult.responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
                                    startBillingConnection(billingClient, false)
                                }
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

                                    if (responseCode != BillingClient.BillingResponseCode.OK) {
                                        Log.e("TAG", "processPurchase: 5")

                                        Snackbar.make(
                                            binding.root,
                                            getString(R.string.billing_api_error),
                                            Snackbar.LENGTH_LONG
                                        )
                                            .setAction(getString(R.string.send_email)) {
                                                SendMail.SendMail.sendSupportMail(requireContext())
                                            }.show()

                                        if (responseCode == BillingClient.BillingResponseCode.SERVICE_DISCONNECTED) {
                                            startBillingConnection(billingClient, false)
                                        }
                                    }
                                }

                                if (PremiumStatus.isPremium(requireContext())) {
                                    binding.tvBePremium.text = getString(R.string.you_are_premium)
                                    binding.tvPremiumDesc.text = getString(R.string.premium_thanks)
                                    binding.tvPremiumAdvantages.visibility = View.GONE
                                    binding.btnPremium.visibility = View.GONE
                                    binding.progressbar.visibility = View.GONE

                                } else {
                                    binding.btnPremium.visibility = View.VISIBLE
                                    binding.progressbar.visibility = View.GONE
                                }
                            }
                        }

                    } else {
                        if (isResuming) {
                            startBillingConnection(billingClient, false)
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
                }

            })
        }

    }

    override fun onPause() {
        super.onPause()
        billingClient.endConnection()
    }

}