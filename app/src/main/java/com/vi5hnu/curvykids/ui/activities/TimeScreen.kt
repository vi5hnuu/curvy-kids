package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.TIMES
import com.vi5hnu.curvykids.data.content.TimeItem
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * Telling Time — Learn (tap a clock to hear the hour) + Play (read the clock, tap the hour).
 * Reuses [DiscoverActivity]: the prompt is the clock face, the options are the hour numbers.
 */
@Composable
fun TimeScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = TIMES,
        quizPromptLabel = "WHAT TIME IS IT?",
        keyOf = { it.hour.toString() },
        speakFor = { "Look at the clock. What time is it?" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Time Star!",
        optionCount = 3,
        learnContent = { TimeLearnGrid(topic, onReward, feedback?.speaker) },
        quizPrompt = { item ->
            SvgImage(
                asset = item.svg,
                fallbackEmoji = "🕐",
                fallbackSize = 64.sp,
                contentDescription = item.label,
                modifier = Modifier.size(140.dp),
            )
        },
        quizOption = { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "${item.hour}",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 40.sp,
                    color = topic.color,
                )
                Text("o'clock", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = InkSoft)
            }
        },
    )
}

/** 3-column grid of clocks; tap one to hear the hour and earn a star the first time. */
@Composable
private fun TimeLearnGrid(
    topic: Topic,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TIMES.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { item ->
                    CardSurface(
                        modifier = Modifier.weight(1f),
                        onClick = { speaker?.speak("${item.label}"); onReward(1) },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp),
                        ) {
                            SvgImage(
                                asset = item.svg,
                                fallbackEmoji = "🕐",
                                fallbackSize = 36.sp,
                                contentDescription = item.label,
                                modifier = Modifier.size(74.dp),
                            )
                            Text(
                                item.label,
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = topic.color,
                            )
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
