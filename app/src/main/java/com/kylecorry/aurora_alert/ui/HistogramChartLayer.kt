package com.kylecorry.aurora_alert.ui

import androidx.annotation.ColorInt
import com.kylecorry.andromeda.canvas.ICanvasDrawer
import com.kylecorry.ceres.chart.IChart
import com.kylecorry.ceres.chart.data.BaseChartLayer
import com.kylecorry.sol.math.Vector2
import kotlin.math.abs

// TODO: Support clicks
// TODO: Don't cut off ends (maybe add a get bounds method to ChartLayer)
class HistogramChartLayer(
    initialData: List<Vector2>,
    @ColorInt initialColor: Int,
    val width: Float = 6f
) : BaseChartLayer(initialData, false) {

    @ColorInt
    var color: Int = initialColor
        set(value) {
            field = value
            invalidate()
        }

    override fun draw(drawer: ICanvasDrawer, chart: IChart) {
        drawer.noStroke()
        drawer.fill(color)
        for (point in data) {
            val topLeft = chart.toPixel(Vector2(point.x - width / 2, point.y))
            val bottomRight = chart.toPixel(Vector2(point.x + width / 2, 0f))
            drawer.rect(
                topLeft.x,
                topLeft.y,
                abs(bottomRight.x - topLeft.x),
                abs(bottomRight.y - topLeft.y)
            )
        }
        super.draw(drawer, chart)

        // Reset the opacity
        drawer.opacity(255)
    }
}