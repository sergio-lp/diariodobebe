package com.diariodobebe.ui.main_activity.premium

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.android.billingclient.api.*
import com.diariodobebe.R

class PremiumViewModel(val app: Application) : AndroidViewModel(app) {
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            processPurchase(billingResult, purchases)
        }

    private var billingClient: BillingClient = BillingClient.newBuilder(app)
        .enablePendingPurchases()
        .setListener(purchasesUpdatedListener)
        .build()

    init {
        startBillingConnection(billingClient)
    }

    fun checkPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { result, purchaseList ->
            processPurchase(result, purchaseList)
        }
    }

    private fun processPurchase(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            if (!purchases.isNullOrEmpty()) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        Toast.makeText(
                            app,
                            app.getString(R.string.successful_purchase),
                            Toast.LENGTH_LONG
                        ).show()

                        app.getSharedPreferences(
                            app.getString(R.string.PREFS),
                            Context.MODE_PRIVATE
                        ).edit {
                            putBoolean(app.getString(R.string.PREMIUM), true)
                        }
                    }
                }
            }
        }
    }

    private fun startBillingConnection(billingClient: BillingClient) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Toast.makeText(
                    app,
                    app.getString(R.string.billing_api_error),
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
            }


        })

    }

}
