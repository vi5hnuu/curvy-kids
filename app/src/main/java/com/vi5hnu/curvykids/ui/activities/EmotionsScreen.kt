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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.Emotion
import com.vi5hnu.curvykids.data.content.EMOTIONS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft

/** Feelings — learn emotion names and faces, then identify them in a quiz. */
@Composable
fun EmotionsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = EMOTIONS,
        quizPromptLabel = "FIND THE FEELING",
        keyOf = { it.name },
        speakFor = { "Find ${it.name}" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Feelings Star!",
        learnContent = { EmotionsLearnGrid() },
        quizPrompt = { emotion ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    emotion.name.uppercase(),
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = topic.color,
                )
                Spacer(Modifier.height(4.dp))
                Text(emotion.description, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkSoft)
            }
        },
        quizOption = { emotion ->
            SvgImage(
                asset = emotion.svg,
                fallbackEmoji = emotion.emoji,
                fallbackSize = 44.sp,
                contentDescription = emotion.name,
                modifier = Modifier.size(82.dp),
            )
        },
    )
}

@Composable
private fun EmotionsLearnGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EMOTIONS.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                pair.forEach { emotion ->
                    CardSurface(modifier = Modifier.weight(1f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(12.dp),
                        ) {
                            // SVG badge carries the feeling name; keep the description below it.
                            SvgImage(
                                asset = emotion.svg,
                                fallbackEmoji = emotion.emoji,
                                fallbackSize = 52.sp,
                                contentDescription = emotion.name,
                                modifier = Modifier.size(92.dp),
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                emotion.description,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = InkSoft,
                            )
                        }
                    }
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}
