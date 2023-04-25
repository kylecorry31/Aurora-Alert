package com.kylecorry.aurora_alert.infrastructure.services

import android.content.Context
import com.kylecorry.andromeda.notify.Notify
import com.kylecorry.aurora_alert.R
import com.kylecorry.aurora_alert.app.NavigationUtils
import com.kylecorry.aurora_alert.app.NotificationChannels
import com.kylecorry.aurora_alert.domain.Notification
import com.kylecorry.aurora_alert.domain.NotificationLevel
import com.kylecorry.aurora_alert.infrastructure.persistence.AuroraAlert
import com.kylecorry.aurora_alert.infrastructure.persistence.AuroraAlertDao
import com.kylecorry.aurora_alert.infrastructure.space_weather.SpaceWeatherService
import java.time.Duration
import java.time.Instant

class UpdateAlertsCommand(
    private val spaceWeatherService: SpaceWeatherService,
    private val alertDao: AuroraAlertDao,
    private val context: Context
) {
    suspend fun execute() {
        // Retrieve alerts
        val notifications = spaceWeatherService.getNotifications()

        // Filter to new alerts
        // TODO: Filter out alerts the user doesn't care about
        val alreadySent = alertDao.getAll()
        val newAlerts =
            notifications
                .filter { it.level == NotificationLevel.Warning || it.level == NotificationLevel.Alert }
                .filter { alreadySent.none { alert -> alert.serialNumber == it.id } }

        if (newAlerts.isNotEmpty()) {
            sendAlerts(newAlerts)
        }

        // Remove old alerts
        alertDao.deleteOld(Instant.now().minus(Duration.ofDays(4)))
    }

    private suspend fun sendAlerts(alerts: List<Notification>) {
        // Send notification for new alerts
        val notification = Notify.alert(
            context,
            NotificationChannels.ALERT_CHANNEL_ID,
            context.getString(R.string.space_weather_alert),
            alerts.joinToString("\n") { it.title },
            R.drawable.ic_info,
            group = NotificationChannels.GROUP_ALERTS,
            intent = NavigationUtils.pendingIntent(context, R.id.action_main),
            autoCancel = true
        )
        Notify.send(context, ALERTS_NOTIFICATION_ID, notification)

        // Record sent alerts
        for (alert in alerts) {
            alertDao.insert(AuroraAlert(0, alert.id, Instant.now()))
        }
    }

    companion object {
        private const val ALERTS_NOTIFICATION_ID = 74309823
    }
}