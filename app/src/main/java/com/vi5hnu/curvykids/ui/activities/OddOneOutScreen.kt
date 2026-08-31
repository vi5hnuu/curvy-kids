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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.GAME_PICTURES
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarCount
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.ui.components.SvgArt
import kotlinx.coroutines.delay

// Separate picture pools used for CATEGORY rounds so kids can spot animal vs food.
private val ANIMAL_PICTURES = listOf(
    KidAssets.animal("dog"), KidAssets.animal("cat"), KidAssets.animal("lion"),
    KidAssets.animal("cow"), KidAssets.animal("frog"), KidAssets.bird("duck"),
    KidAssets.animal("pig"), KidAssets.bird("owl"), KidAssets.bird("penguin"),
    KidAssets.animal("fox"), KidAssets.animal("panda"), KidAssets.animal("koala"),
)
private val FOOD_PICTURES = listOf(
    KidAssets.fruit("apple"), KidAssets.fruit("strawberry"), KidAssets.fruit("banana"),
    KidAssets.fruit("grapes"), KidAssets.fruit("watermelon"), KidAssets.fruit("orange"),
    KidAssets.fruit("cherries"), KidAssets.vegetable("carrot"), KidAssets.vegetable("corn"),
    KidAssets.food("donut"),
)

private enum class OddType { IDENTITY, CATEGORY, SIZE }

/**
 * All per-round state computed once.
 * @param tiles      Four svg strings (one per card).
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
            // Three cards show the same svg; one shows a different one.
            val (majority, odd) = GAME_PICTURES.shuffled().take(2)
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
            val majorityPool = if (animalFirst) ANIMAL_PICTURES else FOOD_PICTURES
            val oddPool = if (animalFirst) FOOD_PICTURES else ANIMAL_PICTURES
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
            // All four cards show the same svg; the odd one renders much smaller.
            val svg = GAME_PICTURES.random()
            val pos = (0 until 4).random()
            OddState(
                tiles = List(4) { svg },
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
    var score by rememberSaveable { mutableIntStateOf(0) }
    var round by rememberSaveable { mutableIntStateOf(0) }
    // Wrong answers on the current question, so a child who cannot get it is
    // moved on instead of being left tapping forever.
    var misses by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<Int, Boolean>?>(null) } // index → correct?
    var showCelebrate by rememberSaveable { mutableStateOf(false) }

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
            misses += 1
            feedback?.wrong()
            feedback?.speaker?.speak(
                if (misses >= MISSES_BEFORE_MOVING_ON) "That one is tricky. Let's try another."
                else "Try again"
            )
        }
    }

    LaunchedEffect(round) { misses = 0 }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(700)
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
                        modifier = Modifier.size(48.dp),
                    ) {
                        KidIconImage(KidIcon.Speaker, Modifier.size(20.dp))
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
                    rowItems.forEachIndexed { colIdx, svg ->
                        val i = rowIdx * 2 + colIdx
                        val flashOk = flash?.first == i && flash?.second == true
                        val flashBad = flash?.first == i && flash?.second == false
                        // SIZE rounds: the odd tile is rendered small so kids can spot it visually.
                        val tileSize = if (state.oddIsSmall && i == state.oddPos) 44.dp else 84.dp
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
                                SvgArt(svg, modifier = Modifier.size(tileSize))
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

/** Wrong answers tolerated before the question is changed for the child. */
private const val MISSES_BEFORE_MOVING_ON = 3
