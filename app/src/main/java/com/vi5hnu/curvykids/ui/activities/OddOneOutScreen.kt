package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.border
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
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import kotlinx.coroutines.delay

private const val TILES = 4 // 2×2

/** Odd One Out — four items, three the same; tap the different one. */
@Composable
fun OddOneOutScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<Int, Boolean>?>(null) } // index → correct?
    var showCelebrate by remember { mutableStateOf(false) }

    val state = remember(round) {
        val two = GAME_EMOJIS.shuffled().take(2)
        val majority = two[0]; val odd = two[1]
        val oddPos = (0 until TILES).random()
        val tiles = (0 until TILES).map { if (it == oddPos) odd else majority }
        Triple(tiles, oddPos, odd)
    }
    val tiles = state.first
    val oddPos = state.second

    LaunchedEffect(Unit) { feedback?.speaker?.speak("Which one is different?") }

    fun choose(i: Int) {
        if (flash != null) return
        val ok = i == oddPos
        flash = i to ok
        if (ok) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! That's different!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(700)
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
                    modifier = Modifier.padding(20.dp),
                ) {
                    Text(
                        text = "FIND THE DIFFERENT ONE",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Three are the same!", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkSoft)
                    Spacer(Modifier.height(10.dp))
                    Chip(onClick = { feedback?.speaker?.speak("Which one is different?") }, modifier = Modifier.size(46.dp)) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            tiles.chunked(2).forEachIndexed { rowIdx, rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                ) {
                    rowItems.forEachIndexed { colIdx, emoji ->
                        val i = rowIdx * 2 + colIdx
                        val flashOk = flash?.first == i && flash?.second == true
                        val flashBad = flash?.first == i && flash?.second == false
                        Surface(
                            onClick = { choose(i) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
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
                                Text(emoji, fontSize = 64.sp)
                            }
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(title = "Sharp Eyes!", sub = "$score correct!", stars = 3, onDone = { showCelebrate = false; round++ })
        }
    }
}
