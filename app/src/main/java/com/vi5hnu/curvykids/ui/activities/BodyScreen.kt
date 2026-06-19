package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.BODY_PARTS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgBadge
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
        learnContent = { BodyLearnGrid(topic = topic, onReward = onReward, speaker = feedback?.speaker) },
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
            // Emoji only in the quiz — the SVG bakes the part's name in, which would reveal the answer.
            Text(part.emoji, fontSize = 46.sp, textAlign = TextAlign.Center)
        },
    )
}

/** Original explore grid — 3-column, tap a part to hear its name and earn a star once. */
@Composable
private fun BodyLearnGrid(
    topic: Topic,
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
                    // The SVG badge carries its own framed box + name, so it's rendered on its
                    // own (no outer card) with its border in the screen theme.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable {
                                speaker?.speak(part.name)
                                if (!seen.contains(part.name)) {
                                    seen = seen + part.name
                                    onReward(2)
                                }
                            },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SvgBadge(
                                asset = part.svg,
                                emoji = part.emoji,
                                fallbackEmoji = part.emoji,
                                themeColor = topic.color,
                                contentDescription = part.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                            )
                            // The SVG badge carries the name; only show it for unmapped parts.
                            if (part.svg == null) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = part.name,
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Ink,
                                )
                            }
                        }
                        if (seen.contains(part.name)) {
                            Text(
                                text = "⭐",
                                fontSize = 13.sp,
                                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                            )
                        }
                    }
                }
                repeat(3 - triple.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
