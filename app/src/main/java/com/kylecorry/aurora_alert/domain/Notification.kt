package com.kylecorry.aurora_alert.domain

import java.time.Instant

data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val level: NotificationLevel,
    val alertType: String,
    val issued: Instant,
    val effective: Instant,
    val expiration: Instant,
    val isCancellation: Boolean = false,
    val isExtension: Boolean = false
)

enum class NotificationLevel {
    Alert,
    Warning,
    Watch,
    Summary
}