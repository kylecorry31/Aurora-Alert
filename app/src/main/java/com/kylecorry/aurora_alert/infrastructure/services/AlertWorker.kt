package com.kylecorry.aurora_alert.infrastructure.services

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.kylecorry.andromeda.jobs.IOneTimeTaskScheduler
import com.kylecorry.andromeda.jobs.IntervalWorker
import com.kylecorry.andromeda.jobs.OneTimeTaskSchedulerFactory
import com.kylecorry.aurora_alert.infrastructure.persistence.AuroraAlertDao
import com.kylecorry.aurora_alert.infrastructure.space_weather.SpaceWeatherService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.Duration

@HiltWorker
class AlertWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val spaceWeatherService: SpaceWeatherService,
    private val alertDao: AuroraAlertDao
) : IntervalWorker(appContext, workerParams, wakelockDuration = Duration.ofSeconds(30)) {

    override fun getFrequency(context: Context): Duration {
        return Duration.ofHours(4)
    }

    override suspend fun execute(context: Context) {
        UpdateAlertsCommand(spaceWeatherService, alertDao, context).execute()
    }

    override val uniqueId: Int = UNIQUE_ID

    companion object {

        private const val UNIQUE_ID = 2739852

        fun scheduler(context: Context): IOneTimeTaskScheduler {
            return OneTimeTaskSchedulerFactory(context).deferrable(
                AlertWorker::class.java,
                UNIQUE_ID
            )
        }
    }

}