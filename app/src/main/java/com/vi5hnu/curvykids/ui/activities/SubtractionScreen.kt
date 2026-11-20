package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.GAME_PICTURES
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.ChoiceText
import com.vi5hnu.curvykids.ui.activities.components.CountingPictures
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/**
 * Level-based range:
 *  Level 0 → total 3-5, take away 1-2
 *  Level 1 → total 4-7, take away 1-3
 *  Level 2 → total 5-10, take away 1-4
 */
private data class SubSpec(val total: Int, val remove: Int, val svg: AssetPath, val choices: List<Int>)

private fun buildSubSpec(level: Int): SubSpec {
    val (totalRange, removeMax) = when (level) {
        0 -> Pair(3..5, 2)
        1 -> Pair(4..7, 3)
        else -> Pair(5..10, 4)
    }
    val total = totalRange.random()
    val remove = (1..removeMax.coerceAtMost(total - 1)).random()
    val answer = total - remove
    val distractors = mutableSetOf<Int>()
    while (distractors.size < 2) {
        val d = (answer - 2..answer + 2).random().coerceAtLeast(0)
        if (d != answer) distractors.add(d)
    }
    return SubSpec(total, remove, GAME_PICTURES.random(), (distractors.toList() + answer).shuffled())
}

/** Subtraction — some pictures are crossed out; how many are left? */
@Composable
fun SubtractionScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward, rewardPerCorrect = 4)
    val level = (session.progress.correct / 4).coerceAtMost(2)
    val spec = remember(session.round) { buildSubSpec(level) }
    val answer = spec.total - spec.remove

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = spec.choices,
        correct = answer,
        keyOf = { it.toString() },
        nameOf = { it.toString() },
        praiseFor = { EnglishVoiceScript.yesLeft(it) },
        speakPrompt = { flush ->
            feedback?.speaker?.say(EnglishVoiceScript.subtraction(spec.total, spec.remove), flush = flush)
        },
        celebrateTitle = "Math Whiz!",
        promptLabel = "HOW MANY ARE LEFT?",
        prompt = {
            CountingPictures(spec.total, spec.svg, crossedOut = spec.remove)
            Spacer(Modifier.height(8.dp))
            Text("${spec.total} - ${spec.remove} = ?", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp, color = topic.color)
        },
        choiceContent = { ChoiceText(it.toString(), topic.color) },
    )
}
