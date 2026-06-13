package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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

/**
 * Level-based range:
 *  Level 0 → total 3-5, take away 1-2
 *  Level 1 → total 4-7, take away 1-3
 *  Level 2 → total 5-10, take away 1-4
 */
private data class SubSpec(val total: Int, val remove: Int, val emoji: String, val choices: List<Int>)

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
    return SubSpec(total, remove, GAME_EMOJIS.random(), (distractors.toList() + answer).shuffled())
}

/** Subtraction — show items with some crossed out; how many are left? */
@Composable
fun SubtractionScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<Int, Boolean>?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    val level = (score / 5).coerceAtMost(2)
    val spec = remember(round, level) { buildSubSpec(level) }
    val answer = spec.total - spec.remove

    fun speakPrompt() = feedback?.speaker?.speak("${spec.total} take away ${spec.remove} equals?")

    LaunchedEffect(round) { speakPrompt() }

    fun choose(n: Int) {
        if (flash != null) return
        val ok = n == answer
        flash = n to ok
        if (ok) {
            score += 1
            onReward(4)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! $answer left!")
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

            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        "HOW MANY ARE LEFT?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = InkSoft,
                    )
                    Spacer(Modifier.height(16.dp))

                    // Visual: first `remove` items are faded/struck, rest are normal
                    CrossedOutDots(total = spec.total, remove = spec.remove, emoji = spec.emoji)

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "${spec.total} - ${spec.remove} = ?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(10.dp))
                    Chip(onClick = { speakPrompt() }, modifier = Modifier.size(46.dp)) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                spec.choices.forEach { n ->
                    val flashOk = flash?.first == n && flash?.second == true
                    val flashBad = flash?.first == n && flash?.second == false
                    Surface(
                        onClick = { choose(n) },
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
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
                            Text(
                                "$n",
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 44.sp,
                                color = topic.color,
                            )
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Subtract Star!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}

/** Shows `total` emojis; first `remove` are faded with strikethrough to show "taken away." */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CrossedOutDots(total: Int, remove: Int, emoji: String) {
    FlowRow(
        maxItemsInEachRow = 5,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        (0 until total).forEach { i ->
            val crossed = i < remove
            Text(
                text = if (crossed) "❌" else emoji,
                fontSize = 26.sp,
                modifier = if (crossed) Modifier.alpha(0.5f) else Modifier,
                textDecoration = if (crossed) TextDecoration.LineThrough else TextDecoration.None,
            )
        }
    }
}
