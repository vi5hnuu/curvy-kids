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
import com.vi5hnu.curvykids.data.content.ANIMALS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgBadge
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/** Animals — Learn (tap to hear name + sound) + Play ("Who says…?" quiz). */
@Composable
fun AnimalsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = ANIMALS,
        quizPromptLabel = "WHO SAYS…",
        keyOf = { it.name },
        speakFor = { "Who says ${it.sound}" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Animal Star!",
        learnContent = { AnimalsLearnGrid(topic = topic, onReward = onReward, speaker = feedback?.speaker) },
        quizPrompt = { target ->
            Text(
                text = target.sound,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                color = topic.color,
            )
        },
        quizOption = { animal ->
            // Emoji only in the quiz — the SVG bakes the animal's name in, which would reveal the answer.
            Text(animal.emoji, fontSize = 48.sp, textAlign = TextAlign.Center)
        },
    )
}

/** Original explore grid — tap an animal to hear its name and sound, earning a star once. */
@Composable
private fun AnimalsLearnGrid(
    topic: Topic,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    var seen by remember { mutableStateOf(setOf<String>()) }

    val rows = ANIMALS.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
        rows.forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                pair.forEach { animal ->
                    // The SVG badge already carries its own framed box + animal name, so it's
                    // rendered on its own (no outer card); only the sound is added below it.
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable {
                                speaker?.speak("${animal.name}. ${animal.sound}")
                                if (!seen.contains(animal.name)) {
                                    seen = seen + animal.name
                                    onReward(2)
                                }
                            },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SvgBadge(
                                asset = animal.svg,
                                emoji = animal.emoji,
                                fallbackEmoji = animal.emoji,
                                themeColor = topic.color,
                                contentDescription = animal.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = animal.sound,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = topic.color,
                            )
                        }
                        if (seen.contains(animal.name)) {
                            Text(
                                text = "⭐",
                                fontSize = 15.sp,
                                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                            )
                        }
                    }
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}
