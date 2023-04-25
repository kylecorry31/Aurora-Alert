package com.kylecorry.aurora_alert.app

import android.content.Context
import com.kylecorry.aurora_alert.infrastructure.services.AlertWorker

object Startup {

    fun restartServices(context: Context) {
        AlertWorker.scheduler(context).once()
    }

}