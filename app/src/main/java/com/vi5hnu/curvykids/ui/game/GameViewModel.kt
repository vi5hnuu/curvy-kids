package com.vi5hnu.curvykids.ui.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.SoundEffects
import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.data.content.Phonics
import com.vi5hnu.curvykids.data.progress.ProgressRepository
import com.vi5hnu.curvykids.haptics.Haptics
import com.vi5hnu.curvykids.models.HttpState
import com.vi5hnu.curvykids.recognition.CharacterMatcher
import com.vi5hnu.curvykids.recognition.MlKitInkRecognizer
import com.vi5hnu.curvykids.recognition.Recognizer
import com.vi5hnu.curvykids.recognition.Stroke
import com.vi5hnu.curvykids.recognition.WritingArea
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Owns the game loop and state. Depends on the [Recognizer] abstraction plus small platform
 * service wrappers, so the recognition backend can be swapped or faked without touching this
 * class (Dependency Inversion). Replaces the logic that previously lived in the Angular
 * `AppComponent` and the JS bridge.
 */
class GameViewModel(
    private val recognizer: Recognizer,
    private val sound: SoundEffects,
    private val phonics: PhonicsSpeaker,
    private val haptics: Haptics,
    private val progress: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /** Recognizer readiness, surfaced for the loading/error overlay. */
    val recognizerReady: StateFlow<HttpState?> = recognizer.ready

    init {
        viewModelScope.launch {
            val saved = progress.load()
            _uiState.update { it.copy(level = saved.level, index = saved.index) }
            speakCurrent()
        }
        viewModelScope.launch { recognizer.prepare() }
    }

    /** Recognises the drawn [strokes] and gives correct/wrong feedback. */
    fun check(strokes: List<Stroke>, writingArea: WritingArea?) {
        val state = _uiState.value
        if (state.isChecking || strokes.isEmpty()) return

        _uiState.update { it.copy(isChecking = true, lastResult = null) }
        viewModelScope.launch {
            val candidates = recognizer.recognize(strokes, writingArea, preContext = null)
            val matched = CharacterMatcher.matches(state.character, candidates, state.level)
            if (matched) {
                sound.playCorrect()
                _uiState.update {
                    it.copy(isChecking = false, lastResult = AnswerResult.CORRECT, score = it.score + 1)
                }
                delay(CORRECT_ADVANCE_DELAY_MS) // let the child see confetti before moving on
                next()
            } else {
                sound.playWrong()
                haptics.vibrate()
                _uiState.update { it.copy(isChecking = false, lastResult = AnswerResult.WRONG) }
            }
        }
    }

    fun next() {
        val state = _uiState.value
        val nextIndex = (state.index + 1) % state.level.characters.size
        goTo(state.level, nextIndex)
    }

    fun previous() {
        val state = _uiState.value
        val size = state.level.characters.size
        val prevIndex = (state.index - 1 + size) % size
        goTo(state.level, prevIndex)
    }

    /** Switches track, resuming at that level's last saved character. */
    fun selectLevel(level: Level) {
        if (level == _uiState.value.level) return
        viewModelScope.launch { goTo(level, progress.loadIndex(level)) }
    }

    /** Re-speaks the current character's phonics (e.g. a "hear it" button). */
    fun speakCurrent() {
        phonics.speak(Phonics.phraseFor(_uiState.value.character))
    }

    private fun goTo(level: Level, index: Int) {
        _uiState.update {
            it.copy(level = level, index = index, lastResult = null, isChecking = false)
        }
        viewModelScope.launch { progress.save(level, index) }
        speakCurrent()
    }

    override fun onCleared() {
        recognizer.release()
        sound.release()
        phonics.release()
    }

    companion object {
        private const val CORRECT_ADVANCE_DELAY_MS = 1200L

        /** Builds a ViewModel with concrete platform-backed dependencies. */
        fun factory(context: Context): ViewModelProvider.Factory {
            val app = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(
                        recognizer = MlKitInkRecognizer(),
                        sound = SoundEffects(app),
                        phonics = PhonicsSpeaker(app),
                        haptics = Haptics(app),
                        progress = ProgressRepository(app),
                    ) as T
                }
            }
        }
    }
}
