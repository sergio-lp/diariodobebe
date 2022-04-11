package com.diariodobebe.helpers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.diariodobebe.R
import kotlin.random.Random

const val CHANNEL_ID = "diario_do_bebe_nc"
const val TITLE_EXTRA = "title_extra"
const val MESSAGE_EXTRA = "message_extra"

class Notification : BroadcastReceiver() {
    val notificationId = Random.nextInt()

    override fun onReceive(ctx: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(TITLE_EXTRA))
            .setContentText(intent.getStringExtra(MESSAGE_EXTRA))
            .build()

        val manager =
            ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}