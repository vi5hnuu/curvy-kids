package com.vi5hnu.curvykids.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

/**
 * One recorded line of narration, stored at `assets/voice/<lang>/<group>/<key>.<ext>`.
 *
 * Text-to-speech is fine for English prompts, but a device without a Hindi voice reads Devanagari
 * through an English voice, which is wrong in a literacy app. So every Hindi line has a stable key
 * that a human recording can be dropped in for. See
 * [com.vi5hnu.curvykids.data.content.HindiVoiceScript] for the full list of keys.
 *
 * @param lang  Folder under `assets/voice/` ("hi", "en").
 * @param group Sub-folder that keeps the recordings browsable ("letters", "barakhadi", "stories").
 * @param key   File name without extension, lowercase ASCII and hyphens (e.g. "letter-ka").
 */
data class VoiceClip(val lang: String, val group: String, val key: String) {
    /** Path under `assets/voice/`, without an extension: "hi/letters/letter-ka". */
    val relativePath: String get() = "$lang/$group/$key"

    /** Asset path without an extension; [ClipPlayer] tries each supported extension in turn. */
    val basePath: String get() = "voice/$relativePath"

    companion object {
        fun hindi(group: String, key: String) = VoiceClip("hi", group, key)
        fun english(group: String, key: String) = VoiceClip("en", group, key)
    }
}

/**
 * Plays [VoiceClip] recordings one after another, from the first of [sources] that has each one
 * (built into the app, or in a downloaded voice pack).
 *
 * Has no text-to-speech fallback: [PhonicsSpeaker] asks [has] first and only routes here when the
 * recording exists. Clips play in the order they are queued, so "praise, then the next prompt"
 * keeps working the way the TTS queue did.
 *
 * Callbacks fire on the main thread, because MediaPlayer posts its listeners to the thread that
 * created it, and this is created on the main thread.
 */
class ClipPlayer(private val sources: List<ClipSource>) {

    private val queue = ArrayDeque<Pair<ClipFile, String>>() // (recording, utteranceId)
    private var player: MediaPlayer? = null

    /** Fired when a clip starts / finishes, with the utteranceId it was queued under. */
    var onStart: ((String) -> Unit)? = null
    var onDone: ((String) -> Unit)? = null

    /** Fired once the last queued clip has finished, so the speaker can run deferred TTS. */
    var onIdle: (() -> Unit)? = null

    val isBusy: Boolean get() = player != null || queue.isNotEmpty()

    /** Whether a recording exists for [clip]. */
    fun has(clip: VoiceClip): Boolean = resolve(clip) != null

    /**
     * Plays [clip]. With [flush], anything playing or queued is dropped first, matching
     * `TextToSpeech.QUEUE_FLUSH`. Returns false when there is no recording.
     */
    fun play(clip: VoiceClip, utteranceId: String, flush: Boolean): Boolean {
        val file = resolve(clip) ?: return false
        if (flush) stop()
        queue.addLast(file to utteranceId)
        if (player == null) next()
        return true
    }

    /** Silences the current clip and drops the queue. Doesn't fire [onDone] for dropped clips. */
    fun stop() {
        queue.clear()
        player?.let { runCatching { it.stop() }; it.release() }
        player = null
    }

    fun release() = stop()

    private fun next() {
        val (file, id) = queue.removeFirstOrNull() ?: run { onIdle?.invoke(); return }
        val mp = MediaPlayer()
        try {
            file.attachTo(mp)
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            mp.setOnCompletionListener { finish(it, id) }
            mp.setOnErrorListener { p, _, _ -> finish(p, id); true }
            mp.prepare()
            player = mp
            onStart?.invoke(id)
            mp.start()
        } catch (e: Exception) {
            // A bad file must not stall the queue behind it.
            Log.w(TAG, "Could not play clip $id", e)
            mp.release()
            onDone?.invoke(id)
            next()
        }
    }

    private fun finish(mp: MediaPlayer, id: String) {
        if (player !== mp) return // already stopped
        mp.release()
        player = null
        onDone?.invoke(id)
        next()
    }

    private fun resolve(clip: VoiceClip): ClipFile? = sources.firstNotNullOfOrNull { it.find(clip) }

    private companion object {
        const val TAG = "CLIPS"
    }
}
