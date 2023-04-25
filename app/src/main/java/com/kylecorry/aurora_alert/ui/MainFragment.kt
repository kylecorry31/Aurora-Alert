package com.kylecorry.aurora_alert.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kylecorry.andromeda.core.coroutines.onMain
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.aurora_alert.databinding.FragmentMainBinding
import com.kylecorry.aurora_alert.infrastructure.space_weather.SpaceWeatherService
import com.kylecorry.ceres.chart.Chart
import com.kylecorry.ceres.chart.data.LineChartLayer
import com.kylecorry.sol.units.Reading
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BoundFragment<FragmentMainBinding>() {

    @Inject
    lateinit var formatter: FormatService

    @Inject
    lateinit var spaceWeatherService: SpaceWeatherService

    @Inject
    lateinit var alertMapper: NotificationListItemMapper

    private val lineChartLayer = LineChartLayer(
        emptyList(),
        AppColor.Gray.color
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kChart.configureYAxis(minimum = 0f, maximum = 9f)
        binding.kChart.plot(listOf(lineChartLayer))
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadAuroraForecast()
        loadAuroraAlerts()
    }

    private fun loadAuroraAlerts() {
        inBackground {
            val alerts = spaceWeatherService.getNotifications()
            onMain {
                binding.alertList.setItems(alerts, alertMapper)
            }
        }
    }

    private fun loadAuroraForecast() {
        inBackground {
            val forecasts = spaceWeatherService.getForecasts()
            updateKpChart(forecasts)
        }
    }

    private fun updateKpChart(forecasts: List<Reading<Float>>) {
        lineChartLayer.data = Chart.getDataFromReadings(forecasts, Instant.now()) { it }
    }
}