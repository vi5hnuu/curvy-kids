package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.vi5hnu.curvykids.data.content.COUNT_ITEMS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Teal
import kotlinx.coroutines.delay

/** Counting activity — tap each emoji then pick the number. */
@Composable
fun CountScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker? = null,
) {
    var n by remember { mutableIntStateOf((1..9).random()) }
    var item by remember { mutableStateOf(COUNT_ITEMS.random()) }
    var tapped by remember { mutableStateOf(listOf<Int>()) }
    var answer by remember { mutableStateOf<Pair<Int, Boolean>?>(null) } // value → correct?
    var streak by remember { mutableIntStateOf(0) }
    var showCelebrate by remember { mutableStateOf(false) }

    val options = remember(n) {
        val set = mutableSetOf(n)
        while (set.size < 3) set.add((1..9).random())
        set.shuffled()
    }

    fun newRound() {
        n = (1..9).random()
        item = COUNT_ITEMS.random()
        tapped = listOf()
        answer = null
    }

    Box {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 120.dp),
        ) {
            ScreenHeader(
                title = "Counting",
                color = topic.color,
                onBack = onBack,
                trailing = {
                    Pill {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            Text("⭐ $streak", fontFamily = FontDisplay, fontSize = 16.sp, color = Color(0xFFF2A93B))
                        }
                    }
                },
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tap each one, then pick the number!",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = topic.color.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            Spacer(Modifier.height(10.dp))

            // Emoji grid
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        for (i in 0 until n) {
                            val isTapped = tapped.contains(i)
                            Box {
                                Surface(
                                    onClick = {
                                        if (!isTapped && answer == null) {
                                            val newList = tapped + i
                                            tapped = newList
                                            speaker?.speak(newList.size.toString())
                                        }
                                    },
                                    color = Color.Transparent,
                                    modifier = Modifier.size(56.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(item, fontSize = 40.sp)
                                    }
                                }
                                if (isTapped) {
                                    Surface(
                                        shape = RoundedCornerShape(999.dp),
                                        color = Teal,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .align(Alignment.TopEnd),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${tapped.indexOf(i) + 1}",
                                                fontFamily = FontDisplay,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 11.sp,
                                                color = Color.White,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Number choice row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                options.forEach { v ->
                    val isAnswer = answer?.first == v
                    val correct = answer?.second == true && isAnswer
                    val wrong = answer?.second == false && isAnswer
                    CandyButton(
                        onClick = {
                            if (answer != null) return@CandyButton
                            if (v == n) {
                                answer = v to true
                                val ns = streak + 1; streak = ns
                                onReward(4)
                                speaker?.speak("Yes! $n")
                            } else {
                                answer = v to false
                                speaker?.speak("Count again")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp),
                        containerColor = when {
                            correct -> Color(0xFF4FCB94)
                            wrong   -> Color(0xFFFF6B6B)
                            else    -> topic.color
                        },
                    ) {
                        Text(
                            text = "$v",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 40.sp,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        LaunchedEffect(answer) {
            if (answer != null) {
                delay(800)
                if (answer?.second == true) {
                    if (streak > 0 && streak % 4 == 0) {
                        showCelebrate = true
                    } else {
                        newRound()
                    }
                } else {
                    // Reset both answer and tapped so the child can re-count from scratch.
                    // Without this, all emoji stay locked (isTapped=true) while answer==null.
                    tapped = listOf()
                    answer = null
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Number Whiz!",
                sub = "$streak in a row!",
                stars = 3,
                onDone = { showCelebrate = false; newRound() },
            )
        }
    }
}
