package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.GAME_PICTURES
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.ChoiceText
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.SvgArt
import androidx.compose.ui.unit.dp

private enum class BigSmallMode { SIZE, NUMBER }

/**
 * All state that changes per round, computed once via remember(round).
 * SIZE mode: two svg at different display sizes.
 * NUMBER mode: two different numbers; ask which is bigger/smaller.
 */
private data class BigSmallSpec(
    val mode: BigSmallMode,
    val askBig: Boolean,
    /** True if the "bigger" side (svg OR larger number) is on the left. */
    val bigOnLeft: Boolean,
    val svg: AssetPath? = null,
    val numLeft: Int = 0,
    val numRight: Int = 0,
) {
    fun prompt() = if (askBig) {
        if (mode == BigSmallMode.SIZE) "WHICH IS BIGGER?" else "WHICH NUMBER IS BIGGER?"
    } else {
        if (mode == BigSmallMode.SIZE) "WHICH IS SMALLER?" else "WHICH NUMBER IS SMALLER?"
    }

    fun spokenPrompt() = if (askBig) "Which is bigger?" else "Which is smaller?"
}

private fun buildSpec(round: Int): BigSmallSpec {
    val askBig = listOf(true, false).random()
    // Alternate modes every 2 rounds: SIZE, SIZE, NUMBER, NUMBER, SIZE, SIZE, …
    return if (round % 4 < 2) {
        BigSmallSpec(
            mode = BigSmallMode.SIZE,
            askBig = askBig,
            bigOnLeft = listOf(true, false).random(),
            svg = GAME_PICTURES.random(),
        )
    } else {
        val nums = (1..9).toList().shuffled().take(2) // guaranteed distinct
        val leftNum = nums[0]; val rightNum = nums[1]
        BigSmallSpec(
            mode = BigSmallMode.NUMBER,
            askBig = askBig,
            bigOnLeft = leftNum > rightNum,
            numLeft = leftNum,
            numRight = rightNum,
        )
    }
}

/** Big or Small — compare two sizes of a picture, or two numbers, and tap the one asked for. */
@Composable
fun BigSmallScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward)
    val spec = remember(session.round) { buildSpec(session.round) }
    val correctSide = if (spec.askBig == spec.bigOnLeft) "left" else "right"
    fun numberOn(side: String) = if (side == "left") spec.numLeft else spec.numRight

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = listOf("left", "right"),
        correct = correctSide,
        keyOf = { it },
        nameOf = { side ->
            if (spec.mode == BigSmallMode.NUMBER) numberOn(side).toString()
            else if (spec.askBig) "the big one" else "the small one"
        },
        praiseFor = { "Yes!" },
        speakPrompt = { flush -> feedback?.speaker?.speak(spec.spokenPrompt(), flush = flush) },
        celebrateTitle = "Size Star!",
        promptLabel = spec.prompt(),
        prompt = { CurvyMascot(size = 72.dp, mood = CurvyMood.Happy) },
        columns = 2,
        choiceContent = { side ->
            if (spec.mode == BigSmallMode.NUMBER) {
                ChoiceText(numberOn(side).toString(), topic.color)
            } else {
                val big = (side == "left") == spec.bigOnLeft
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    SvgArt(spec.svg, modifier = Modifier.fillMaxSize(if (big) 0.95f else 0.4f))
                }
            }
        },
    )
}
