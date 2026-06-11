package com.vi5hnu.curvykids.ui.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vi5hnu.curvykids.data.app.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Persisted parent-facing settings (sound, music, reminder). */
data class AppSettings(
    val soundEffects: Boolean = true,
    val backgroundMusic: Boolean = false,
    val playReminder: Boolean = true,
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
            combine(
                repo.starsFlow,
                repo.streakFlow,
                repo.masteredFlow,
                repo.badgesFlow,
                repo.lastTopicFlow,
            ) { stars, streak, mastered, badges, lastTopic ->
                AppUiState(
                    stars = stars,
                    streak = streak,
                    mastered = mastered,
                    badges = badges,
                    lastTopicId = lastTopic,
                )
            }.collect { _uiState.value = it }
        }
        viewModelScope.launch {
            combine(
                repo.soundEffectsFlow,
                repo.backgroundMusicFlow,
                repo.playReminderFlow,
            ) { sound, music, reminder ->
                AppSettings(soundEffects = sound, backgroundMusic = music, playReminder = reminder)
            }.collect { _settings.value = it }
        }
    }

    /** Award [amount] stars to the child and persist. */
    fun reward(amount: Int) {
        viewModelScope.launch { repo.addStars(amount) }
    }

    /**
     * Mark a character as mastered. [set] is "upper" / "lower" / "numbers",
     * [char] is the character string (e.g. "A", "a", "3").
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

    fun setBackgroundMusic(enabled: Boolean) {
        viewModelScope.launch { repo.setBackgroundMusic(enabled) }
    }

    fun setPlayReminder(enabled: Boolean) {
        viewModelScope.launch { repo.setPlayReminder(enabled) }
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
