package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.Vehicle
import com.vi5hnu.curvykids.data.content.VEHICLES
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgBadge
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft

/** Vehicles — learn transportation names and sounds, then identify them in a quiz. */
@Composable
fun VehiclesScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = VEHICLES,
        quizPromptLabel = "FIND THE VEHICLE",
        keyOf = { it.name },
        speakFor = { "Find the ${it.name}" },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Road Trip!",
        learnContent = { VehiclesLearnGrid(topic.color, onSpeak = { v -> feedback?.speaker?.speak("${v.name}! ${v.sound}") }) },
        quizPrompt = { vehicle ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    vehicle.name.uppercase(),
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = topic.color,
                )
                Spacer(Modifier.height(4.dp))
                Text(vehicle.sound, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkSoft)
            }
        },
        quizOption = { vehicle ->
            // Emoji only in the quiz — the SVG bakes the vehicle's name in, which would reveal the answer.
            Text(vehicle.emoji, fontSize = 44.sp, textAlign = TextAlign.Center)
        },
    )
}

@Composable
private fun VehiclesLearnGrid(accent: Color, onSpeak: (Vehicle) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        VEHICLES.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                pair.forEach { vehicle ->
                    // The SVG badge carries its own framed box + vehicle name, so it's rendered on
                    // its own (no outer card); the sound text is kept below it.
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSpeak(vehicle) },
                    ) {
                        SvgBadge(
                            asset = vehicle.svg,
                            emoji = vehicle.emoji,
                            fallbackEmoji = vehicle.emoji,
                            themeColor = accent,
                            contentDescription = vehicle.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            vehicle.sound,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = InkSoft,
                        )
                    }
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}
