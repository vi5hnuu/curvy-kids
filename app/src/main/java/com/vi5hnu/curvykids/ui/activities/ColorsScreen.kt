package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.background
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
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.data.content.COLOR_ITEMS
import com.vi5hnu.curvykids.data.content.ColorItem
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import kotlinx.coroutines.delay

/** "Find the Color" matching game. */
@Composable
fun ColorsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    onAddBadge: (String) -> Unit = {},
    speaker: PhonicsSpeaker? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var target by remember { mutableStateOf(COLOR_ITEMS.random()) }
    var options by remember { mutableStateOf(buildOptions(target)) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // name → correct?
    var showCelebrate by remember { mutableStateOf(false) }

    fun newRound() {
        val t = COLOR_ITEMS.random()
        target = t
        options = buildOptions(t)
        speaker?.speak("Find ${t.name}")
    }

    LaunchedEffect(Unit) { speaker?.speak("Find ${target.name}") }

    Box {
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

            // Prompt card
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp, 24.dp, 24.dp, 18.dp),
                ) {
                    Text(
                        text = "FIND THE COLOR",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = InkSoft,
                    )
                    Text(
                        text = target.name,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 40.sp,
                        color = Color(target.hex),
                    )
                    Spacer(Modifier.height(10.dp))
                    Chip(
                        onClick = { speaker?.speak("Find ${target.name}") },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // 2×2 color option grid
            val rows = options.chunked(2)
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                rows.forEach { pair ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        pair.forEach { item ->
                            val isFlash = flash?.first == item.name
                            val flashOk = flash?.second == true
                            Surface(
                                onClick = {
                                    if (flash != null) return@Surface
                                    if (item.name == target.name) {
                                        flash = item.name to true
                                        val newScore = score + 1
                                        score = newScore
                                        onReward(3)
                                        if (newScore == 1) onAddBadge("colors")
                                        speaker?.speak("Yes! ${item.name}")
                                    } else {
                                        flash = item.name to false
                                        speaker?.speak("Try again")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(96.dp),
                                shape = RoundedCornerShape(24.dp),
                                color = Color(item.hex),
                                shadowElevation = 6.dp,
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                    if (isFlash && flashOk) Text("✓", fontSize = 40.sp, color = Color.White)
                                }
                            }
                        }
                        if (pair.size < 2) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        // Handle flash auto-clear + round advance
        LaunchedEffect(flash) {
            if (flash != null) {
                delay(700)
                if (flash?.second == true) {
                    if (score > 0 && score % 5 == 0) {
                        showCelebrate = true
                    } else {
                        newRound()
                    }
                }
                flash = null
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Color Star!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; newRound() },
            )
        }
    }
}

private fun buildOptions(target: ColorItem): List<ColorItem> {
    val pool = COLOR_ITEMS.filter { it.name != target.name }.shuffled().take(3)
    return (pool + target).shuffled()
}
