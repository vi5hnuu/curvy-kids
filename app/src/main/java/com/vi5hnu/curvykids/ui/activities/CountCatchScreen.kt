package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Teal
import kotlinx.coroutines.delay

private const val COLS = 3

/** Count & Tap — tap exactly the requested number of items. Reinforces counting. */
@Composable
fun CountCatchScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var tapped by remember(round) { mutableStateOf(setOf<Int>()) }
    var solved by remember(round) { mutableStateOf(false) }
    var showCelebrate by remember { mutableStateOf(false) }

    val spec = remember(round) {
        val target = (2..6).random()
        val pool = (target + (2..4).random()).coerceAtMost(9)
        Triple(target, pool, GAME_EMOJIS.random())
    }
    val target = spec.first
    val pool = spec.second
    val emoji = spec.third

    fun speakPrompt() = feedback?.speaker?.speak("Tap $target")

    LaunchedEffect(round) { speakPrompt() }

    fun onItem(i: Int) {
        if (solved || tapped.contains(i)) return
        val now = tapped + i
        tapped = now
        if (now.size < target) {
            feedback?.speaker?.speak(now.size.toString())
        } else {
            // reached the target
            solved = true
            score += 1
            onReward(4)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! $target")
        }
    }

    LaunchedEffect(solved) {
        if (solved) {
            delay(900)
            if (score > 0 && score % 5 == 0) showCelebrate = true else round++
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
                    modifier = Modifier.padding(20.dp),
                ) {
                    Text("TAP THIS MANY", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = InkSoft)
                    Text("$target", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 52.sp, color = topic.color)
                    Text("Tapped ${tapped.size.coerceAtMost(target)} / $target", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkSoft)
                    Spacer(Modifier.height(8.dp))
                    Chip(onClick = { speakPrompt() }, modifier = Modifier.size(46.dp)) { Text("🔊", fontSize = 20.sp) }
                }
            }

            Spacer(Modifier.height(18.dp))

            (0 until pool).toList().chunked(COLS).forEach { rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                ) {
                    rowItems.forEach { i ->
                        val isTapped = tapped.contains(i)
                        Surface(
                            onClick = { onItem(i) },
                            modifier = Modifier.weight(1f).aspectRatio(1f),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isTapped) topic.tint else Color.White,
                            shadowElevation = 3.dp,
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(emoji, fontSize = 40.sp)
                                if (isTapped) {
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = Teal,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .size(22.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("✓", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    repeat(COLS - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(title = "Counting Star!", sub = "$score rounds!", stars = 3, onDone = { showCelebrate = false; round++ })
        }
    }
}
