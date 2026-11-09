package com.vi5hnu.curvykids.ads

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vi5hnu.curvykids.billing.Lockable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.adUnlockStore by preferencesDataStore("curvy_ad_unlocks")

/**
 * What a watched video opened, for the rest of the day: activities, Barakhadi letters, stories,
 * by [Lockable.key]. The record carries its date, so on a new day it reads as empty and the next
 * opening starts it over. (A day that changes while the app stays open is noticed on the next
 * opening or app start.)
 *
 * @param today the current date; replaceable for checking the day change.
 */
class AdUnlocks(context: Context, private val today: () -> LocalDate = LocalDate::now) {

    private val store = context.applicationContext.adUnlockStore

    /** Keys opened today. */
    val openedToday: Flow<Set<String>> = store.data.map { prefs ->
        if (prefs[DAY] == today().toString()) prefs[KEYS].orEmpty() else emptySet()
    }

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
    }
}
