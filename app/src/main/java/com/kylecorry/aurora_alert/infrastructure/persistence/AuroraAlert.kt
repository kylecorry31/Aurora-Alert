package com.kylecorry.aurora_alert.infrastructure.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "aurora_alert"
)
data class AuroraAlert(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Long = 0,
    @ColumnInfo(name = "serial_number")
    val serialNumber: Long,
    @ColumnInfo(name = "sent_at")
    val sentAt: Instant,
)