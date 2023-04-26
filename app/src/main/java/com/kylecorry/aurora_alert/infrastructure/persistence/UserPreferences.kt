package com.kylecorry.aurora_alert.infrastructure.persistence

import android.content.Context
import com.kylecorry.andromeda.preferences.IPreferences
import com.kylecorry.aurora_alert.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val prefs: IPreferences,
    @ApplicationContext private val context: Context
) {

    fun isNotificationBlocked(code: String): Boolean {
        // Don't notify for summaries
        if (code.matches("SUM.+".toRegex())) {
            return true
        }

        if (code.matches("WARK\\d+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_kp_warnings)) == false
        }

        if (code.matches("ALTK\\d+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_kp_alerts)) == false
        }

        if (code.matches("WAREF.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_ef_warnings)) == false
        }

        if (code.matches("ALTEF.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_ef_alerts)) == false
        }

        if (code.matches("WARP.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_px_warnings)) == false
        }

        if (code.matches("ALTP.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_px_alerts)) == false
        }

        if (code.matches("WARTP.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_tp_warnings)) == false
        }

        if (code.matches("ALTTP.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_tp_alerts)) == false
        }

        if (code.matches("WARXM.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_xm_warnings)) == false
        }

        if (code.matches("ALTXM.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_xm_alerts)) == false
        }

        if (code == "WARSUD") {
            return prefs.getBoolean(context.getString(R.string.pref_key_sud_warnings)) == false
        }

        if (code.matches("WATA.+".toRegex())) {
            return prefs.getBoolean(context.getString(R.string.pref_key_storm_watch)) == false
        }

        return false
    }

}