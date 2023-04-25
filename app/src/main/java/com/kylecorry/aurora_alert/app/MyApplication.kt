package com.kylecorry.aurora_alert.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kylecorry.andromeda.preferences.PreferenceMigration
import com.kylecorry.andromeda.preferences.PreferenceMigrator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.createChannels(this)
        migratePreferences()
    }

    private fun migratePreferences() {
        val key = "pref_version"
        val version = 0
        val migrations = listOf<PreferenceMigration>()
        PreferenceMigrator(this, key).migrate(version, migrations)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}