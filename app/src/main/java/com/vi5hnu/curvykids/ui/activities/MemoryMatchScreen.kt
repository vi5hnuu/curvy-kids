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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft

private const val PAIRS = 6        // 12 cards in a 4×3 grid
private const val COLS = 4

/** Memory Match — flip cards two at a time to find matching pairs. */
@Composable
fun MemoryMatchScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var round by remember { mutableIntStateOf(0) }
    val board = remember(round) {
        val picks = GAME_EMOJIS.shuffled().take(PAIRS)
        (picks + picks).shuffled()
    }
    val matched = remember(round) { mutableStateListOf<Int>() }
    var first by remember(round) { mutableStateOf<Int?>(null) }
    var second by remember(round) { mutableStateOf<Int?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    // Evaluate a pair once two cards are face-up.
    LaunchedEffect(first, second) {
        val a = first; val b = second
        if (a != null && b != null) {
            if (board[a] == board[b]) {
                kotlinx.coroutines.delay(350)
                matched.add(a); matched.add(b)
                feedback?.correct()
                feedback?.speaker?.speak("Match!")
                onReward(3)
                first = null; second = null
                if (matched.size == board.size) showCelebrate = true
            } else {
                kotlinx.coroutines.delay(850)
                feedback?.wrong()
                first = null; second = null
            }
        }
    }

    fun onCard(i: Int) {
        if (first != null && second != null) return        // pair resolving
        if (matched.contains(i) || i == first) return
        if (first == null) first = i else second = i
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
                            Text("${matched.size / 2}/$PAIRS", fontFamily = FontDisplay, fontSize = 16.sp, color = topic.color)
                        }
                    }
                },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tap two cards to find a pair!",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = InkSoft,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            Spacer(Modifier.height(14.dp))

            board.chunked(COLS).forEachIndexed { rowIdx, rowCards ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                ) {
                    rowCards.forEachIndexed { colIdx, emoji ->
                        val i = rowIdx * COLS + colIdx
                        val isUp = matched.contains(i) || i == first || i == second
                        val isMatched = matched.contains(i)
                        Surface(
                            onClick = { onCard(i) },
                            modifier = Modifier.weight(1f).aspectRatio(1f),
                            shape = RoundedCornerShape(18.dp),
                            color = when {
                                isMatched -> topic.tint
                                isUp -> Color.White
                                else -> topic.color
                            },
                            shadowElevation = 3.dp,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isUp) {
                                    Text(emoji, fontSize = 32.sp)
                                } else {
                                    Text("?", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, color = Color.White)
                                }
                            }
                        }
                    }
                    repeat(COLS - rowCards.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "All matched!",
                sub = "You found every pair!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}
