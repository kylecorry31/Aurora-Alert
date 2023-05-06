package com.kylecorry.aurora_alert.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.kylecorry.andromeda.alerts.dialog
import com.kylecorry.andromeda.alerts.toast
import com.kylecorry.andromeda.core.coroutines.onMain
import com.kylecorry.andromeda.core.math.DecimalFormatter
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.fragments.BoundFragment
import com.kylecorry.andromeda.fragments.inBackground
import com.kylecorry.aurora_alert.databinding.FragmentMainBinding
import com.kylecorry.aurora_alert.infrastructure.space_weather.SpaceWeatherService
import com.kylecorry.ceres.chart.Chart
import com.kylecorry.ceres.chart.data.LineChartLayer
import com.kylecorry.ceres.chart.data.ScatterChartLayer
import com.kylecorry.sol.math.Vector2
import com.kylecorry.sol.time.Time
import com.kylecorry.sol.time.Time.toZonedDateTime
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

    private val histogramWidth = 2.75f

    private val highlightLayer = LineChartLayer(
        emptyList(),
        AppColor.Gray.color
    )

    private val g0Layer = HistogramChartLayer(
        emptyList(),
        AppColor.Green.color,
        histogramWidth,
        this::onBarClicked
    )

    private val g1Layer = HistogramChartLayer(
        emptyList(),
        Color.parseColor("#F6EB14"),
        histogramWidth,
        this::onBarClicked
    )

    private val g2Layer = HistogramChartLayer(
        emptyList(),
        Color.parseColor("#FFC800"),
        histogramWidth,
        this::onBarClicked
    )

    private val g3Layer = HistogramChartLayer(
        emptyList(),
        Color.parseColor("#FF9600"),
        histogramWidth,
        this::onBarClicked
    )

    private val g4Layer = HistogramChartLayer(
        emptyList(),
        Color.parseColor("#FF0000"),
        histogramWidth,
        this::onBarClicked
    )

    private val g5Layer = HistogramChartLayer(
        emptyList(),
        Color.parseColor("#C80000"),
        histogramWidth,
        this::onBarClicked
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kChart.configureYAxis(minimum = 0f, maximum = 10f)

        binding.kChart.plot(
            listOf(
                g0Layer,
                g1Layer,
                g2Layer,
                g3Layer,
                g4Layer,
                g5Layer,
                highlightLayer
            )
        )
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

        val readings = Chart.getDataFromReadings(forecasts, Instant.now()) { it }

        // Clear all layers
        g0Layer.data = emptyList()
        g1Layer.data = emptyList()
        g2Layer.data = emptyList()
        g3Layer.data = emptyList()
        g4Layer.data = emptyList()
        g5Layer.data = emptyList()

        for (reading in readings) {
            // Classify reading into a G level
            val layer = when {
                reading.y < 5 -> g0Layer
                reading.y < 6 -> g1Layer
                reading.y < 7 -> g2Layer
                reading.y < 8 -> g3Layer
                reading.y < 9 -> g4Layer
                else -> g5Layer
            }

            layer.data = layer.data + reading
        }
    }

    private fun onBarClicked(point: Vector2): Boolean {
        val time = Instant.now().plus(Time.hours(point.x.toDouble())).toZonedDateTime()
        val text = "Kp ${DecimalFormatter.format(point.y, 2)} at ${formatter.formatDateTime(time)}"
        binding.homeTitle.subtitle.isVisible = true
        binding.homeTitle.subtitle.text = text
        highlightLayer.data = listOf(
            Vector2(point.x, 0f),
            Vector2(point.x, 10f)
        )
        return true
    }
}