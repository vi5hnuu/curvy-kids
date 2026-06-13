package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.BODY_PARTS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink

/** My Body — Learn (tap to hear the part) + Play ("Find the…" quiz). */
@Composable
fun BodyScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = BODY_PARTS,
        quizPromptLabel = "FIND…",
        keyOf = { it.name },
        speakFor = { "Find the ${it.name}" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Body Star!",
        learnContent = { BodyLearnGrid(onReward = onReward, speaker = feedback?.speaker) },
        quizPrompt = { target ->
            Text(
                text = target.name,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                color = topic.color,
            )
        },
        quizOption = { part ->
            Text(part.emoji, fontSize = 46.sp)
        },
    )
}

/** Original explore grid — 3-column, tap a part to hear its name and earn a star once. */
@Composable
private fun BodyLearnGrid(
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    var seen by remember { mutableStateOf(setOf<String>()) }

    val rows = BODY_PARTS.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
        rows.forEach { triple ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                triple.forEach { part ->
                    Surface(
                        onClick = {
                            speaker?.speak(part.name)
                            if (!seen.contains(part.name)) {
                                seen = seen + part.name
                                onReward(2)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 3.dp,
                    ) {
                        Box(modifier = Modifier.padding(16.dp, 16.dp, 8.dp, 12.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(part.emoji, fontSize = 40.sp, lineHeight = 44.sp)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = part.name,
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Ink,
                                )
                            }
                            if (seen.contains(part.name)) {
                                Text(
                                    text = "⭐",
                                    fontSize = 13.sp,
                                    modifier = Modifier.align(Alignment.TopEnd),
                                )
                            }
                        }
                    }
                }
                repeat(3 - triple.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
