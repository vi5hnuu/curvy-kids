package com.vi5hnu.curvykids.audio

import android.content.Context
import android.speech.tts.TextToSpeech
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

    private companion object {
        const val TAG = "PHONICS"
    }

    @Volatile
    private var ready = false

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
            tts.setSpeechRate(0.85f)
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
    fun speak(text: String, langTag: String? = null, flush: Boolean = true) {
        if (text.isBlank() || muted) return
        if (!ready) {
            pendingText = text  // will fire once TTS is initialised
            pendingLangTag = langTag
            return
        }
        applyLanguage(langTag)
        val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts.speak(text, mode, null, text)
    }

    /**
     * Whether the engine has a usable voice for [langTag]. Optimistic before init (returns true);
     * used to decide between speaking Devanagari directly vs a romanized English fallback.
     */
    fun supports(langTag: String): Boolean {
        if (!ready) return true
        return tts.isLanguageAvailable(Locale.forLanguageTag(langTag)) >= TextToSpeech.LANG_AVAILABLE
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
