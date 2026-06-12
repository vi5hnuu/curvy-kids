package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.data.content.DAY_COLORS
import com.vi5hnu.curvykids.data.content.DAYS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Grape
import com.vi5hnu.curvykids.ui.theme.Ink
import java.util.Calendar

/** Day before [day] in the week, wrapping Sunday→Saturday. */
private fun dayBefore(day: String): String {
    val i = DAYS.indexOf(day)
    return DAYS[(i - 1 + DAYS.size) % DAYS.size]
}

/** Days of the Week — Learn (today + full week) + Play ("What comes next?" quiz). */
@Composable
fun DaysScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = DAYS,
        quizPromptLabel = "WHAT COMES AFTER…",
        keyOf = { it },
        speakFor = { "What comes after ${dayBefore(it)}?" },
        onReward = onReward,
        speaker = speaker,
        celebrateTitle = "Day Star!",
        learnContent = { DaysLearnList(onReward = onReward, speaker = speaker) },
        quizPrompt = { target ->
            Text(
                text = dayBefore(target),
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = topic.color,
            )
        },
        quizOption = { day ->
            Text(
                text = day,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Ink,
            )
        },
    )
}

/** Original explore content — "Today is" card + the week list (tap a day to hear it). */
@Composable
private fun DaysLearnList(
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    val todayIdx = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
    var seen by remember { mutableStateOf(setOf<Int>()) }

    Column {
        // "Today is" card
        Surface(
            shape = RoundedCornerShape(26.dp),
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(colors = listOf(Grape, Color(0xFFBCA6FB)))),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp, 18.dp),
                ) {
                    Text(
                        text = "TODAY IS",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                    Text(
                        text = DAYS[todayIdx],
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        color = Color.White,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DAYS.forEachIndexed { i, day ->
                val isToday = i == todayIdx
                val dayColor = DAY_COLORS[i]

                Surface(
                    onClick = {
                        speaker?.speak(day)
                        if (!seen.contains(i)) {
                            seen = seen + i
                            onReward(1)
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = if (isToday) dayColor else Color.White,
                    shadowElevation = 3.dp,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isToday) Color.White.copy(alpha = 0.3f) else dayColor,
                            modifier = Modifier.size(36.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = day.first().toString(),
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                )
                            }
                        }
                        Spacer(Modifier.size(14.dp))
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp,
                            color = if (isToday) Color.White else Ink,
                        )
                        if (isToday) {
                            Text(
                                text = "TODAY",
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp,
                                color = Color.White,
                            )
                        } else if (seen.contains(i)) {
                            Text("⭐", fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}
