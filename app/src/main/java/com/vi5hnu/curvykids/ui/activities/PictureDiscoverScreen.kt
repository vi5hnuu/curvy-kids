package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.vi5hnu.curvykids.data.content.PICTURE_SPECS
import com.vi5hnu.curvykids.data.content.PictureItem
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgBadge
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
            // Emoji only in the quiz — the SVG bakes the name in, which would reveal the answer.
            Text(item.emoji, fontSize = 48.sp, textAlign = TextAlign.Center)
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
                    // The SVG badge already carries its own rounded border, emoji and name, so we
                    // render it on its own — no surrounding card — to avoid a box-inside-a-box look.
                    // The badge itself is the tap target.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable {
                                speaker?.speak(item.name)
                                if (!seen.contains(item.name)) {
                                    seen = seen + item.name
                                    onReward(2)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        SvgBadge(
                            asset = item.svg,
                            fallbackEmoji = item.emoji,
                            themeColor = topic.color,
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        )
                        if (seen.contains(item.name)) {
                            Text("⭐", fontSize = 13.sp, modifier = Modifier.align(Alignment.TopEnd))
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
