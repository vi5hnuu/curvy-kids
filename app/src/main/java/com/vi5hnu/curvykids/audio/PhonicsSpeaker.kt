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

    /** Human recordings, preferred over the synthetic voice whenever one exists for a line. */
    private val clips = ClipPlayer(context)

    /**
     * Lines queued behind audio from the *other* engine (a TTS line behind a clip, or a clip
     * behind a TTS line). Each engine queues its own lines natively, but they can't see each
     * other, so cross-engine order is kept here and released when both have gone quiet.
     */
    private val waiting = ArrayDeque<() -> Unit>()

    /**
     * Id of the last line handed to TTS while it is still playing, else null. Tracked here because
     * `TextToSpeech.isSpeaking` can still read true inside the onDone callback of that same line.
     */
    @Volatile
    private var ttsTail: String? = null

    private fun releaseWaiting() {
        while (waiting.isNotEmpty() && !clips.isBusy && ttsTail == null) {
            waiting.removeFirst().invoke()
        }
    }

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

    @Volatile
    private var pendingRomanized: String? = null

    /** Currently-applied TTS language, so we only switch locale when it actually changes. */
    @Volatile
    private var currentLangTag: String = Locale.US.toLanguageTag()

    init {
        clips.onStart = { id -> onUtteranceStart?.invoke(id) }
        clips.onDone = { id -> onUtteranceDone?.invoke(id) }
        clips.onIdle = { releaseWaiting() }
    }

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
                    mainHandler.post {
                        if (id == ttsTail) ttsTail = null
                        onUtteranceDone?.invoke(id)
                        releaseWaiting()
                    }
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    val id = utteranceId ?: return
                    mainHandler.post {
                        if (id == ttsTail) ttsTail = null
                        onUtteranceDone?.invoke(id)
                        releaseWaiting()
                    }
                }
                override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                    val id = utteranceId ?: return
                    mainHandler.post { onUtteranceRange?.invoke(id, start, end) }
                }
            })
            ready = true
            pendingText?.let { speak(it, pendingLangTag, romanized = pendingRomanized) }
            pendingText = null
            pendingLangTag = null
            pendingRomanized = null
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
     * @param romanized what to say instead when the device has no voice for [langTag]. Whether a
     *        voice exists can only be known once the engine has started, which is after the first
     *        tap, so the choice is made here rather than by the caller: asking beforehand returned
     *        an optimistic yes and sent Devanagari to an English voice, which reads as nonsense.
     * @param clip a recording of this exact line. When it is bundled it is played instead of the
     *        synthetic voice; when it isn't, everything above applies unchanged.
     */
    fun speak(
        text: String,
        langTag: String? = null,
        flush: Boolean = true,
        utteranceId: String = text,
        romanized: String? = null,
        clip: VoiceClip? = null,
    ) {
        if (text.isBlank() || muted) return
        if (clip != null && clips.has(clip)) {
            if (flush) { waiting.clear(); ttsTail = null; if (ready) tts.stop() }
            if (!flush && ttsTail != null) {
                waiting.addLast { clips.play(clip, utteranceId, flush = false) }
            } else {
                clips.play(clip, utteranceId, flush)
            }
            return
        }
        if (flush) { clips.stop(); waiting.clear() }
        else if (clips.isBusy) {
            waiting.addLast { speak(text, langTag, flush = false, utteranceId, romanized) }
            return
        }
        if (!ready) {
            pendingText = text  // will fire once TTS is initialised
            pendingLangTag = langTag
            pendingRomanized = romanized
            return
        }
        val spoken = if (langTag != null && romanized != null && !supports(langTag)) {
            applyLanguage(null)
            romanized
        } else {
            applyLanguage(langTag)
            text
        }
        val mode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        ttsTail = utteranceId
        tts.speak(spoken, mode, null, utteranceId)
    }

    /**
     * Whether the engine has a usable voice for [langTag]. Optimistic before init (returns true);
     * used to decide between speaking Devanagari directly vs a romanized English fallback.
     */
    fun supports(langTag: String): Boolean {
        if (!ready) return true
        return tts.isLanguageAvailable(Locale.forLanguageTag(langTag)) >= TextToSpeech.LANG_AVAILABLE
    }

    /** Whether a human recording is bundled for [clip]. */
    fun hasClip(clip: VoiceClip): Boolean = clips.has(clip)

    /**
     * Whether [langTag] can be spoken properly: either [clip] is recorded, or the device has a
     * voice for the language. Callers use it to choose a Hindi line over an English stand-in.
     */
    fun canSpeak(langTag: String, clip: VoiceClip?): Boolean =
        (clip != null && clips.has(clip)) || supports(langTag)

    /** Whether the device has a real voice installed for [langTag], known only after init. */
    fun hasVoiceFor(langTag: String): Boolean =
        ready && tts.isLanguageAvailable(Locale.forLanguageTag(langTag)) >= TextToSpeech.LANG_AVAILABLE

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
        pendingRomanized = null
        waiting.clear()
        clips.stop()
        ttsTail = null
        if (ready) tts.stop()
    }

    fun release() {
        clips.release()
        tts.stop()
        tts.shutdown()
    }
}
