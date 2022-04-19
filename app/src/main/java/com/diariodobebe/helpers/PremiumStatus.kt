package com.diariodobebe.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import com.diariodobebe.R
import com.diariodobebe.ui.main_activity.EmptyActivity
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetDialog

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

        @SuppressLint("InflateParams")
        fun showPremiumOffer(
            inflater: LayoutInflater,
            title: String = inflater.context.getString(R.string.unlock_everything)
        ) {
            val view = inflater.inflate(R.layout.view_premium_offer, null, false)
            view.findViewById<TextView>(R.id.tv_offer_title).text = title
            view.findViewById<TextView>(R.id.tv_offer_description).text = inflater.context.getString(R.string.premium_only)
            val dialog = BottomSheetDialog(inflater.context)
            dialog.setContentView(view)

            val window = dialog.window ?: run {
                Toast.makeText(
                    inflater.context,
                    inflater.context.getString(R.string.premium_only),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val params = window.attributes
            params.gravity = Gravity.BOTTOM
            params.verticalMargin = 0f
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.attributes = params

            view.findViewById<ImageView>(R.id.btn_dismiss_offer).setOnClickListener {
                dialog.dismiss()
            }

            view.findViewById<Button>(R.id.btn_accept_offer).setOnClickListener {
                val intent = Intent(inflater.context, EmptyActivity::class.java)
                intent.putExtra(EmptyActivity.FRAGMENT, EmptyActivity.PREMIUM)
                inflater.context.startActivity(intent)
            }

            dialog.show()
        }
    }
}