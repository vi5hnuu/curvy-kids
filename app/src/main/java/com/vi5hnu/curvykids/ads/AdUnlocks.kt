package com.vi5hnu.curvykids.ads

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vi5hnu.curvykids.billing.Lockable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

private val Context.adUnlockStore by preferencesDataStore("curvy_ad_unlocks")

/**
 * What a watched video opened, for the rest of the day: activities, Barakhadi letters, stories,
 * by [Lockable.key]. The record carries its date, so on a new day it reads as empty and the next
 * opening starts it over. The date is looked at once a minute, so midnight locks things again
 * even while the app stays open (a timer to midnight could oversleep a device's deep sleep).
 *
 * @param today the current date; replaceable for checking the day change.
 */
class AdUnlocks(context: Context, private val today: () -> LocalDate = LocalDate::now) {

    private val store = context.applicationContext.adUnlockStore

    /** The date, as it changes. */
    private val days: Flow<String> = flow {
        while (true) {
            emit(today().toString())
            delay(DAY_CHECK_MS)
        }
    }.distinctUntilChanged()

    /** Keys opened today. */
    val openedToday: Flow<Set<String>> = combine(store.data, days) { prefs, day ->
        if (prefs[DAY] == day) prefs[KEYS].orEmpty() else emptySet()
    }.distinctUntilChanged()

    /** Opens [item] until the end of today. */
    suspend fun open(item: Lockable) {
        store.edit { prefs ->
            val day = today().toString()
            val keys = if (prefs[DAY] == day) prefs[KEYS].orEmpty() else emptySet()
            prefs[DAY] = day
            prefs[KEYS] = keys + item.key
        }
    }

    private companion object {
        val DAY = stringPreferencesKey("day")
        val KEYS = stringSetPreferencesKey("keys")
        const val DAY_CHECK_MS = 60_000L
    }
}
