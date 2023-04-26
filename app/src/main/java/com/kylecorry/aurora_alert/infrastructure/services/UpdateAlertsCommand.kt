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
import com.kylecorry.aurora_alert.infrastructure.persistence.UserPreferences
import com.kylecorry.aurora_alert.infrastructure.space_weather.SpaceWeatherService
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class UpdateAlertsCommand @Inject constructor(
    private val spaceWeatherService: SpaceWeatherService,
    private val alertDao: AuroraAlertDao,
    private val preferences: UserPreferences,
    @ApplicationContext private val context: Context
) {
    suspend fun execute() {
        // Retrieve alerts
        val notifications = spaceWeatherService.getNotifications()

        // Filter to new alerts
        val alreadySent = alertDao.getAll()
        val newAlerts =
            notifications
                .filter { it.level == NotificationLevel.Warning || it.level == NotificationLevel.Alert }
                .filter { alreadySent.none { alert -> alert.serialNumber == it.id } }
                .filterNot { preferences.isNotificationBlocked(it.alertType) }

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