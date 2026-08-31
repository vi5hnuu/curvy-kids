package com.vi5hnu.curvykids.ui.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vi5hnu.curvykids.data.app.AppRepository
import com.vi5hnu.curvykids.data.app.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Persisted parent-facing settings. */
data class AppSettings(
    val soundEffects: Boolean = true,
    val difficulty: Difficulty = Difficulty.NORMAL,
)

/**
 * Shared ViewModel that holds global app state — stars, streak, mastered items, badges,
 * lastTopicId. Owned at the Activity level so all screens share the same instance.
 */
class AppViewModel(private val repo: AppRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            repo.record.collect { record ->
                _uiState.value = AppUiState(
                    stars = record.stars,
                    streak = record.streak,
                    mastered = record.mastered,
                    badges = record.badges,
                    lastTopicId = record.lastTopicId,
                )
                _settings.value = AppSettings(
                    soundEffects = record.soundEffects,
                    difficulty = record.difficulty,
                )
            }
        }
    }

    /**
     * Award [amount] stars to the child and persist.
     *
     * Earning a star is what counts as "the child played today", so this is also where the
     * daily streak advances — keeping the two together means no screen can award progress
     * without the streak noticing.
     */
    fun reward(amount: Int) {
        viewModelScope.launch {
            repo.addStars(amount)
            repo.recordActivity(LocalDate.now().toEpochDay())
        }
    }

    /**
     * Mark a character as mastered. [set] is the trace topic id ("upper", "lower", "numbers",
     * "hindi_vowels", "hindi_consonants"), [char] is the character string (e.g. "A", "a", "3").
     */
    fun markMastered(set: String, char: String) {
        viewModelScope.launch { repo.markMastered("$set:$char") }
    }

    /** Unlock a badge by id (e.g. "colors", "first"). */
    fun addBadge(id: String) {
        viewModelScope.launch { repo.addBadge(id) }
    }

    /** Remember which topic the child played last (for the "Continue" card). */
    fun setLastTopic(topicId: String) {
        viewModelScope.launch { repo.setLastTopic(topicId) }
    }

    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch { repo.setSoundEffects(enabled) }
    }

    fun setDifficulty(difficulty: Difficulty) {
        viewModelScope.launch { repo.setDifficulty(difficulty) }
    }

    /** Clears all child progress; the parent sound setting is kept. */
    fun resetProgress() {
        viewModelScope.launch { repo.resetProgress() }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val app = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AppViewModel(AppRepository(app)) as T
            }
        }
    }
}
