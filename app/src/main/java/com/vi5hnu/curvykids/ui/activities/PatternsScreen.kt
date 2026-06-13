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
import com.vi5hnu.curvykids.data.content.GAME_EMOJIS
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

/** Pattern type cycling per round. */
private enum class PatternType { ABAB, AABB, ABBA, ABBC }

private data class PatternSpec(
    val shown: List<String>,   // the visible portion of the sequence
    val answer: String,        // the next item (correct choice)
    val choices: List<String>, // answer + 2 distractors, shuffled
)

private fun buildPatternSpec(round: Int): PatternSpec {
    val type = PatternType.values()[round % 4]
    val emojis = GAME_EMOJIS.shuffled()
    val a = emojis[0]; val b = emojis[1]; val c = emojis[2]
    // Build a long sequence then slice off the last item as the answer
    val sequence: List<String> = when (type) {
        PatternType.ABAB -> listOf(a, b, a, b, a)  // show 4, answer is b
        PatternType.AABB -> listOf(a, a, b, b, a)  // show 4, answer is a
        PatternType.ABBA -> listOf(a, b, b, a, b)  // show 4, answer is b
        PatternType.ABBC -> listOf(a, b, b, c, a)  // show 4, answer is b
    }
    val shown = sequence.dropLast(1)
    val answer = sequence.last()
    // Distractors: two other emojis from the pool that aren't the answer
    val distractors = emojis.drop(3).take(2)
    return PatternSpec(shown, answer, (distractors + answer).shuffled())
}

/** Patterns — complete the sequence by tapping what comes next. */
@Composable
fun PatternsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    val spec = remember(round) { buildPatternSpec(round) }

    fun speakPrompt() = feedback?.speaker?.speak("What comes next?")

    LaunchedEffect(round) { speakPrompt() }

    fun choose(emoji: String) {
        if (flash != null) return
        val ok = emoji == spec.answer
        flash = emoji to ok
        if (ok) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(800)
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

            // Sequence display card
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        "WHAT COMES NEXT?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = InkSoft,
                    )
                    Spacer(Modifier.height(14.dp))

                    // Show the pattern sequence with a "?" at the end
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        spec.shown.forEach { emoji ->
                            Text(emoji, fontSize = 36.sp, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                        Text(
                            "  ?",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            color = topic.color,
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Chip(onClick = { speakPrompt() }, modifier = Modifier.size(46.dp)) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // Answer choices
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                spec.choices.forEach { emoji ->
                    val flashOk = flash?.first == emoji && flash?.second == true
                    val flashBad = flash?.first == emoji && flash?.second == false
                    Surface(
                        onClick = { choose(emoji) },
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
                            Text(emoji, fontSize = 48.sp)
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Pattern Pro!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}
