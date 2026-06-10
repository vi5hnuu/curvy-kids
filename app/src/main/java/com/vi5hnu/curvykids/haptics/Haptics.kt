package com.vi5hnu.curvykids.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Thin wrapper around the platform vibrator. Replaces the `vibrate()` method that used to
 * live in the WebView JS bridge — now called directly from native game logic on a wrong
 * answer.
 */
class Haptics(context: Context) {

    private val appContext = context.applicationContext

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager =
                appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /** Single short buzz; [ms] controls duration. */
    fun vibrate(ms: Long = 200) {
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}
