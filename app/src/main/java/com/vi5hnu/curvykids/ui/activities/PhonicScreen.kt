package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.LetterPhonic
import com.vi5hnu.curvykids.data.content.PHONICS_DATA
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.ChoiceText
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

internal data class PhonicSpec(val target: LetterPhonic, val choices: List<LetterPhonic>)

/**
 * A letter to find and two others. Letters that make the same sound as the target (C and K) are
 * never offered with it, so there's only ever one right answer. Letters not yet asked this
 * session ([asked]) come first.
 */
internal fun buildPhonicSpec(asked: Set<String>): PhonicSpec {
    val pool = PHONICS_DATA.filter { it.letter !in asked }.ifEmpty { PHONICS_DATA }
    val target = pool.random()
    val distractors = PHONICS_DATA
        .filter { it.soundGroup != target.soundGroup }
        .shuffled()
        .distinctBy { it.soundGroup }
        .take(2)
    return PhonicSpec(target, (distractors + target).shuffled())
}

/**
 * Letter Sounds — hear a sound, tap the letter that makes it.
 *
 * Built for children who can't read yet: the question is entirely spoken (the recorded sound,
 * when there is one), the picture is the clue, and the answers are big letters.
 */
@Composable
fun PhonicScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward)
    val asked = remember { mutableSetOf<String>() }
    val spec = remember(session.round) {
        buildPhonicSpec(asked).also { asked.add(it.target.letter) }
    }
    val target = spec.target
    val hint = EnglishVoiceScript.soundHint(target)

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = spec.choices,
        correct = target,
        keyOf = { it.letter },
        nameOf = { it.letter },
        praiseFor = { EnglishVoiceScript.yesSound(target) },
        speakPrompt = { flush ->
            val sp = feedback?.speaker
            sp?.say(EnglishVoiceScript.say("Which letter makes this sound?"), flush = flush)
            sp?.speak(target.sound, flush = false, utteranceId = "phoneme", clip = target.clip)
            sp?.say(EnglishVoiceScript.say(hint), flush = false)
        },
        celebrateTitle = "Sound Expert!",
        promptLabel = "WHICH LETTER MAKES THIS SOUND?",
        prompt = { SvgArt(target.svg, modifier = Modifier.size(110.dp), contentDescription = target.example) },
        choiceContent = { ChoiceText(it.letter, topic.color) },
    )
}
