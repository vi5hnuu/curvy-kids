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
import com.vi5hnu.curvykids.data.content.RHYME_GROUPS
import com.vi5hnu.curvykids.data.content.RhymeGroup
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.ChoiceArt
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.theme.FontDisplay

private data class RhymeChoice(val word: String, val svg: AssetPath)

private data class RhymeSpec(val group: RhymeGroup, val correct: RhymeChoice, val choices: List<RhymeChoice>)

/** One rhyme from [round]'s family, plus two words from other families that don't rhyme with it. */
private fun buildRhymeSpec(round: Int): RhymeSpec {
    val group = RHYME_GROUPS[round % RHYME_GROUPS.size]
    val correct = group.rhymes.random().let { RhymeChoice(it.first, it.second) }
    val distractors = RHYME_GROUPS.filter { it != group }.flatMap { it.rhymes }.shuffled().take(2)
        .map { RhymeChoice(it.first, it.second) }
    return RhymeSpec(group, correct, (distractors + correct).shuffled())
}

/** Rhyming — hear a word and the three choices, tap the one that rhymes. */
@Composable
fun RhymingScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward)
    val offset = remember { RHYME_GROUPS.indices.random() }
    val spec = remember(session.round) { buildRhymeSpec(session.round + offset) }
    val word = spec.group.word.lowercase()

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = spec.choices,
        correct = spec.correct,
        keyOf = { it.word },
        nameOf = { it.word.lowercase() },
        praiseFor = { "Yes! $word and ${it.word.lowercase()} rhyme!" },
        // The choices are said as part of the question: a child who can't read the labels
        // still has to hear each word to know whether it rhymes.
        speakPrompt = { flush ->
            val options = spec.choices.joinToString(", ") { it.word.lowercase() }
            feedback?.speaker?.speak("Which word rhymes with $word? $options.", flush = flush)
        },
        celebrateTitle = "Rhyme Master!",
        promptLabel = "WHICH WORD RHYMES?",
        prompt = {
            SvgArt(spec.group.svg, modifier = Modifier.size(96.dp))
            Text(spec.group.word, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp,
                color = topic.color)
        },
        choiceContent = { ChoiceArt(it.svg, it.word) },
    )
}
