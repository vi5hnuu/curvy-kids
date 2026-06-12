package com.vi5hnu.curvykids.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.vi5hnu.curvykids.R

/**
 * Low-latency playback of short game sound effects via [SoundPool] (preferable to
 * MediaPlayer for tiny, frequently-triggered clips). Replaces the web app's HTML `<audio>`.
 *
 * Call [release] when no longer needed (e.g. ViewModel onCleared).
 */
class SoundEffects(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val correctId = soundPool.load(context, R.raw.correct, 1)
    private val wrongId = soundPool.load(context, R.raw.wrong, 1)

    /** When true, [playCorrect]/[playWrong] are no-ops — driven by the parent's sound toggle. */
    @Volatile
    var muted: Boolean = false

    fun playCorrect() = play(correctId)
    fun playWrong() = play(wrongId)

    private fun play(soundId: Int) {
        if (muted) return
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    fun release() = soundPool.release()
}
