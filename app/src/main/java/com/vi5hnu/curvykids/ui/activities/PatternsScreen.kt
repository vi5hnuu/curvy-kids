package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
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
import com.vi5hnu.curvykids.ui.activities.components.ChoiceArt
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/**
 * The repeating units, as indexes into the pictures picked for a question: AB, AABB, ABB, ABC.
 * Simplest first; later questions use the longer ones.
 */
private val UNITS = listOf(listOf(0, 1), listOf(0, 0, 1, 1), listOf(0, 1, 1), listOf(0, 1, 2))

internal data class PatternSpec(
    val shown: List<AssetPath>,
    val answer: AssetPath,
    val choices: List<AssetPath>,
)

/**
 * Builds a question from a repeating [unit]. At least one and a half repeats are shown, so the
 * next item follows from what's on screen and has exactly one right answer.
 */
internal fun buildPatternSpec(unit: List<Int>, pictures: List<AssetPath>): PatternSpec {
    val shownCount = unit.size + (unit.size + 1) / 2 + if (unit.size == 2) 1 else 0
    val sequence = List(shownCount + 1) { pictures[unit[it % unit.size]] }
    val answer = sequence.last()
    val used = unit.distinct().map { pictures[it] }
    // The other picture(s) in the pattern are the most tempting wrong answers.
    val distractors = (used.filter { it != answer } + pictures.drop(3)).distinct().take(2)
    return PatternSpec(sequence.dropLast(1), answer, (distractors + answer).shuffled())
}

/** Patterns — look at the row, tap what comes next. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PatternsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward)
    val spec = remember(session.round) {
        val unit = UNITS[(session.progress.correct / 3).coerceAtMost(UNITS.lastIndex)]
        buildPatternSpec(unit, GAME_PICTURES.shuffled())
    }

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = spec.choices,
        correct = spec.answer,
        keyOf = { it.path },
        nameOf = { EnglishVoiceScript.pictureName(it) },
        praiseFor = { EnglishVoiceScript.say("Yes! That comes next!") },
        speakPrompt = { flush -> feedback?.speaker?.say(EnglishVoiceScript.say("What comes next?"), flush = flush) },
        celebrateTitle = "Pattern Pro!",
        promptLabel = "WHAT COMES NEXT?",
        prompt = {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center,
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                spec.shown.forEach { SvgArt(it, modifier = Modifier.size(50.dp)) }
                Text("?", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp,
                    color = topic.color)
            }
        },
        choiceContent = { ChoiceArt(it) },
    )
}
