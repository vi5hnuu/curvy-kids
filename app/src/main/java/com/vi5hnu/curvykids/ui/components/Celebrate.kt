package com.vi5hnu.curvykids.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import kotlinx.coroutines.delay

/**
 * Full-screen celebration overlay — shown after a correct answer.
 * Displays Curvy (cheer mood) + confetti + star rating + title/subtitle.
 * Auto-dismisses after 2200 ms; can also be tapped to dismiss.
 *
 * @param title  Primary celebration text.
 * @param sub    Secondary line (e.g. phonics phrase).
 * @param stars  How many of 3 stars to highlight (1–3).
 * @param onDone Called when the overlay is dismissed.
 */
@Composable
fun Celebrate(
    title: String = "Yay!",
    sub: String? = null,
    stars: Int = 3,
    onDone: () -> Unit = {},
) {
    LaunchedEffect(Unit) {
        delay(2200)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(70f)
            .clickable(onClick = onDone),
        contentAlignment = Alignment.Center,
    ) {
        // Teal translucent scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .run {
                    // Draw a teal overlay at 22% opacity
                    this
                },
        ) {
            // Background tint via Canvas
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = Color(0xFF1FC2AE).copy(alpha = 0.22f))
            }
        }

        // Confetti particles
        ConfettiOverlay(count = 36)

        // Content column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp),
        ) {
            // Floating Curvy
            CurvyMascot(size = 150.dp, mood = CurvyMood.Cheer, floating = true)

            Spacer(Modifier.height(10.dp))

            // Star row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0 until 3) {
                    Text(
                        text = "⭐",
                        fontSize = 34.sp,
                        color = if (i < stars) Color.Unspecified else Color.Gray.copy(alpha = 0.4f),
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = title,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                color = Color.White,
            )

            if (sub != null) {
                Text(
                    text = sub,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.95f),
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "tap to keep going →",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f),
            )
        }
    }
}
