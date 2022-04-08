package com.diariodobebe.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.diariodobebe.R

class SendMail {

    object SendMail {
        public fun sendSupportMail(ctx: Context): Intent {
            val i = Intent(Intent.ACTION_SENDTO)
            i.data = Uri.parse("mailto:")
            i.putExtra(Intent.EXTRA_EMAIL, ctx.getString(R.string.support_email))
            i.putExtra(Intent.EXTRA_SUBJECT, ctx.getString(R.string.support_email_subject))

            return i
        }
    }
}