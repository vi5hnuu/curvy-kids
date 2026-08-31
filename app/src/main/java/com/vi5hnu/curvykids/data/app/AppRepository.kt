package com.vi5hnu.curvykids.data.app

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("curvy_app")

/**
 * How forgiving the activities are.
 *
 * A three-year-old and a six-year-old use the same app; without this the quizzes are either
 * frustrating for the younger child or trivial for the older one.
 */
enum class Difficulty {
    /** Fewer choices per question and more attempts before moving on. */
    EASY,
    NORMAL;

    companion object {
        fun from(name: String?) = entries.firstOrNull { it.name == name } ?: NORMAL
    }
}

/** Everything the app persists about the child, read in one pass. */
data class AppRecord(
    val stars: Int = 0,
    val streak: Int = 1,
    val mastered: List<String> = emptyList(),
    val badges: List<String> = emptyList(),
    val lastTopicId: String? = null,
    val soundEffects: Boolean = true,
    val difficulty: Difficulty = Difficulty.NORMAL,
)

/**
 * Persists global app state — stars, daily streak, mastered items, badges, lastTopic.
 * Mastered items are stored as strings like "upper:A", "lower:a", "numbers:5".
 *
 * Exposes a single [record] flow rather than one flow per field: every field lives in the same
 * DataStore file, so separate flows meant one write woke six independent collectors that each
 * re-mapped the whole preference set.
 */
class AppRepository(private val context: Context) {

    val record: Flow<AppRecord> = context.dataStore.data.map { it.toRecord() }

    private fun Preferences.toRecord() = AppRecord(
        stars = this[STARS] ?: 0,
        streak = this[STREAK] ?: 1,
        mastered = this[MASTERED]?.toList() ?: emptyList(),
        badges = this[BADGES]?.toList() ?: emptyList(),
        lastTopicId = this[LAST_TOPIC],
        soundEffects = this[SOUND_EFFECTS] ?: true,
        difficulty = Difficulty.from(this[DIFFICULTY]),
    )

    suspend fun addStars(amount: Int) {
        context.dataStore.edit { prefs ->
            prefs[STARS] = (prefs[STARS] ?: 0) + amount
        }
    }

    /**
     * Records that the child was active on [today] (a local epoch day) and rolls the streak
     * forward. Safe to call on every answer — repeat calls within the same day are no-ops.
     */
    suspend fun recordActivity(today: Long) {
        context.dataStore.edit { prefs ->
            val lastDay = prefs[LAST_ACTIVE_DAY]
            if (lastDay == today) return@edit
            prefs[STREAK] = nextStreak(prefs[STREAK] ?: 1, lastDay, today)
            prefs[LAST_ACTIVE_DAY] = today
        }
    }

    suspend fun markMastered(key: String) {
        context.dataStore.edit { prefs ->
            prefs[MASTERED] = (prefs[MASTERED] ?: emptySet()) + key
        }
    }

    suspend fun addBadge(id: String) {
        context.dataStore.edit { prefs ->
            prefs[BADGES] = (prefs[BADGES] ?: emptySet()) + id
        }
    }

    suspend fun setLastTopic(id: String) {
        context.dataStore.edit { it[LAST_TOPIC] = id }
    }

    suspend fun setSoundEffects(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_EFFECTS] = enabled }
    }

    suspend fun setDifficulty(difficulty: Difficulty) {
        context.dataStore.edit { it[DIFFICULTY] = difficulty.name }
    }

    /** Wipes all progress. Used by the parent zone's "Reset progress" action. */
    suspend fun resetProgress() {
        context.dataStore.edit { prefs ->
            prefs.remove(STARS)
            prefs.remove(STREAK)
            prefs.remove(MASTERED)
            prefs.remove(BADGES)
            prefs.remove(LAST_TOPIC)
            prefs.remove(LAST_ACTIVE_DAY)
            // Sound and difficulty are parent settings, not child progress — deliberately kept.
        }
    }

    companion object {
        private val STARS = intPreferencesKey("stars")
        private val STREAK = intPreferencesKey("streak")
        private val MASTERED = stringSetPreferencesKey("mastered_v2")
        private val BADGES = stringSetPreferencesKey("badges")
        private val LAST_TOPIC = stringPreferencesKey("last_topic")
        private val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        private val LAST_ACTIVE_DAY = longPreferencesKey("last_active_day")
        private val DIFFICULTY = stringPreferencesKey("difficulty")

        /**
         * The streak value for a child active on [today], given they were last active on
         * [lastDay]. Pure so the calendar rules are unit-testable without a DataStore.
         *
         * - First ever session, or a gap of more than one day: the streak restarts at 1.
         * - Active on consecutive days: the streak grows.
         * - A clock moved backwards (today before lastDay) also restarts, rather than
         *   producing a negative or frozen streak.
         */
        fun nextStreak(current: Int, lastDay: Long?, today: Long): Int = when {
            lastDay == null -> 1
            today == lastDay + 1 -> current + 1
            today == lastDay -> current
            else -> 1
        }
    }
}
