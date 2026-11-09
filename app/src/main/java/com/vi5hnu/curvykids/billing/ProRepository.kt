package com.vi5hnu.curvykids.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

/**
 * Sells and remembers CurvyKids Pro. Screens depend on this interface only, so the Play-backed
 * store ([PlayProRepository]) and the debug stand-in ([FakeProRepository]) are interchangeable,
 * and a server check could be added behind it later without touching a screen.
 */
interface ProRepository {
    /** Whether Pro is unlocked, and what the store is offering; updates as things change. */
    val state: StateFlow<ProState>

    /** Asks Play again for the offers and for what this user owns (also restores a purchase). */
    fun refresh()

    /** Opens Play's purchase sheet for [offer]. The result arrives through [state]. */
    fun buy(activity: Activity, offer: ProOffer)

    /** Clears [ProState.failure] once the screen has shown it. */
    fun clearFailure()
}
