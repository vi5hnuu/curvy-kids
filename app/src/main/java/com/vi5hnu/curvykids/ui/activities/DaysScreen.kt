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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Grape
import com.vi5hnu.curvykids.ui.theme.Ink
import java.util.Calendar

/** Days of the Week — vertical list with today highlighted. */
@Composable
fun DaysScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker? = null,
) {
    val todayIdx = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
    var seen by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 24.dp),
    ) {
        ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
        Spacer(Modifier.height(16.dp))

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
                        // Initial circle
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
