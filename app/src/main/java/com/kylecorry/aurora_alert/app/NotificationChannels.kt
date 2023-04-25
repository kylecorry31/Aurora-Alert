package com.kylecorry.aurora_alert.app

import android.content.Context
import com.kylecorry.andromeda.notify.Notify
import com.kylecorry.aurora_alert.R

object NotificationChannels {

    const val GROUP_ALERTS = "alerts"
    const val ALERT_CHANNEL_ID = "alerts"

    fun createChannels(context: Context) {
        // Create channels here
        Notify.createChannel(
            context,
            ALERT_CHANNEL_ID,
            context.getString(R.string.alerts),
            context.getString(R.string.alerts),
            Notify.CHANNEL_IMPORTANCE_HIGH
        )
    }

}