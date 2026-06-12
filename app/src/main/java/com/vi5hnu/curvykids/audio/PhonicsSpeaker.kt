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

    // Holds the first speak() call that arrives before TTS is ready, so the initial
    // "Draw the letter A" prompt isn't silently dropped due to the async init race.
    @Volatile
    private var pendingText: String? = null

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            // Slower rate is easier for young children to follow.
            tts.setSpeechRate(0.85f)
            ready = true
            pendingText?.let { speak(it) }
            pendingText = null
        } else {
            Log.e(TAG, "TextToSpeech init failed: $status")
        }
    }

    /** Speaks the given phrase, replacing anything currently being spoken. */
    fun speak(text: String) {
        if (text.isBlank()) return
        if (!ready) {
            pendingText = text  // will fire once TTS is initialised
            return
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    /**
     * Immediately silences any in-progress or queued speech without shutting the engine
     * down (unlike [release]). Call this when leaving a screen so a long prompt doesn't keep
     * talking after the child has navigated away. Also drops a not-yet-spoken [pendingText].
     */
    fun stop() {
        pendingText = null
        if (ready) tts.stop()
    }

    fun release() {
        tts.stop()
        tts.shutdown()
    }
}
