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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.ANIMALS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgImage
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
            SvgImage(
                asset = animal.svg,
                fallbackEmoji = animal.emoji,
                fallbackSize = 52.sp,
                contentDescription = animal.name,
                modifier = Modifier.size(84.dp),
            )
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
                    Surface(
                        onClick = {
                            speaker?.speak("${animal.name}. ${animal.sound}")
                            if (!seen.contains(animal.name)) {
                                seen = seen + animal.name
                                onReward(2)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        color = topic.tint,
                        shadowElevation = 3.dp,
                    ) {
                        Box(modifier = Modifier.padding(12.dp, 12.dp, 10.dp, 12.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                // The SVG badge already carries the colored box + animal name;
                                // we keep only the sound below it (the SVG has no sound text).
                                SvgImage(
                                    asset = animal.svg,
                                    fallbackEmoji = animal.emoji,
                                    fallbackSize = 54.sp,
                                    contentDescription = animal.name,
                                    modifier = Modifier.size(96.dp),
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
                                    modifier = Modifier.align(Alignment.TopEnd),
                                )
                            }
                        }
                    }
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}
