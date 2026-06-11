package com.vi5hnu.curvykids.data.app

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("curvy_app")

/**
 * Persists global app state — stars, streak, mastered items, badges, lastTopic.
 * Mastered items are stored as strings like "upper:A", "lower:a", "numbers:5".
 */
class AppRepository(private val context: Context) {

    private val STARS             = intPreferencesKey("stars")
    private val STREAK            = intPreferencesKey("streak")
    private val MASTERED          = stringSetPreferencesKey("mastered_v2")
    private val BADGES            = stringSetPreferencesKey("badges")
    private val LAST_TOPIC        = stringPreferencesKey("last_topic")
    private val SOUND_EFFECTS     = booleanPreferencesKey("sound_effects")
    private val BACKGROUND_MUSIC  = booleanPreferencesKey("background_music")
    private val PLAY_REMINDER     = booleanPreferencesKey("play_reminder")

    val starsFlow: Flow<Int> = context.dataStore.data.map { it[STARS] ?: 0 }
    val streakFlow: Flow<Int> = context.dataStore.data.map { it[STREAK] ?: 1 }
    val masteredFlow: Flow<List<String>> = context.dataStore.data.map {
        it[MASTERED]?.toList() ?: emptyList()
    }
    val badgesFlow: Flow<List<String>> = context.dataStore.data.map {
        it[BADGES]?.toList() ?: emptyList()
    }
    val lastTopicFlow: Flow<String?> = context.dataStore.data.map { it[LAST_TOPIC] }
    val soundEffectsFlow: Flow<Boolean> = context.dataStore.data.map { it[SOUND_EFFECTS] ?: true }
    val backgroundMusicFlow: Flow<Boolean> = context.dataStore.data.map { it[BACKGROUND_MUSIC] ?: false }
    val playReminderFlow: Flow<Boolean> = context.dataStore.data.map { it[PLAY_REMINDER] ?: true }

    suspend fun addStars(amount: Int) {
        context.dataStore.edit { prefs ->
            prefs[STARS] = (prefs[STARS] ?: 0) + amount
        }
    }

    suspend fun markMastered(key: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[MASTERED]?.toMutableSet() ?: mutableSetOf()
            set.add(key)
            prefs[MASTERED] = set
        }
    }

    suspend fun addBadge(id: String) {
        context.dataStore.edit { prefs ->
            val set = prefs[BADGES]?.toMutableSet() ?: mutableSetOf()
            set.add(id)
            prefs[BADGES] = set
        }
    }

    suspend fun setLastTopic(id: String) {
        context.dataStore.edit { it[LAST_TOPIC] = id }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_EFFECTS] = enabled }
    }

    suspend fun setBackgroundMusic(enabled: Boolean) {
        context.dataStore.edit { it[BACKGROUND_MUSIC] = enabled }
    }

    suspend fun setPlayReminder(enabled: Boolean) {
        context.dataStore.edit { it[PLAY_REMINDER] = enabled }
    }
}
