package com.diariodobebe.helpers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import com.diariodobebe.R
import com.google.android.gms.ads.AdView

class PremiumStatus {

    companion object {
        fun isPremium(ctx: Context): Boolean {
            return ctx.getSharedPreferences(ctx.getString(R.string.PREFS), Context.MODE_PRIVATE)
                .getBoolean(ctx.getString(R.string.PREMIUM), false)
        }

        fun setPremium(ctx: Context) {
            ctx.getSharedPreferences(ctx.getString(R.string.PREFS), Context.MODE_PRIVATE)
                .edit {
                    putBoolean(ctx.getString(R.string.PREMIUM), true)
                    apply()
                }
        }

        fun processPremium(adView: AdView, parent: ViewGroup) {
            adView.visibility = View.GONE
            parent.removeView(adView)
        }
    }
}