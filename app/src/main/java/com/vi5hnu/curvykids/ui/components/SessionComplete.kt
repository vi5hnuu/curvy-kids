package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/**
 * The end of a quiz session: the mascot cheers, the stars earned are shown, and there are two
 * big picture buttons, play again or go back. Neither needs reading: one is the replay arrow, the
 * other the house from the tab bar, and both are said aloud when the screen appears.
 *
 * It doesn't dismiss itself. A session that just ends and starts over defeats the point of
 * having an end.
 */
@Composable
fun SessionComplete(
    stars: Int,
    color: Color,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    onShown: () -> Unit = {},
) {
    LaunchedEffect(Unit) { onShown() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(70f)
            .background(Color(0xFF1FC2AE).copy(alpha = 0.30f))
            // Swallow taps so the quiz underneath can't be played.
            .clickable(remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.Center,
    ) {
        ConfettiOverlay(count = 36)
        CardSurface(modifier = Modifier.padding(24.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
            ) {
                CurvyMascot(size = 130.dp, mood = CurvyMood.Cheer, floating = true)
                Spacer(Modifier.height(8.dp))
                Text(
                    "All done!",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    color = color,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0 until 3) {
                        KidIconImage(
                            icon = KidIcon.Star,
                            modifier = Modifier.size(44.dp),
                            color = if (i < stars) Color(0xFFFFC24A) else Color(0xFFE4EDF4),
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                    CandyButton(onClick = onHome, containerColor = Color.White, contentColor = color,
                        modifier = Modifier.size(84.dp)) {
                        Icon(Icons.Rounded.Home, contentDescription = "Home", modifier = Modifier.size(44.dp))
                    }
                    CandyButton(onClick = onPlayAgain, containerColor = color, modifier = Modifier.size(84.dp)) {
                        KidIconImage(KidIcon.Undo, Modifier.size(42.dp), color = Color.White,
                            contentDescription = "Play again")
                    }
                }
            }
        }
    }
}
