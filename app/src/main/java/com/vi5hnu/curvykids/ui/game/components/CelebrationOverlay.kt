package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import androidx.compose.ui.graphics.Color

/**
 * A one-shot confetti burst, shown when the child gets a letter right. Native particles via
 * the konfetti library — no Lottie/image asset needed.
 */
@Composable
fun CelebrationOverlay(modifier: Modifier = Modifier) {
    KonfettiView(
        modifier = modifier.fillMaxSize(),
        parties = listOf(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = CONFETTI_COLORS,
                emitter = Emitter(duration = 120, TimeUnit.MILLISECONDS).max(120),
                position = Position.Relative(0.5, 0.35),
            )
        ),
    )
}

private val CONFETTI_COLORS = listOf(
    Color(0xFFA9BD3E).toArgb(),
    Color(0xFFFFC107).toArgb(),
    Color(0xFFFF5722).toArgb(),
    Color(0xFF03A9F4).toArgb(),
    Color(0xFFE91E63).toArgb(),
)
