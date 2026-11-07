package com.vi5hnu.curvykids.ui.activities.components

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.vi5hnu.curvykids.audio.PlayFeedback
import kotlinx.coroutines.delay
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript
import com.vi5hnu.curvykids.data.content.Speech

/** How one answer tile should look right now. */
enum class ChoiceState { IDLE, CORRECT, WRONG, REVEALED, DIMMED }

/** The tap being shown before the quiz moves on. */
private data class Pending(val tappedKey: String, val correctKey: String, val outcome: QuizOutcome)

/**
 * Runs one quiz session for a screen: which question it's on, the score, what a tapped tile
 * shows, when to celebrate, and when the session is over. The screen only builds questions (from
 * [round]) and draws them; the rules ([QuizRules]) are the same everywhere.
 *
 * Create with [rememberQuizSession]. [QuizScaffold] drives it; a screen with a custom layout can
 * call [answer], [settle] and [stateOf] directly.
 */
@Stable
class QuizSession internal constructor(
    val rules: QuizRules,
    initialProgress: QuizProgress,
    initialRound: Int,
) {
    internal var feedback: PlayFeedback? = null
    internal var onReward: (Int) -> Unit = {}
    internal var rewardPerCorrect: Int = 3

    var progress by mutableStateOf(initialProgress)
        private set

    /** Increases for every new question; key question generation on it. */
    var round by mutableIntStateOf(initialRound)
        private set

    var celebrating by mutableStateOf(false)
        private set

    private var pending by mutableStateOf<Pending?>(null)

    /** True once all questions are done and nothing is still on screen. */
    val finished: Boolean get() = progress.isFinished(rules) && pending == null && !celebrating

    /** Taps are ignored while the last one is still being shown. */
    val busy: Boolean get() = pending != null || celebrating

    fun stateOf(key: String): ChoiceState {
        val p = pending ?: return ChoiceState.IDLE
        return when (p.outcome) {
            QuizOutcome.CORRECT -> if (key == p.tappedKey) ChoiceState.CORRECT else ChoiceState.IDLE
            QuizOutcome.TRY_AGAIN -> if (key == p.tappedKey) ChoiceState.WRONG else ChoiceState.IDLE
            QuizOutcome.REVEAL -> when (key) {
                p.correctKey -> ChoiceState.REVEALED
                p.tappedKey -> ChoiceState.WRONG
                else -> ChoiceState.DIMMED
            }
        }
    }

    /**
     * Records a tap on [tappedKey] when [correctKey] is right. [praise] is said for a right answer
     * ("Yes! Five!"); [correctName] is said when the answer has to be shown ("This one is five").
     */
    fun answer(tappedKey: String, correctKey: String, praise: Speech, correctName: String) {
        if (busy || progress.isFinished(rules)) return
        val outcome = progress.outcomeOf(tappedKey == correctKey, rules)
        progress = progress.after(outcome)
        pending = Pending(tappedKey, correctKey, outcome)
        val speaker = feedback?.speaker
        when (outcome) {
            QuizOutcome.CORRECT -> {
                onReward(rewardPerCorrect)
                feedback?.correct()
                speaker?.say(praise)
            }
            QuizOutcome.TRY_AGAIN -> {
                feedback?.wrong()
                speaker?.say(EnglishVoiceScript.say(EnglishVoiceScript.TRY_AGAIN))
            }
            QuizOutcome.REVEAL -> {
                feedback?.wrong()
                speaker?.say(EnglishVoiceScript.thisOneIs(correctName))
            }
        }
    }

    /** Waits while the last tap is shown, then moves on. Call from a LaunchedEffect on [pendingKey]. */
    suspend fun settle() {
        val p = pending ?: return
        delay(
            when (p.outcome) {
                QuizOutcome.CORRECT -> 1000L
                QuizOutcome.TRY_AGAIN -> 700L
                QuizOutcome.REVEAL -> 2200L // long enough to look at the right answer
            }
        )
        if (p.outcome != QuizOutcome.TRY_AGAIN) {
            if (progress.celebrates(p.outcome, rules)) celebrating = true
            else if (!progress.isFinished(rules)) round++
        }
        pending = null
    }

    /** What the tap being shown turned out to be, or null between taps (drives the mascot). */
    val shownOutcome: QuizOutcome? get() = pending?.outcome

    /** Identity of the tap being shown, for keying the [settle] effect. */
    val pendingKey: Any? get() = pending

    fun endCelebration() {
        if (!celebrating) return
        celebrating = false
        round++
    }

    /** Starts a fresh session of the same quiz. */
    fun playAgain() {
        progress = QuizProgress()
        pending = null
        celebrating = false
        round++
    }

    companion object {
        internal fun saver(rules: QuizRules) = listSaver<QuizSession, Int>(
            save = { listOf(it.progress.asked, it.progress.correct, it.round) },
            restore = { QuizSession(rules, QuizProgress(asked = it[0], correct = it[1]), it[2]) },
        )
    }
}

/**
 * A [QuizSession] that survives rotation. [onReward] and [feedback] are kept current on each
 * recomposition, so callers can pass lambdas freely.
 */
@Composable
fun rememberQuizSession(
    feedback: PlayFeedback?,
    onReward: (Int) -> Unit,
    rewardPerCorrect: Int = 3,
    rules: QuizRules = remember { QuizRules() },
): QuizSession {
    val session = rememberSaveable(saver = QuizSession.saver(rules)) {
        QuizSession(rules, QuizProgress(), 0)
    }
    val reward by rememberUpdatedState(onReward)
    session.feedback = feedback
    session.onReward = { reward(it) }
    session.rewardPerCorrect = rewardPerCorrect
    return session
}
