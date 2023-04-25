package com.kylecorry.aurora_alert.ui

import android.widget.ImageButton
import com.kylecorry.andromeda.core.system.Resources
import com.kylecorry.andromeda.core.ui.setState
import com.kylecorry.aurora_alert.R

object UiUtils {
    fun setButtonState(button: ImageButton, state: Boolean) {
        button.setState(
            state,
            Resources.getAndroidColorAttr(button.context, R.attr.colorPrimary),
            Resources.color(button.context, R.color.colorSecondary)
        )
    }
}