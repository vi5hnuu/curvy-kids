package com.vi5hnu.curvykids.ads

import android.content.Context
import android.os.SystemClock
import android.provider.Settings
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vi5hnu.curvykids.billing.Lockable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

private val Context.adUnlockStore by preferencesDataStore("curvy_ad_unlocks")

/**
 * What a watched video opened, for the rest of the day: activities, Barakhadi letters, stories,
 * by [Lockable.key]. The record carries its date, so on a new day it reads as empty and the next
 * opening starts it over. The date is looked at once a minute, so midnight locks things again
 * even while the app stays open (a timer to midnight could oversleep a device's deep sleep).
 *
 * Setting the phone's clock back doesn't bring a day's openings back:
 * - Since the phone last started, time is measured from the first opening with the clock that
 *   counts since boot ([SystemClock.elapsedRealtime]), which the date setting doesn't move, so
 *   the real date is known whatever the clock says.
 * - After a restart that clock starts over, so the wall clock is used, but only if it isn't
 *   behind the latest time this app has seen (a clock set back reads as "not today").
 */
class AdUnlocks(context: Context) {

    private val app = context.applicationContext
    private val store = app.adUnlockStore

    /** Ticks once a minute, so the day is checked again even if nothing else changes. */
    private val ticks: Flow<Long> = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(DAY_CHECK_MS)
        }
    }

    /** Keys opened today. */
    val openedToday: Flow<Set<String>> = combine(store.data, ticks) { prefs, _ ->
        if (isToday(prefs)) prefs[KEYS].orEmpty() else emptySet()
    }.distinctUntilChanged()

    /** Opens [item] until the end of today. */
    suspend fun open(item: Lockable) {
        store.edit { prefs ->
            val now = System.currentTimeMillis()
            if (!isToday(prefs)) {
                // A new day's first opening: start the record and anchor it to the boot clock.
                prefs[DAY] = dateOf(now)
                prefs[KEYS] = emptySet()
                prefs[OPENED_AT] = now
                prefs[OPENED_AT_ELAPSED] = SystemClock.elapsedRealtime()
                prefs[BOOT] = bootCount()
            }
            prefs[KEYS] = prefs[KEYS].orEmpty() + item.key
            prefs[LAST_SEEN] = maxOf(prefs[LAST_SEEN] ?: 0L, now)
        }
    }

    /** Whether the record in [prefs] is for the real today (see the class notes). */
    private fun isToday(prefs: Preferences): Boolean {
        val day = prefs[DAY] ?: return false
        val openedAt = prefs[OPENED_AT] ?: return false
        val sameBoot = prefs[BOOT] == bootCount() && prefs[OPENED_AT_ELAPSED] != null
        val now = if (sameBoot) {
            // Real time now: the opening's time plus how long has really passed since.
            openedAt + (SystemClock.elapsedRealtime() - prefs[OPENED_AT_ELAPSED]!!)
        } else {
            System.currentTimeMillis().also { wall ->
                if (wall < (prefs[LAST_SEEN] ?: 0L) - CLOCK_SLACK_MS) return false
            }
        }
        return now >= openedAt && dateOf(now) == day
    }

    /** Times the phone has started; the same value means the boot clock hasn't restarted. */
    private fun bootCount(): Int = Settings.Global.getInt(app.contentResolver, Settings.Global.BOOT_COUNT, -1)

    private fun dateOf(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate().toString() // API 26+

    private companion object {
        val DAY = stringPreferencesKey("day")
        val KEYS = stringSetPreferencesKey("keys")
        /** Wall-clock time of the day's first opening. */
        val OPENED_AT = longPreferencesKey("opened_at")
        /** Boot-clock time of the same moment. */
        val OPENED_AT_ELAPSED = longPreferencesKey("opened_at_elapsed")
        val BOOT = intPreferencesKey("boot")
        /** The latest wall-clock time seen, to spot a clock set back after a restart. */
        val LAST_SEEN = longPreferencesKey("last_seen")
        const val DAY_CHECK_MS = 60_000L
        /** Clocks get corrected by a few minutes now and then; that isn't a trick. */
        val CLOCK_SLACK_MS = TimeUnit.MINUTES.toMillis(10)
    }
}
