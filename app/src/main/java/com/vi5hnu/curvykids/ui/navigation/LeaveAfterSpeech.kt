package com.vi5hnu.curvykids.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Leaving a screen without cutting its voice off mid-sentence, and without the line spilling onto
 * the next screen:
 * - Quiet: leaves at once.
 * - Talking: the tap is held until the line ends ([PhonicsSpeaker.awaitQuiet], capped), and
 *   [waiting] is true meanwhile; provide it as [com.vi5hnu.curvykids.ui.kit.LocalLeavePending]
 *   so the back button pulses to show the tap was heard.
 * - A second tap while held leaves straight away and silences the voice, so an impatient child
 *   or a grown-up is never stuck.
 *
 * Screens that read at length (a story, a Barakhadi recitation) stop their own reading before
 * asking to leave, so they leave at once.
 */
@Stable
class LeaveState internal constructor(
    private val speaker: PhonicsSpeaker,
    private val scope: CoroutineScope,
    private val leave: () -> Unit,
) {
    /** True while a leave is held for the voice to finish. */
    var waiting by mutableStateOf(false)
        private set

    private var job: Job? = null

    /** The voice's [PhonicsSpeaker.generation] when this screen left through [request], else null. */
    private var leftAt: Long? = null

    fun request() {
        if (leftAt != null) return // a late second tap must not leave the screen behind this one too
        when {
            waiting -> { job?.cancel(); speaker.stop(); go() }
            !speaker.speaking.value -> go()
            else -> {
                waiting = true
                job = scope.launch { speaker.awaitQuiet(); go() }
            }
        }
    }

    /**
     * Silences what this screen was saying, for its teardown. That runs after the exit animation,
     * by which time the next screen may be saying its name, which must not be cut; so after a
     * [request] only speech this screen started is stopped. Any other way out stops it all.
     */
    fun silence() {
        leftAt?.let(speaker::stopIfStill) ?: speaker.stop()
    }

    private fun go() {
        leftAt = speaker.generation
        waiting = false
        job = null
        leave()
    }
}

/** A [LeaveState] for one visit of a screen; [leave] does the actual navigation. */
@Composable
fun rememberLeave(speaker: PhonicsSpeaker, leave: () -> Unit): LeaveState {
    val scope = rememberCoroutineScope()
    val currentLeave by rememberUpdatedState(leave)
    return remember(speaker, scope) { LeaveState(speaker, scope) { currentLeave() } }
}
