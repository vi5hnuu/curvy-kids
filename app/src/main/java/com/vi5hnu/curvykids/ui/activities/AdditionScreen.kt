package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

/**
 * Score-based difficulty:
 *  Level 0 → sums ≤ 5   (e.g. 1+2)
 *  Level 1 → sums ≤ 8   (e.g. 3+5)
 *  Level 2 → sums ≤ 10  (e.g. 4+6)
 */
private data class AddSpec(val a: Int, val b: Int, val svg: AssetPath, val choices: List<Int>)

private fun buildAddSpec(level: Int): AddSpec {
    val maxSum = when (level) { 0 -> 5; 1 -> 8; else -> 10 }
    val a = (1..maxSum / 2).random()
    val b = (1..(maxSum - a)).random()
    val answer = a + b
    // Two nearby distractors that are distinct from answer and ≥ 1
    val distractors = mutableSetOf<Int>()
    while (distractors.size < 2) {
        val d = (answer - 2..answer + 2).random().coerceAtLeast(1)
        if (d != answer) distractors.add(d)
    }
    return AddSpec(a, b, GAME_PICTURES.random(), (distractors.toList() + answer).shuffled())
}

/** Addition — two groups of pictures to count together; sums grow as the child gets them right. */
@Composable
fun AdditionScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward, rewardPerCorrect = 4)
    val level = (session.progress.correct / 4).coerceAtMost(2)
    val spec = remember(session.round) { buildAddSpec(level) }
    val answer = spec.a + spec.b

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = spec.choices,
        correct = answer,
        keyOf = { it.toString() },
        nameOf = { it.toString() },
        speakPrompt = { flush -> feedback?.speaker?.speak("${spec.a} plus ${spec.b}. How many in all?", flush = flush) },
        celebrateTitle = "Math Whiz!",
        promptLabel = "HOW MANY IN ALL?",
        prompt = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                CountingPictures(spec.a, spec.svg, Modifier.weight(1f, fill = false), perRow = 3)
                Sign("+", topic.color)
                CountingPictures(spec.b, spec.svg, Modifier.weight(1f, fill = false), perRow = 3)
            }
            Spacer(Modifier.height(8.dp))
            Text("${spec.a} + ${spec.b} = ?", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp, color = topic.color)
        },
        choiceContent = { ChoiceText(it.toString(), topic.color) },
    )
}

@Composable
private fun Sign(text: String, color: androidx.compose.ui.graphics.Color) {
    Spacer(Modifier.width(6.dp))
    Text(text, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, color = color)
    Spacer(Modifier.width(6.dp))
}
