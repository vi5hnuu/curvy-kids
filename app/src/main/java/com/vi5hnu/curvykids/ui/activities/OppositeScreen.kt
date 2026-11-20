package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.OPPOSITES
import com.vi5hnu.curvykids.data.content.OppositePair
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.ChoiceArt
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/** One option in the opposite quiz — either word of any pair. */
private data class OppositeChoice(val word: String, val svg: AssetPath)

private data class OppositeSpec(
    val promptWord: String,
    val promptSvg: AssetPath,
    val correct: OppositeChoice,
    val choices: List<OppositeChoice>,
)

/**
 * Question [round] walks through the pairs, alternating which side is asked. Distractors come
 * from other pairs only, so neither the prompt word nor its opposite appears twice.
 */
private fun buildOppositeSpec(round: Int): OppositeSpec {
    val pair: OppositePair = OPPOSITES[round % OPPOSITES.size]
    val askFirst = (round / OPPOSITES.size) % 2 == 0
    val correct = if (askFirst) OppositeChoice(pair.word2, pair.svg2) else OppositeChoice(pair.word1, pair.svg1)
    val distractors = OPPOSITES.filter { it != pair }.shuffled().take(3).map { other ->
        if (listOf(true, false).random()) OppositeChoice(other.word1, other.svg1)
        else OppositeChoice(other.word2, other.svg2)
    }
    return OppositeSpec(
        promptWord = if (askFirst) pair.word1 else pair.word2,
        promptSvg = if (askFirst) pair.svg1 else pair.svg2,
        correct = correct,
        choices = (distractors + correct).shuffled(),
    )
}

/** Opposites — see a word and its picture, tap its opposite. */
@Composable
fun OppositeScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward)
    // Start somewhere different each visit so the same pairs don't always come first.
    val offset = remember { OPPOSITES.indices.random() }
    val spec = remember(session.round) { buildOppositeSpec(session.round + offset) }

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = spec.choices,
        correct = spec.correct,
        keyOf = { it.word },
        nameOf = { it.word },
        speakPrompt = { flush ->
            feedback?.speaker?.say(EnglishVoiceScript.say(EnglishVoiceScript.opposite(spec.promptWord)), flush = flush)
        },
        celebrateTitle = "Opposite Expert!",
        promptLabel = "WHAT IS THE OPPOSITE?",
        prompt = {
            SvgArt(spec.promptSvg, modifier = Modifier.size(96.dp))
            Text(
                spec.promptWord,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = topic.color,
            )
        },
        choiceContent = { ChoiceArt(it.svg, it.word) },
    )
}
