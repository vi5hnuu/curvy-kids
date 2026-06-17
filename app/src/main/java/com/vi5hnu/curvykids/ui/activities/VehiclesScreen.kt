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
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.Vehicle
import com.vi5hnu.curvykids.data.content.VEHICLES
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.SvgImage
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
            // SVG badge already carries the vehicle name; fall back to emoji + name if unmapped.
            SvgImage(
                asset = vehicle.svg,
                fallbackEmoji = vehicle.emoji,
                fallbackSize = 44.sp,
                contentDescription = vehicle.name,
                modifier = Modifier.size(82.dp),
            )
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
                    CardSurface(
                        modifier = Modifier.weight(1f),
                        onClick = { onSpeak(vehicle) },
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(12.dp),
                        ) {
                            // SVG badge carries the name; keep the sound text below it.
                            SvgImage(
                                asset = vehicle.svg,
                                fallbackEmoji = vehicle.emoji,
                                fallbackSize = 48.sp,
                                contentDescription = vehicle.name,
                                modifier = Modifier.size(90.dp),
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
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}
