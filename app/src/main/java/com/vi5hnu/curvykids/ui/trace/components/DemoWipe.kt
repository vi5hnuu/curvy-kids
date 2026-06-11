package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import kotlinx.coroutines.delay

/**
 * Left→right animated reveal of the letter in the current crayon color at 32% opacity.
 * Matches the `DemoWipe` component in trace.jsx: animates over 1600ms with 250ms delay.
 * Disappears completely once the wipe finishes (progress = 1).
 *
 * @param char     Character to render.
 * @param color    Current crayon/ink color (revealed copy is this color at 32% alpha).
 * @param demoKey  Increment to restart the animation from the replay button.
 */
@Composable
fun DemoWipe(
    char: String,
    color: Color,
    demoKey: Int,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }
    var finished by remember { mutableStateOf(false) }

    LaunchedEffect(demoKey) {
        finished = false
        progress.snapTo(0f)
        delay(250)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 1600))
        finished = true
    }

    if (finished) return

    val p = progress.value

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = char,
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 230.sp,
            lineHeight = 230.sp,
            color = color.copy(alpha = 0.32f),
            modifier = Modifier.drawWithContent {
                // Clip the drawn content to the revealed fraction (left → right)
                drawContext.canvas.save()
                drawContext.canvas.clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width * p,
                    bottom = size.height,
                )
                drawContent()
                drawContext.canvas.restore()
            },
        )
    }
}
