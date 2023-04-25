package com.kylecorry.aurora_alert.infrastructure.space_weather

import com.kylecorry.andromeda.core.coroutines.onIO
import com.kylecorry.aurora.NOAASpaceWeatherProxy
import com.kylecorry.aurora.notifications.SpaceWeatherAlert
import com.kylecorry.aurora.notifications.SpaceWeatherWarning
import com.kylecorry.aurora.notifications.SpaceWeatherWatch
import com.kylecorry.aurora_alert.domain.Notification
import com.kylecorry.aurora_alert.domain.NotificationLevel
import com.kylecorry.sol.units.Reading
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpaceWeatherService @Inject constructor() {

    private val proxy = NOAASpaceWeatherProxy()

    suspend fun getForecasts(): List<Reading<Float>> = onIO {
        val forecast = proxy.get3DayForecast()
        // TODO: Save in DB
        forecast
            .flatMap { it.kp }
            .map {
                Reading(
                    it.kp,
                    it.start.plus(Duration.between(it.start, it.end).dividedBy(2)).toInstant()
                )
            }
            .filter { it.time >= Instant.now() }
            .sortedBy { it.time }
    }

    suspend fun getNotifications(): List<Notification> =
        onIO {
            val alerts = proxy.getNotifications(true)
            alerts.map {
                Notification(
                    if (it is SpaceWeatherWarning) it.originalSerialNumber else it.serialNumber,
                    it.title,
                    it.message,
                    when (it) {
                        is SpaceWeatherWarning -> NotificationLevel.Warning
                        is SpaceWeatherAlert -> NotificationLevel.Alert
                        is SpaceWeatherWatch -> NotificationLevel.Watch
                        else -> NotificationLevel.Summary
                    },
                    it.messageCode,
                    it.issueTime.toInstant(),
                    if (it is SpaceWeatherWarning) it.validFrom.toInstant() else it.issueTime.toInstant(),
                    if (it is SpaceWeatherWarning) it.validTo.toInstant() else it.issueTime.toInstant(),
                    if (it is SpaceWeatherWarning) it.cancellationOf != null else false,
                    if (it is SpaceWeatherWarning) it.extensionOf != null else false
                )
            }
        }
}