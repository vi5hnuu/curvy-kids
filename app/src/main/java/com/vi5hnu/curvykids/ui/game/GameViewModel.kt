package com.vi5hnu.curvykids.ui.game

import com.vi5hnu.curvykids.CurvyKidsApp
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.audio.SoundEffects
import com.vi5hnu.curvykids.data.app.Difficulty
import com.vi5hnu.curvykids.data.content.HINDI_BY_CHAR
import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.VoiceLine
import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.data.content.Phonics
import com.vi5hnu.curvykids.data.content.Script
import com.vi5hnu.curvykids.data.progress.ProgressRepository
import com.vi5hnu.curvykids.haptics.Haptics
import com.vi5hnu.curvykids.models.HttpState
import com.vi5hnu.curvykids.recognition.CharacterMatcher
import com.vi5hnu.curvykids.recognition.MlKitInkRecognizer
import com.vi5hnu.curvykids.recognition.Recognizer
import com.vi5hnu.curvykids.recognition.Stroke
import com.vi5hnu.curvykids.recognition.WritingArea
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _correctAnswers = MutableSharedFlow<CorrectAnswer>(extraBufferCapacity = 4)

    /**
     * Emitted exactly once each time a drawing is accepted.
     *
     * Rewards must not be driven off [uiState].lastResult: that is state, so it is replayed to
     * every new collector — a screen rotation re-delivered a still-CORRECT result and the child
     * was paid twice for one letter. An event stream is consumed once and never replayed.
     */
    val correctAnswers: SharedFlow<CorrectAnswer> = _correctAnswers.asSharedFlow()

    /** Recognizer readiness, surfaced for the loading/error overlay. */
    val recognizerReady: StateFlow<HttpState?> = recognizer.ready

    /** Shared TTS instance — exposed so activity screens can speak names without a second TTS. */
    val speaker: PhonicsSpeaker get() = phonics

    /** Unified voice + sound + haptic feedback for the play/quiz screens. */
    val feedback: PlayFeedback by lazy { PlayFeedback(phonics, sound, haptics) }

    /**
     * Enables/disables all game audio (voice + sound effects), driven by the parent zone's
     * "Sound effects & voice" toggle. Muting also halts any in-progress speech.
     */
    fun setAudioEnabled(enabled: Boolean) {
        phonics.muted = !enabled
        sound.muted = !enabled
    }

    /** Attempts allowed on a character before the app moves on; Easy is more forgiving. */
    private var maxWrongAttempts: Int = MAX_WRONG_ATTEMPTS_NORMAL

    fun setDifficulty(difficulty: Difficulty) {
        maxWrongAttempts = when (difficulty) {
            Difficulty.EASY -> MAX_WRONG_ATTEMPTS_EASY
            Difficulty.NORMAL -> MAX_WRONG_ATTEMPTS_NORMAL
        }
    }

    init {
        viewModelScope.launch {
            val saved = progress.load()
            val mastered = progress.masteredSet(saved.level)
            // Restore saved level/index without speaking — the prompt fires when the user
            // actually opens the TraceScreen (via goTo() inside selectLevel()).
            _uiState.update { it.copy(level = saved.level, index = saved.index, masteredCharacters = mastered) }
            // Warm the recognizer model for the resumed track (en-US or hi).
            recognizer.ensure(saved.level.languageTag)
        }
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

            // The child may have tapped Next/Previous during the up-to-8s recognition. Scoring
            // the stale character would credit (and advance past) the wrong letter, so drop a
            // result that no longer describes what is on screen.
            val currentState = _uiState.value
            if (currentState.level != state.level || currentState.index != state.index) {
                _uiState.update { it.copy(isChecking = false) }
                return@launch
            }

            if (matched) {
                sound.playCorrect()
                speakPraise()
                // Then the letter's own line again ("A for Apple"), so the shape just written is
                // heard with its sound at the moment it is praised.
                speakLetterLine(state.character)
                viewModelScope.launch { progress.markMastered(state.level, state.character) }
                val newMastered = state.masteredCharacters + state.character
                _correctAnswers.tryEmit(
                    CorrectAnswer(
                        topicId = state.level.progressKey,
                        character = state.character,
                        alreadyMastered = state.character in state.masteredCharacters,
                    )
                )
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
                        lastWasPractice = state.character in state.masteredCharacters,
                    )
                }
                if (!levelComplete) {
                    delay(CORRECT_ADVANCE_DELAY_MS)
                    // Queue the next prompt so the praise line finishes instead of being cut off.
                    next(queueSpeech = true)
                }
                // If levelComplete, the overlay handles navigation via dismissLevelComplete().
            } else {
                sound.playWrong()
                haptics.vibrate()
                val newAttempts = state.wrongAttempts + 1
                _uiState.update {
                    it.copy(isChecking = false, lastResult = AnswerResult.WRONG, wrongAttempts = newAttempts)
                }
                if (newAttempts >= maxWrongAttempts) {
                    speakGiveUp()
                    delay(GIVE_UP_DELAY_MS)
                    // Queue so the "keep practicing" line finishes before the next prompt.
                    next(queueSpeech = true)
                } else {
                    speakTryAgain(sawNothing = candidates.isEmpty())
                }
            }
        }
    }

    fun next(queueSpeech: Boolean = false) {
        val state = _uiState.value
        val nextIndex = (state.index + 1) % state.level.characters.size
        goTo(state.level, nextIndex, queueSpeech = queueSpeech)
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
        // Switch the recognizer model concurrently — readiness gates input in TraceScreen, so a
        // first-time Hindi model download shows the loader while the UI already shows the letter.
        viewModelScope.launch { recognizer.ensure(level.languageTag) }
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

    /**
     * Spoken when the child taps Check with nothing drawn. The button stays enabled so the tap
     * always does something; this is what it does.
     */
    fun promptToDraw() {
        val state = _uiState.value
        if (state.level.script == Script.DEVANAGARI && speakHindi(HindiVoiceScript.WRITE_FIRST)) return
        phonics.speak("Draw it first, then tap check.")
    }

    /**
     * Clears all tracing progress and returns to the first character of the first level.
     *
     * Paired with AppViewModel.resetProgress by the navigation graph: the two ViewModels own
     * different DataStore files, so a full reset has to go through both.
     */
    fun resetProgress() {
        viewModelScope.launch {
            progress.clear()
            _uiState.value = GameUiState()
            recognizer.ensure(Level.UPPERCASE.languageTag)
        }
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
        val state = _uiState.value
        if (state.level.script == Script.DEVANAGARI) {
            speakHindiLetter(state.character); return
        }
        phonics.speak(Phonics.phraseFor(state.character))
    }

    /** Queues [char]'s spoken line after whatever is playing ("A for Apple", "क से कबूतर"). */
    private fun speakLetterLine(char: String) {
        if (_uiState.value.level.script == Script.DEVANAGARI) {
            speakHindiLetter(char, queue = true); return
        }
        phonics.speak(Phonics.phraseFor(char), flush = false)
    }

    /** Guides the child on what to draw, e.g. "Draw the letter A. A for Apple." */
    private fun speakPrompt(queue: Boolean = false) {
        val state = _uiState.value
        if (state.level.script == Script.DEVANAGARI) {
            speakHindiLetter(state.character, queue); return
        }
        val noun = if (state.level == Level.NUMBERS) "number" else "letter"
        val word = Phonics.wordFor(state.character)
        val suffix = word?.let { " ${Phonics.phraseFor(state.character)}." } ?: ""
        phonics.speak("Draw the $noun ${state.character}.$suffix", flush = !queue)
    }

    /**
     * Whether the drawn [strokes] read as [expected] in Devanagari.
     *
     * A narrower door onto the recogniser than [check], for screens that mark a single piece of
     * writing without owning a whole tracing session - the barakhadi writing page. Keeping it
     * here means those screens never have to know that a recogniser exists, let alone which one.
     *
     * Returns null when the model is not available, so the caller can leave the check out
     * rather than tell the child their writing is wrong when nothing actually read it.
     */
    suspend fun readsAs(strokes: List<Stroke>, expected: String): Boolean? {
        if (strokes.isEmpty()) return null
        recognizer.ensure(Level.HINDI_CONSONANTS.languageTag)
        if (recognizer.ready.value?.success != true) return null
        val candidates = withTimeoutOrNull(RECOGNIZE_TIMEOUT_MS) {
            recognizer.recognize(strokes, null, preContext = null)
        } ?: return null
        val matched = CharacterMatcher.matches(expected, candidates, Level.HINDI_CONSONANTS)
        Log.d(TAG, "readsAs expected=$expected candidates=$candidates matched=$matched")
        return matched
    }

    /**
     * Speaks a Hindi letter naturally ("अ, अ से अनानास"): the recording if bundled, else the Hindi
     * voice, else the romanized name in English so the child always hears something.
     */
    private fun speakHindiLetter(char: String, queue: Boolean = false) {
        val letter = HINDI_BY_CHAR[char]
        if (letter == null) {
            phonics.speak(char, langTag = HINDI_TTS_TAG, flush = !queue, romanized = char); return
        }
        val line = HindiVoiceScript.letter(letter)
        phonics.speak(
            line.text,
            langTag = HINDI_TTS_TAG,
            flush = !queue,
            romanized = letter.romanized,
            clip = line.clip,
        )
    }

    /**
     * Speaks a fixed Hindi phrase if it can be said properly (a recording, or a Hindi voice).
     * Returns false so the caller can say its English line instead of a garbled one.
     */
    private fun speakHindi(line: VoiceLine): Boolean {
        if (!phonics.canSpeak(HINDI_TTS_TAG, line.clip)) return false
        phonics.speak(line.text, langTag = HINDI_TTS_TAG, clip = line.clip)
        return true
    }

    private fun speakPraise() {
        if (_uiState.value.level.script == Script.DEVANAGARI &&
            speakHindi(HindiVoiceScript.PRAISE.random())) return
        phonics.speak(PRAISE.random())
    }

    /** Spoken when the child has used all attempts — gentle, not discouraging. */
    private fun speakGiveUp() {
        val state = _uiState.value
        if (state.level.script == Script.DEVANAGARI && speakHindi(HindiVoiceScript.GIVE_UP)) return
        if (state.level.script == Script.DEVANAGARI) { phonics.speak("That's okay! Let's try the next one."); return }
        phonics.speak("That's OK! Keep practicing. ${Phonics.phraseFor(state.character)}. Moving on!")
    }

    /** Encourages another attempt; gives a clearer hint when nothing was recognised. */
    private fun speakTryAgain(sawNothing: Boolean) {
        val state = _uiState.value
        if (state.level.script == Script.DEVANAGARI) {
            if (!speakHindi(HindiVoiceScript.TRY_AGAIN)) phonics.speak("Try again!")
            return
        }
        val noun = if (state.level == Level.NUMBERS) "number" else "letter"
        val lead = if (sawNothing) "Hmm, I couldn't see it. Try drawing a bit bigger."
        else "Almost! Let's try again."
        val word = Phonics.wordFor(state.character)
        val hint = word?.let { " This is ${state.character}. ${Phonics.phraseFor(state.character)}." }
            ?: " This is ${state.character}."
        phonics.speak("$lead$hint")
    }

    private fun goTo(
        level: Level,
        index: Int,
        mastered: Set<String> = _uiState.value.masteredCharacters,
        queueSpeech: Boolean = false,
    ) {
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
        speakPrompt(queueSpeech)
    }

    /**
     * Advances to the next level, cycling within the same script so the English and Hindi tracks
     * stay separate: A→a→1→A for Latin, स्वर→व्यंजन→स्वर for Devanagari.
     */
    private fun nextLevel() {
        val next = when (_uiState.value.level) {
            Level.UPPERCASE -> Level.LOWERCASE
            Level.LOWERCASE -> Level.NUMBERS
            Level.NUMBERS -> Level.UPPERCASE
            Level.HINDI_VOWELS -> Level.HINDI_CONSONANTS
            Level.HINDI_CONSONANTS -> Level.HINDI_VOWELS
        }
        viewModelScope.launch { recognizer.ensure(next.languageTag) }
        viewModelScope.launch {
            val mastered = progress.masteredSet(next)
            goTo(next, progress.loadIndex(next), mastered)
        }
    }

    override fun onCleared() {
        super.onCleared()
        recognizer.release()
        sound.release()
        phonics.release()
    }

    companion object {
        private const val TAG = "CurvyKidsVM"
        private const val CORRECT_ADVANCE_DELAY_MS = 1200L
        private const val RECOGNIZE_TIMEOUT_MS = 8000L
        private const val MAX_WRONG_ATTEMPTS_NORMAL = 3
        private const val MAX_WRONG_ATTEMPTS_EASY = 5
        private const val GIVE_UP_DELAY_MS = 2000L
        private val PRAISE = listOf("Yay! Great job!", "Awesome!", "Well done!", "Perfect!", "You did it!")

        /** BCP-47 tag for the Hindi TTS voice (India), distinct from the recognizer's "hi" model tag. */
        private const val HINDI_TTS_TAG = "hi-IN"

        /** Builds a ViewModel with concrete platform-backed dependencies. */
        fun factory(context: Context): ViewModelProvider.Factory {
            val app = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(
                        recognizer = MlKitInkRecognizer(),
                        sound = SoundEffects(app),
                        phonics = PhonicsSpeaker(app, (app as CurvyKidsApp).clipSources),
                        haptics = Haptics(app),
                        progress = ProgressRepository(app),
                    ) as T
                }
            }
        }
    }
}
