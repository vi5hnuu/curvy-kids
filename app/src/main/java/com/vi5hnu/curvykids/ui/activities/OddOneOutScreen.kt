package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.GAME_PICTURES
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.SvgArt
import androidx.compose.ui.unit.dp

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
    val tiles: List<AssetPath>,
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
            val tiles = mutableListOf<AssetPath>()
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

/** Odd One Out — four pictures; three belong together, tap the one that doesn't. */
@Composable
fun OddOneOutScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val session = rememberQuizSession(feedback, onReward)
    val state = remember(session.round) { buildOddState(session.round) }

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = state.tiles.indices.toList(),
        correct = state.oddPos,
        keyOf = { it.toString() },
        nameOf = { "this one" },
        praiseFor = { "Yes! That's the different one!" },
        speakPrompt = { flush -> feedback?.speaker?.speak(state.prompt, flush = flush) },
        celebrateTitle = "Sharp Eyes!",
        promptLabel = "FIND THE ODD ONE OUT",
        prompt = { CurvyMascot(size = 72.dp, mood = CurvyMood.Happy) },
        columns = 2,
        choiceContent = { i ->
            val small = state.oddIsSmall && i == state.oddPos
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                SvgArt(state.tiles[i], modifier = Modifier.fillMaxSize(if (small) 0.45f else 0.9f))
            }
        },
    )
}
