package com.vi5hnu.curvykids.data.progress

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vi5hnu.curvykids.data.content.Level
import kotlinx.coroutines.flow.first

/** Where the child currently is: which [level] and the index of the character within it. */
data class GameProgress(val level: Level, val index: Int)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "curvykids_progress")

/**
 * Persists game progress so the child resumes at the last character instead of restarting
 * at the beginning every launch. Backed by Jetpack DataStore.
 */
class ProgressRepository(context: Context) {

    private val store = context.applicationContext.dataStore

    /** Reads the saved progress, defaulting to the first uppercase character. */
    suspend fun load(): GameProgress {
        val prefs = store.data.first()
        val level = prefs[KEY_LEVEL]?.let { runCatching { Level.valueOf(it) }.getOrNull() }
            ?: Level.UPPERCASE
        val index = (prefs[indexKey(level)] ?: 0).coerceIn(0, level.characters.lastIndex)
        return GameProgress(level, index)
    }

    /** Reads the saved character index for a specific [level] (defaults to 0). */
    suspend fun loadIndex(level: Level): Int {
        val prefs = store.data.first()
        return (prefs[indexKey(level)] ?: 0).coerceIn(0, level.characters.lastIndex)
    }

    /** Saves the current [level] and per-level [index]. */
    suspend fun save(level: Level, index: Int) {
        store.edit { prefs ->
            prefs[KEY_LEVEL] = level.name
            prefs[indexKey(level)] = index
        }
    }

    private fun indexKey(level: Level) = intPreferencesKey("index_${level.name}")

    private companion object {
        val KEY_LEVEL = stringPreferencesKey("current_level")
    }
}
