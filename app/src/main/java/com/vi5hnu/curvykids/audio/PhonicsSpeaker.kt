package com.vi5hnu.curvykids.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

/**
 * Speaks letters/numbers and their example words ("A — Apple") using the platform
 * Text-To-Speech engine, so phonics works with no bundled audio assets.
 *
 * Initialisation is asynchronous; requests made before the engine is ready are dropped
 * (the child can tap again), which keeps the wrapper simple and stateless.
 */
class PhonicsSpeaker(context: Context) {

    companion object {
        private const val TAG = "PHONICS"
        /** Default kids' speaking rate (a little slower than normal). */
        const val DEFAULT_RATE = 0.85f
    }

    @Volatile
    private var ready = false

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Optional callbacks fired (on the main thread) when a spoken utterance starts / finishes. Used
     * by sequential read-aloud (e.g. Stories) to highlight the sentence currently being read. The
     * id is the `utteranceId` passed to [speak]. Set to null when leaving the screen.
     */
    @Volatile
    var onUtteranceStart: ((String) -> Unit)? = null

    @Volatile
    var onUtteranceDone: ((String) -> Unit)? = null

    /**
     * Fired (on the main thread) as the engine speaks through an utterance, with the character
     * [start]/[end] range currently being voiced. Lets a single, naturally-flowing utterance drive
     * a word/line highlight (used by Stories) instead of choppy one-utterance-per-line playback.
     */
    @Volatile
    var onUtteranceRange: ((id: String, start: Int, end: Int) -> Unit)? = null

    /** When true, [speak] is a no-op and any in-progress speech is halted (parent sound toggle). */
    @Volatile
    var muted: Boolean = false
        set(value) {
            field = value
            if (value) stop()
        }

    // Holds the first speak() call that arrives before TTS is ready, so the initial
    // "Draw the letter A" prompt isn't silently dropped due to the async init race.
    @Volatile
    private var pendingText: String? = null

    @Volatile
    private var pendingLangTag: String? = null

    /** Currently-applied TTS language, so we only switch locale when it actually changes. */
    @Volatile
    private var currentLangTag: String = Locale.US.toLanguageTag()

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            // Slower rate is easier for young children to follow.
            tts.setSpeechRate(DEFAULT_RATE)
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    val id = utteranceId ?: return
                    mainHandler.post { onUtteranceStart?.invoke(id) }
                }
                override fun onDone(utteranceId: String?) {
                    val id = utteranceId ?: return
                    mainHandler.post { onUtteranceDone?.invoke(id) }
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    val id = utteranceId ?: return
                    mainHandler.post { onUtteranceDone?.invoke(id) }
                }
                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    val id = utteranceId ?: return
                    mainHandler.post { onUtteranceRange?.invoke(id, start, end) }
                }
            })
            ready = true
            pendingText?.let { speak(it, pendingLangTag) }
            pendingText = null
            pendingLangTag = null
        } else {
            Log.e(TAG, "TextToSpeech init failed: $status")
        }
    }

    /**
     * Speaks the given phrase.
     *
     * @param langTag optional BCP-47 tag (e.g. "hi-IN") to pronounce non-English text. The locale is
     *        switched only when it changes; if the device lacks that voice, it falls back to US so
     *        speech is never silently broken.
     * @param flush when true (default) interrupts any current speech; when false the phrase is
     *        QUEUED after whatever is playing — used so a praise/feedback line finishes before the
     *        next prompt speaks, instead of being cut off mid-word.
     */
    fun speak(text: String, langTag: String? = null, flush: Boolean = true, utteranceId: String = text) {
        if (text.isBlank() || muted) return
        if (!ready) {
            pendingText = text  // will fire once TTS is initialised
            pendingLangTag = langTag
            return
        }
        applyLanguage(langTag)
        val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts.speak(text, mode, null, utteranceId)
    }

    /**
     * Whether the engine has a usable voice for [langTag]. Optimistic before init (returns true);
     * used to decide between speaking Devanagari directly vs a romanized English fallback.
     */
    fun supports(langTag: String): Boolean {
        if (!ready) return true
        return tts.isLanguageAvailable(Locale.forLanguageTag(langTag)) >= TextToSpeech.LANG_AVAILABLE
    }

    /**
     * Sets the speaking pitch (1.0 = normal). Higher values sound younger/"boyish" — used by the
     * Barakhadi recite mode so the forms are read like a child reciting in class. Remember to reset
     * to 1.0 afterwards so other prompts keep the normal voice.
     */
    fun setPitch(pitch: Float) {
        if (ready) tts.setPitch(pitch)
    }

    /**
     * Sets the speaking rate (1.0 = normal; the engine default for this app is 0.85, set on init).
     * Story narration nudges this closer to natural so sentences flow; reset to [DEFAULT_RATE]
     * afterwards so the slower kids' pace returns for letters/words.
     */
    fun setSpeechRate(rate: Float) {
        if (ready) tts.setSpeechRate(rate)
    }

    /** Switches the TTS voice to [langTag] (default US), with a safe fallback if unsupported. */
    private fun applyLanguage(langTag: String?) {
        val target = langTag ?: Locale.US.toLanguageTag()
        if (target == currentLangTag) return
        val result = tts.setLanguage(Locale.forLanguageTag(target))
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "TTS language '$target' unavailable; falling back to US")
            tts.language = Locale.US
            currentLangTag = Locale.US.toLanguageTag()
        } else {
            currentLangTag = target
        }
    }

    /**
     * Immediately silences any in-progress or queued speech without shutting the engine
     * down (unlike [release]). Call this when leaving a screen so a long prompt doesn't keep
     * talking after the child has navigated away. Also drops a not-yet-spoken [pendingText].
     */
    fun stop() {
        pendingText = null
        pendingLangTag = null
        if (ready) tts.stop()
    }

    fun release() {
        tts.stop()
        tts.shutdown()
    }
}
