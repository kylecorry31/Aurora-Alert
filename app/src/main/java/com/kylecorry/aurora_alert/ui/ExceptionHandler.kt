package com.kylecorry.aurora_alert.ui

import android.util.Log
import com.kylecorry.andromeda.exceptions.AggregateBugReportGenerator
import com.kylecorry.andromeda.exceptions.AndroidDetailsBugReportGenerator
import com.kylecorry.andromeda.exceptions.AppDetailsBugReportGenerator
import com.kylecorry.andromeda.exceptions.BugReportEmailMessage
import com.kylecorry.andromeda.exceptions.DeviceDetailsBugReportGenerator
import com.kylecorry.andromeda.exceptions.EmailExceptionHandler
import com.kylecorry.andromeda.exceptions.StackTraceBugReportGenerator
import com.kylecorry.aurora_alert.R
import com.kylecorry.aurora_alert.infrastructure.errors.FragmentDetailsBugReportGenerator

object ExceptionHandler {

    fun initialize(activity: MainActivity) {
        val handler = EmailExceptionHandler(
            activity,
            AggregateBugReportGenerator(
                listOf(
                    AppDetailsBugReportGenerator(activity.getString(R.string.app_name)),
                    AndroidDetailsBugReportGenerator(),
                    DeviceDetailsBugReportGenerator(),
                    FragmentDetailsBugReportGenerator(),
                    StackTraceBugReportGenerator()
                )
            )
        ) { context, log ->
            Log.e(context.getString(R.string.app_name), log)
            BugReportEmailMessage(
                context.getString(R.string.error_occurred),
                context.getString(R.string.error_occurred_message),
                context.getString(R.string.email_developer),
                context.getString(android.R.string.cancel),
                context.getString(R.string.email),
                "Error in ${context.getString(R.string.app_name)}"
            )
        }
        handler.bind()
    }

}