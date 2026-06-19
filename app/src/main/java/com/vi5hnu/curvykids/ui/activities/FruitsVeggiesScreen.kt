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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.FruitItem
import com.vi5hnu.curvykids.data.content.FRUITS_VEGGIES
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.SegmentedTabs
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.SvgBadge
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import kotlinx.coroutines.delay

private val FRUIT_COLOR = Color(0xFF4FCB94)
private val VEGGIE_COLOR = Color(0xFFFF8B6B)

/** Fruits & Vegetables — learn the two categories, then sort each item into the right group. */
@Composable
fun FruitsVeggiesScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var mode by rememberSaveable { mutableStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<Boolean, Boolean>?>(null) } // guessedFruit → correct?
    var showCelebrate by remember { mutableStateOf(false) }

    // Shuffle order so each session feels different
    val shuffled = remember { FRUITS_VEGGIES.shuffled() }
    val itemIndex = round % shuffled.size
    val current: FruitItem = shuffled[itemIndex]

    LaunchedEffect(round) {
        if (mode == 1) feedback?.speaker?.speak("Is a ${current.name} a Fruit or Vegetable?")
    }
    LaunchedEffect(mode) {
        if (mode == 1) feedback?.speaker?.speak("Is a ${current.name} a Fruit or Vegetable?")
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(750)
            if (flash?.second == true) {
                if (score > 0 && score % 7 == 0) showCelebrate = true else round++
            }
            flash = null
        }
    }

    fun guess(guessedFruit: Boolean) {
        if (flash != null) return
        val ok = guessedFruit == current.isFruit
        flash = guessedFruit to ok
        if (ok) {
            score += 1
            onReward(3)
            feedback?.correct()
            val label = if (current.isFruit) "Fruit!" else "Vegetable!"
            feedback?.speaker?.speak("Yes! $label")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
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
                trailing = if (mode == 1) {
                    {
                        Pill {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            ) {
                                Text("⭐ $score", fontFamily = FontDisplay, fontSize = 16.sp, color = Color(0xFFF2A93B))
                            }
                        }
                    }
                } else null,
            )
            Spacer(Modifier.height(14.dp))
            SegmentedTabs(tabs = listOf("Learn", "Play"), selected = mode, accent = topic.color, onSelect = { mode = it })
            Spacer(Modifier.height(16.dp))

            if (mode == 0) {
                FruitsLearnGrid()
            } else {
                // Play — show item emoji, ask if it's a fruit or veggie
                CardSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp),
                    ) {
                        Text("FRUIT OR VEGETABLE?", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = InkSoft)
                        Spacer(Modifier.height(12.dp))
                        SvgBadge(
                            asset = current.svg,
                            fallbackEmoji = current.emoji,
                            themeColor = topic.color, // neutral — fruit/veggie tint would reveal the answer
                            contentDescription = current.name,
                            modifier = Modifier.size(124.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            current.name,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = topic.color,
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ChoiceButton(
                        label = "🍎 Fruit",
                        color = FRUIT_COLOR,
                        flashOk = flash?.first == true && flash?.second == true,
                        flashBad = flash?.first == true && flash?.second == false,
                        onTap = { guess(true) },
                        modifier = Modifier.weight(1f),
                    )
                    ChoiceButton(
                        label = "🥦 Veggie",
                        color = VEGGIE_COLOR,
                        flashOk = flash?.first == false && flash?.second == true,
                        flashBad = flash?.first == false && flash?.second == false,
                        onTap = { guess(false) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Item ${(itemIndex + 1)} of ${shuffled.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = InkSoft,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Sorting Star!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}

@Composable
private fun FruitsLearnGrid() {
    val fruits = FRUITS_VEGGIES.filter { it.isFruit }
    val veggies = FRUITS_VEGGIES.filter { !it.isFruit }

    @Composable
    fun CategoryRow(title: String, items: List<FruitItem>, accent: Color) {
        Text(
            title,
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = accent,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        items.chunked(4).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                row.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                    ) {
                        // SVG badge carries the item name already; rendered on its own (no card).
                        SvgBadge(
                            asset = item.svg,
                            fallbackEmoji = item.emoji,
                            themeColor = accent,
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        )
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }

    CategoryRow("Fruits 🍎", fruits, FRUIT_COLOR)
    Spacer(Modifier.height(4.dp))
    CategoryRow("Vegetables 🥦", veggies, VEGGIE_COLOR)
}

@Composable
private fun ChoiceButton(
    label: String,
    color: Color,
    flashOk: Boolean,
    flashBad: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onTap,
        modifier = modifier
            .height(100.dp)
            .then(
                when {
                    flashOk -> Modifier.border(4.dp, Green, RoundedCornerShape(24.dp))
                    flashBad -> Modifier.border(4.dp, Color(0xFFFF6B6B), RoundedCornerShape(24.dp))
                    else -> Modifier
                }
            ),
        shape = RoundedCornerShape(24.dp),
        color = color.copy(alpha = 0.12f),
        shadowElevation = 3.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = color,
            )
        }
    }
}
