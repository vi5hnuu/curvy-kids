package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.vi5hnu.curvykids.data.content.FRACTIONS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/**
 * Fractions — Learn (tap a pie to hear the fraction) + Play (see the shaded pie, tap the label).
 * Reuses [DiscoverActivity]: the prompt is the pie chart, the options are the fraction labels.
 */
@Composable
fun FractionsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = FRACTIONS,
        quizPromptLabel = "WHICH FRACTION IS SHADED?",
        keyOf = { it.label },
        speakFor = { "Which fraction is shaded?" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Fraction Star!",
        optionCount = 3,
        learnContent = { FractionsLearnGrid(topic, onReward, feedback?.speaker) },
        quizPrompt = { item ->
            SvgImage(
                asset = item.svg,
                fallbackEmoji = "🥧",
                fallbackSize = 64.sp,
                contentDescription = item.spoken,
                modifier = Modifier.size(130.dp),
            )
        },
        quizOption = { item ->
            Text(
                item.label,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = topic.color,
            )
        },
    )
}

/** 3-column grid of fraction pies; tap one to hear it and earn a star the first time. */
@Composable
private fun FractionsLearnGrid(
    topic: Topic,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FRACTIONS.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { item ->
                    CardSurface(
                        modifier = Modifier.weight(1f),
                        onClick = { speaker?.speak(item.spoken); onReward(1) },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(10.dp),
                        ) {
                            SvgImage(
                                asset = item.svg,
                                fallbackEmoji = "🥧",
                                fallbackSize = 40.sp,
                                contentDescription = item.spoken,
                                modifier = Modifier.size(78.dp),
                            )
                            Text(
                                item.label,
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
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
