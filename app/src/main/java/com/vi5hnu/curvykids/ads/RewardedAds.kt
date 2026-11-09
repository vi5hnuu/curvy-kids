package com.vi5hnu.curvykids.ads

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * Short opt-in videos a free user can choose to watch to open a Pro item for the day. Never shown
 * unasked: the only way to one is the "Watch a video" button on the locked sheet.
 *
 * Screens depend on this interface only, so the ad network is replaceable (or absent) without
 * touching them.
 */
interface RewardedAds {
    /** A video is loaded and can play straight away; the button is shown only then. */
    val ready: StateFlow<Boolean>

    /** Begins keeping one video loaded (free users). Safe to call more than once. */
    fun start()

    /** Stops loading and drops any loaded video (Pro was bought). */
    fun stop()

    /**
     * Plays the loaded video over [activity]. [onReward] runs only if it was watched through;
     * closing it early gives nothing. Does nothing when no video is [ready].
     */
    fun show(activity: Activity, onReward: () -> Unit)
}
