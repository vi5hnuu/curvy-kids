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

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            // Slower rate is easier for young children to follow.
            tts.setSpeechRate(0.85f)
            ready = true
        } else {
            Log.e(TAG, "TextToSpeech init failed: $status")
        }
    }

    /** Speaks the given phrase, replacing anything currently being spoken. */
    fun speak(text: String) {
        if (!ready || text.isBlank()) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
    }

    fun release() {
        tts.stop()
        tts.shutdown()
    }
}
