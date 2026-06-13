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

// Separate emoji pools used for CATEGORY rounds so kids can spot animal vs food.
private val ANIMAL_EMOJIS = listOf(
    "🐶", "🐱", "🦁", "🐮", "🐸", "🦆", "🐷", "🦉", "🐧", "🦊", "🐼", "🐨",
)
private val FOOD_EMOJIS = listOf(
    "🍎", "🍓", "🍌", "🍇", "🍉", "🍊", "🍒", "🥕", "🌽", "🍩",
)

private enum class OddType { IDENTITY, CATEGORY, SIZE }

/**
 * All per-round state computed once.
 * @param tiles      Four emoji strings (one per card).
 * @param oddPos     Index of the card that is "different."
 * @param prompt     Header text and spoken prompt for this round type.
 * @param oddIsSmall When true (SIZE rounds), the odd card renders at a smaller font.
 */
private data class OddState(
    val tiles: List<String>,
    val oddPos: Int,
    val prompt: String,
    val oddIsSmall: Boolean = false,
)

private fun buildOddState(round: Int): OddState {
    return when (OddType.values()[round % 3]) {
        OddType.IDENTITY -> {
            // Three cards show the same emoji; one shows a different one.
            val (majority, odd) = GAME_EMOJIS.shuffled().take(2)
            val pos = (0 until 4).random()
            OddState(
                tiles = List(4) { if (it == pos) odd else majority },
                oddPos = pos,
                prompt = "Which one is different?",
            )
        }
        OddType.CATEGORY -> {
            // Three cards from one category, one card from the other.
            val animalFirst = listOf(true, false).random()
            val majorityPool = if (animalFirst) ANIMAL_EMOJIS else FOOD_EMOJIS
            val oddPool = if (animalFirst) FOOD_EMOJIS else ANIMAL_EMOJIS
            val majority = majorityPool.shuffled().take(3)
            val odd = oddPool.random()
            val pos = (0 until 4).random()
            val tiles = mutableListOf<String>()
            var mIdx = 0
            repeat(4) { i -> if (i == pos) tiles.add(odd) else tiles.add(majority[mIdx++]) }
            val categoryName = if (animalFirst) "an animal" else "food"
            OddState(
                tiles = tiles,
                oddPos = pos,
                prompt = "Which is NOT $categoryName?",
            )
        }
        OddType.SIZE -> {
            // All four cards show the same emoji; the odd one renders much smaller.
            val emoji = GAME_EMOJIS.random()
            val pos = (0 until 4).random()
            OddState(
                tiles = List(4) { emoji },
                oddPos = pos,
                prompt = "Tap the small one!",
                oddIsSmall = true,
            )
        }
    }
}

/** Odd One Out — four items; three share something in common, tap the odd one out. */
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

    val state = remember(round) { buildOddState(round) }

    LaunchedEffect(round) { feedback?.speaker?.speak(state.prompt) }

    fun choose(i: Int) {
        if (flash != null) return
        val ok = i == state.oddPos
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
                        text = "FIND THE ODD ONE OUT",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        state.prompt,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = InkSoft,
                    )
                    Spacer(Modifier.height(10.dp))
                    Chip(
                        onClick = { feedback?.speaker?.speak(state.prompt) },
                        modifier = Modifier.size(46.dp),
                    ) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            state.tiles.chunked(2).forEachIndexed { rowIdx, rowItems ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                ) {
                    rowItems.forEachIndexed { colIdx, emoji ->
                        val i = rowIdx * 2 + colIdx
                        val flashOk = flash?.first == i && flash?.second == true
                        val flashBad = flash?.first == i && flash?.second == false
                        // SIZE rounds: the odd tile is rendered small so kids can spot it visually.
                        val fontSize = if (state.oddIsSmall && i == state.oddPos) 32.sp else 64.sp
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
                                Text(emoji, fontSize = fontSize)
                            }
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Sharp Eyes!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}
