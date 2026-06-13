package com.vi5hnu.curvykids.audio

import com.vi5hnu.curvykids.haptics.Haptics

/**
 * Bundles the three feedback channels a play screen needs — voice, sound effects and haptics —
 * behind two intent-revealing calls. Lets every quiz give consistent "correct"/"wrong" feedback
 * without each screen wiring up three services. The sound engine already honours the parent
 * mute toggle; haptics are independent of it.
 */
class PlayFeedback(
    val speaker: PhonicsSpeaker,
    private val sound: SoundEffects,
    private val haptics: Haptics,
) {
    /** A right answer: happy ding + a light tap. */
    fun correct() {
        sound.playCorrect()
        haptics.vibrate(35)
    }

    /** A wrong answer: gentle buzz + a slightly longer tap. */
    fun wrong() {
        sound.playWrong()
        haptics.vibrate(110)
    }
}
