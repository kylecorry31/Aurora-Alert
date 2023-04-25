package com.kylecorry.aurora_alert.ui

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.kylecorry.andromeda.alerts.Alerts
import com.kylecorry.aurora_alert.domain.Notification
import com.kylecorry.aurora_alert.domain.NotificationLevel
import com.kylecorry.ceres.list.ListItem
import com.kylecorry.ceres.list.ListItemMapper
import com.kylecorry.ceres.list.ListItemTag
import com.kylecorry.sol.time.Time.toZonedDateTime
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class NotificationListItemMapper @Inject constructor(
    @ActivityContext private val context: Context,
    private val formatService: FormatService
) :
    ListItemMapper<Notification> {
    override fun map(value: Notification): ListItem {
        return ListItem(
            value.id,
            value.title,
            formatTimeRange(value),
            tags = listOf(
                ListItemTag(value.level.name, null, getLevelColor(value.level))
            ),
        ) {
            Alerts.dialog(context, value.title, value.message)
        }
    }

    @ColorInt
    private fun getLevelColor(level: NotificationLevel): Int {
        return when (level) {
            NotificationLevel.Alert -> AppColor.Red.color
            NotificationLevel.Warning -> AppColor.Orange.color
            NotificationLevel.Watch -> AppColor.Yellow.color
            NotificationLevel.Summary -> AppColor.Green.color
        }
    }

    private fun formatTimeRange(notification: Notification): String {
        val start = notification.effective.toZonedDateTime()
        val end = notification.expiration.toZonedDateTime()

        if (start == end) {
            return formatService.formatDateTime(start, false, true)
        }
        return formatService.formatDateTime(
            start,
            false,
            true
        ) + " - " + formatService.formatDateTime(end, false, true)
    }
}