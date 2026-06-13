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
import kotlinx.coroutines.delay

/** One option in the opposite quiz — could be word1 or word2 from any pair. */
private data class OppositeChoice(val word: String, val emoji: String, val isCorrect: Boolean)

private data class OppositeSpec(
    val prompt: OppositePair,
    val showWord1: Boolean,      // which side of the pair is shown as the prompt
    val choices: List<OppositeChoice>,
)

private fun buildOppositeSpec(round: Int): OppositeSpec {
    val pair = OPPOSITES[round % OPPOSITES.size]
    val showWord1 = round % 2 == 0
    val correctWord = if (showWord1) pair.word2 else pair.word1
    val correctEmoji = if (showWord1) pair.emoji2 else pair.emoji1

    // Distractors from other pairs — pick the "opposite" words from random pairs
    val distractors = OPPOSITES
        .filter { it != pair }
        .shuffled()
        .take(3)
        .map { other ->
            val useWord1 = listOf(true, false).random()
            OppositeChoice(
                if (useWord1) other.word1 else other.word2,
                if (useWord1) other.emoji1 else other.emoji2,
                isCorrect = false,
            )
        }
    val correct = OppositeChoice(correctWord, correctEmoji, isCorrect = true)
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
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    val spec = remember(round) { buildOppositeSpec(round) }
    val promptWord = if (spec.showWord1) spec.prompt.word1 else spec.prompt.word2
    val promptEmoji = if (spec.showWord1) spec.prompt.emoji1 else spec.prompt.emoji2

    fun speakPrompt() = feedback?.speaker?.speak("What is the opposite of $promptWord?")

    LaunchedEffect(round) { speakPrompt() }

    fun choose(choice: OppositeChoice) {
        if (flash != null) return
        flash = choice.word to choice.isCorrect
        if (choice.isCorrect) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! ${choice.word}!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(750)
            if (flash?.second == true) {
                if (score > 0 && score % 5 == 0) showCelebrate = true else round++
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
                            Text("⭐ $score", fontFamily = FontDisplay, fontSize = 16.sp, color = Color(0xFFF2A93B))
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
                    Text(promptEmoji, fontSize = 56.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        promptWord,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(12.dp))
                    Chip(onClick = { speakPrompt() }, modifier = Modifier.size(46.dp)) {
                        Text("🔊", fontSize = 20.sp)
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
                                    Text(choice.emoji, fontSize = 30.sp)
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
