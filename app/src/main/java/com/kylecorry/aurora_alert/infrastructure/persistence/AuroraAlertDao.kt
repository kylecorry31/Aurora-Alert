package com.kylecorry.aurora_alert.infrastructure.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.Instant

@Dao
interface AuroraAlertDao {
    @Query("SELECT * FROM aurora_alert")
    fun getAllLive(): LiveData<List<AuroraAlert>>

    @Query("SELECT * FROM aurora_alert")
    suspend fun getAll(): List<AuroraAlert>

    @Query("SELECT * FROM aurora_alert WHERE _id = :id LIMIT 1")
    suspend fun get(id: Long): AuroraAlert?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AuroraAlert): Long

    @Query("DELETE FROM aurora_alert WHERE sent_at < :before")
    suspend fun deleteOld(before: Instant)

    @Delete
    suspend fun delete(alert: AuroraAlert)

    @Update
    suspend fun update(alert: AuroraAlert)
}