package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.OPPOSITES
import com.vi5hnu.curvykids.data.content.OppositePair
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.components.SvgBadge
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarCount
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.SvgArt
import kotlinx.coroutines.delay

/** One option in the opposite quiz — could be word1 or word2 from any pair. */
private data class OppositeChoice(val word: String, val svg: String, val isCorrect: Boolean)

private data class OppositeSpec(
    val prompt: OppositePair,
    val showWord1: Boolean,      // which side of the pair is shown as the prompt
    val choices: List<OppositeChoice>,
)

private fun buildOppositeSpec(round: Int): OppositeSpec {
    val pair = OPPOSITES[round % OPPOSITES.size]
    val showWord1 = round % 2 == 0
    val correctWord = if (showWord1) pair.word2 else pair.word1
    val correctSvg = if (showWord1) pair.svg2 else pair.svg1

    // Distractors from other pairs — pick the "opposite" words from random pairs
    val distractors = OPPOSITES
        .filter { it != pair }
        .shuffled()
        .take(3)
        .map { other ->
            val useWord1 = listOf(true, false).random()
            OppositeChoice(
                if (useWord1) other.word1 else other.word2,
                if (useWord1) other.svg1 else other.svg2,
                isCorrect = false,
            )
        }
    val correct = OppositeChoice(correctWord, correctSvg, isCorrect = true)
    return OppositeSpec(pair, showWord1, (distractors + correct).shuffled())
}

/** Opposites — see a word, tap its opposite. */
@Composable
fun OppositeScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by rememberSaveable { mutableIntStateOf(0) }
    var round by rememberSaveable { mutableIntStateOf(0) }
    // Wrong answers on the current question, so a child who cannot get it is
    // moved on instead of being left tapping forever.
    var misses by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showCelebrate by rememberSaveable { mutableStateOf(false) }

    val spec = remember(round) { buildOppositeSpec(round) }
    val promptWord = if (spec.showWord1) spec.prompt.word1 else spec.prompt.word2
    val promptSvg = if (spec.showWord1) spec.prompt.svg1 else spec.prompt.svg2

    fun speakPrompt() = feedback?.speaker?.speak("What is the opposite of $promptWord?")

    LaunchedEffect(round) { speakPrompt() }
    LaunchedEffect(round) { misses = 0 }

    fun choose(choice: OppositeChoice) {
        if (flash != null) return
        flash = choice.word to choice.isCorrect
        if (choice.isCorrect) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! ${choice.word}!")
        } else {
            misses += 1
            feedback?.wrong()
            feedback?.speaker?.speak(
                if (misses >= MISSES_BEFORE_MOVING_ON) "That one is tricky. Let's try another."
                else "Try again"
            )
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(750)
            if (flash?.second == true) {
                if (score > 0 && score % 5 == 0) showCelebrate = true else round++
            } else if (misses >= MISSES_BEFORE_MOVING_ON) {
                round++
            }
            flash = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 24.dp),
        ) {
            ScreenHeader(
                title = topic.title,
                color = topic.color,
                onBack = onBack,
                trailing = {
                    Pill {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            StarCount(score)
                        }
                    }
                },
            )
            Spacer(Modifier.height(16.dp))

            // Prompt card showing the word to find the opposite of
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        "WHAT IS THE OPPOSITE?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = InkSoft,
                    )
                    Spacer(Modifier.height(12.dp))
                    SvgArt(promptSvg, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(
                        promptWord,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(12.dp))
                    Chip(onClick = { speakPrompt() }, modifier = Modifier.size(48.dp)) {
                        KidIconImage(KidIcon.Speaker, Modifier.size(20.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 2×2 choice grid
            spec.choices.chunked(2).forEach { pair ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                ) {
                    pair.forEach { choice ->
                        val flashOk = flash?.first == choice.word && flash?.second == true
                        val flashBad = flash?.first == choice.word && flash?.second == false
                        Surface(
                            onClick = { choose(choice) },
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .then(
                                    when {
                                        flashOk -> Modifier.border(4.dp, Green, RoundedCornerShape(24.dp))
                                        flashBad -> Modifier.border(4.dp, Color(0xFFFF6B6B), RoundedCornerShape(24.dp))
                                        else -> Modifier
                                    }
                                ),
                            shape = RoundedCornerShape(24.dp),
                            color = Color.White,
                            shadowElevation = 4.dp,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    SvgArt(choice.svg, modifier = Modifier.size(34.dp))
                                    Text(
                                        choice.word,
                                        fontFamily = FontDisplay,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF2B3A4A),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Opposite Expert!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}

/** Wrong answers tolerated before the question is changed for the child. */
private const val MISSES_BEFORE_MOVING_ON = 3
