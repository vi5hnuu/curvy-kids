package com.vi5hnu.curvykids.ui.game

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.withTimeoutOrNull

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

    /** Shared TTS instance — exposed so activity screens can speak names without a second TTS. */
    val speaker: PhonicsSpeaker get() = phonics

    /**
     * Enables/disables all game audio (voice + sound effects), driven by the parent zone's
     * "Sound effects & voice" toggle. Muting also halts any in-progress speech.
     */
    fun setAudioEnabled(enabled: Boolean) {
        phonics.muted = !enabled
        sound.muted = !enabled
    }

    init {
        viewModelScope.launch {
            val saved = progress.load()
            val mastered = progress.masteredSet(saved.level)
            // Restore saved level/index without speaking — the prompt fires when the user
            // actually opens the TraceScreen (via goTo() inside selectLevel()).
            _uiState.update { it.copy(level = saved.level, index = saved.index, masteredCharacters = mastered) }
        }
        viewModelScope.launch { recognizer.prepare() }
    }

    /** Recognises the drawn [strokes] and gives correct/wrong feedback. */
    fun check(strokes: List<Stroke>, writingArea: WritingArea?) {
        val state = _uiState.value
        if (state.isChecking || strokes.isEmpty()) return

        _uiState.update { it.copy(isChecking = true, lastResult = null) }
        viewModelScope.launch {
            // Timeout so a hung recognition can never leave the Check button stuck-disabled.
            val candidates = withTimeoutOrNull(RECOGNIZE_TIMEOUT_MS) {
                recognizer.recognize(strokes, writingArea, preContext = null)
            } ?: emptyList()
            val matched = CharacterMatcher.matches(state.character, candidates, state.level)
            Log.d(TAG, "check level=${state.level} expected=${state.character} " +
                "candidates=$candidates matched=$matched")

            if (matched) {
                sound.playCorrect()
                speakPraise()
                viewModelScope.launch { progress.markMastered(state.level, state.character) }
                val newMastered = state.masteredCharacters + state.character
                // Only trigger the completion overlay when the level is mastered for the first
                // time — not on repeat correct answers after full completion.
                val levelComplete = state.masteredCharacters.size < state.level.characters.size &&
                    newMastered.size == state.level.characters.size
                _uiState.update {
                    it.copy(
                        isChecking = false,
                        lastResult = AnswerResult.CORRECT,
                        score = it.score + 1,
                        masteredCharacters = newMastered,
                        levelJustCompleted = levelComplete,
                    )
                }
                if (!levelComplete) {
                    delay(CORRECT_ADVANCE_DELAY_MS)
                    next()
                }
                // If levelComplete, the overlay handles navigation via dismissLevelComplete().
            } else {
                sound.playWrong()
                haptics.vibrate()
                val newAttempts = state.wrongAttempts + 1
                _uiState.update {
                    it.copy(isChecking = false, lastResult = AnswerResult.WRONG, wrongAttempts = newAttempts)
                }
                if (newAttempts >= MAX_WRONG_ATTEMPTS) {
                    speakGiveUp()
                    delay(GIVE_UP_DELAY_MS)
                    next()
                } else {
                    speakTryAgain(sawNothing = candidates.isEmpty())
                }
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
        viewModelScope.launch {
            val mastered = progress.masteredSet(level)
            goTo(level, progress.loadIndex(level), mastered)
        }
    }

    /** Jumps directly to a character by [index] — used by the progress map. */
    fun jumpToIndex(index: Int) {
        val state = _uiState.value
        goTo(state.level, index.coerceIn(0, state.level.characters.lastIndex))
    }

    /** Clears the last answer result so the feedback badge disappears (e.g. after erasing). */
    fun clearFeedback() {
        _uiState.update { it.copy(lastResult = null) }
    }

    /** Called when the child taps/dismisses the level-complete overlay; advances to next level. */
    fun dismissLevelComplete() {
        _uiState.update { it.copy(levelJustCompleted = false) }
        nextLevel()
    }

    /** Re-speaks the current character's phonics (the "hear it" 🔊 button). */
    fun speakCurrent() {
        phonics.speak(Phonics.phraseFor(_uiState.value.character))
    }

    /** Guides the child on what to draw, e.g. "Draw the letter A. A for Apple." */
    private fun speakPrompt() {
        val state = _uiState.value
        val noun = if (state.level == Level.NUMBERS) "number" else "letter"
        val word = Phonics.wordFor(state.character)
        val suffix = word?.let { " ${Phonics.phraseFor(state.character)}." } ?: ""
        phonics.speak("Draw the $noun ${state.character}.$suffix")
    }

    private fun speakPraise() {
        phonics.speak(PRAISE.random())
    }

    /** Spoken when the child has used all attempts — gentle, not discouraging. */
    private fun speakGiveUp() {
        val state = _uiState.value
        phonics.speak("That's OK! Keep practicing. ${Phonics.phraseFor(state.character)}. Moving on!")
    }

    /** Encourages another attempt; gives a clearer hint when nothing was recognised. */
    private fun speakTryAgain(sawNothing: Boolean) {
        val state = _uiState.value
        val noun = if (state.level == Level.NUMBERS) "number" else "letter"
        val lead = if (sawNothing) "Hmm, I couldn't see it. Try drawing a bit bigger."
        else "Almost! Let's try again."
        val word = Phonics.wordFor(state.character)
        val hint = word?.let { " This is ${state.character}. ${Phonics.phraseFor(state.character)}." }
            ?: " This is ${state.character}."
        phonics.speak("$lead$hint")
    }

    private fun goTo(level: Level, index: Int, mastered: Set<String> = _uiState.value.masteredCharacters) {
        _uiState.update {
            it.copy(
                level = level,
                index = index,
                lastResult = null,
                isChecking = false,
                masteredCharacters = mastered,
                wrongAttempts = 0,
                levelJustCompleted = false,
            )
        }
        viewModelScope.launch { progress.save(level, index) }
        speakPrompt()
    }

    /** Advances to the next level in order: UPPERCASE → LOWERCASE → NUMBERS → UPPERCASE. */
    private fun nextLevel() {
        val next = when (_uiState.value.level) {
            Level.UPPERCASE -> Level.LOWERCASE
            Level.LOWERCASE -> Level.NUMBERS
            Level.NUMBERS -> Level.UPPERCASE
        }
        viewModelScope.launch {
            val mastered = progress.masteredSet(next)
            goTo(next, progress.loadIndex(next), mastered)
        }
    }

    override fun onCleared() {
        recognizer.release()
        sound.release()
        phonics.release()
    }

    companion object {
        private const val TAG = "CurvyKidsVM"
        private const val CORRECT_ADVANCE_DELAY_MS = 1200L
        private const val RECOGNIZE_TIMEOUT_MS = 8000L
        private const val MAX_WRONG_ATTEMPTS = 3
        private const val GIVE_UP_DELAY_MS = 2000L
        private val PRAISE = listOf("Yay! Great job!", "Awesome!", "Well done!", "Perfect!", "You did it!")

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
