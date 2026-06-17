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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.PICTURE_SPECS
import com.vi5hnu.curvykids.data.content.PictureItem
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/**
 * One reusable screen for every "tap-to-learn + find-it" picture topic (Food, Clothes, Jobs,
 * Sports, Birds, …). The topic's content is looked up by [Topic.id] in [PICTURE_SPECS], so adding
 * a new picture topic needs only data — no new screen (Open/Closed Principle).
 *
 * Learn tab: a 3-column grid of SVG badges, tap to hear the name. Play tab: the [DiscoverActivity]
 * quiz — read/hear the target name, tap the matching picture.
 */
@Composable
fun PictureDiscoverScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    val spec = PICTURE_SPECS[topic.id] ?: return

    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = spec.items,
        quizPromptLabel = spec.promptLabel,
        keyOf = { it.name },
        speakFor = { "Find the ${it.name}" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = spec.celebrateTitle,
        learnContent = { PictureLearnGrid(spec.items, topic, onReward, feedback?.speaker) },
        quizPrompt = { target ->
            Text(
                text = target.name.uppercase(),
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                color = topic.color,
            )
        },
        quizOption = { item ->
            SvgImage(
                asset = item.svg,
                fallbackEmoji = item.emoji,
                fallbackSize = 44.sp,
                contentDescription = item.name,
                modifier = Modifier.size(84.dp),
            )
        },
    )
}

/** 3-column explore grid — tap a picture to hear its name and earn a star the first time. */
@Composable
private fun PictureLearnGrid(
    items: List<PictureItem>,
    topic: Topic,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    var seen by remember { mutableStateOf(setOf<String>()) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { item ->
                    Surface(
                        onClick = {
                            speaker?.speak(item.name)
                            if (!seen.contains(item.name)) {
                                seen = seen + item.name
                                onReward(2)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        color = topic.tint,
                        shadowElevation = 3.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(10.dp),
                        ) {
                            // SVG badge carries the item name already.
                            SvgImage(
                                asset = item.svg,
                                fallbackEmoji = item.emoji,
                                fallbackSize = 40.sp,
                                contentDescription = item.name,
                                modifier = Modifier.size(78.dp),
                            )
                            if (seen.contains(item.name)) {
                                Text(
                                    "⭐",
                                    fontSize = 13.sp,
                                    modifier = Modifier.align(Alignment.TopEnd),
                                )
                            }
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
