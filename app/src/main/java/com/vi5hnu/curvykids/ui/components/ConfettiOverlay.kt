package com.vi5hnu.curvykids.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val CONFETTI_COLORS = listOf(
    Color(0xFFFF8B6B), Color(0xFFFFC24A), Color(0xFF4FCB94),
    Color(0xFF46A6F0), Color(0xFFA88BF6), Color(0xFFFF8FB6), Color(0xFF1FC2AE),
)

private data class Particle(
    val startX: Float,    // fraction 0..1 of canvas width
    val color: Color,
    val size: Float,      // dp
    val isRound: Boolean,
    val rotation: Float,  // initial rotation degrees
    val speedFactor: Float, // fall speed multiplier
    val delayMs: Int,
)

/**
 * Pure Compose confetti — particles fall from the top of the canvas.
 * Translated from the CSS confetti-fall animation in shared.jsx.
 */
@Composable
fun ConfettiOverlay(
    count: Int = 28,
    modifier: Modifier = Modifier,
) {
    val particles = remember(count) {
        List(count) { i ->
            Particle(
                startX = (i.toFloat() / count + (i % 7) * 0.05f) % 1f,
                color = CONFETTI_COLORS[i % CONFETTI_COLORS.size],
                size = 8f + (i % 4) * 2f,
                isRound = i % 2 == 0,
                rotation = (i * 47) % 360f,
                speedFactor = 0.8f + (i % 5) * 0.1f,
                delayMs = (i % 8) * 40,
            )
        }
    }

    val animatables = remember(count) { List(count) { Animatable(0f) } }

    LaunchedEffect(count) {
        animatables.forEachIndexed { i, anim ->
            launch {
                kotlinx.coroutines.delay(particles[i].delayMs.toLong())
                anim.animateTo(1f, animationSpec = tween(durationMillis = (1100 + (i % 4) * 250)))
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasH = size.height

        animatables.forEachIndexed { i, anim ->
            val p = animatables[i].value
            val particle = particles[i]
            val x = particle.startX * size.width
            val y = -size.height * 0.08f + p * (canvasH * 1.15f)
            val alpha = if (p > 0.75f) 1f - (p - 0.75f) * 4f else 1f
            val rot = particle.rotation + p * 540f
            val sz = particle.size

            if (alpha <= 0f) return@forEachIndexed

            if (particle.isRound) {
                drawCircle(
                    color = particle.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                    radius = sz / 2f,
                    center = Offset(x, y),
                )
            } else {
                rotate(rot, Offset(x, y)) {
                    drawRect(
                        color = particle.color.copy(alpha = alpha.coerceIn(0f, 1f)),
                        topLeft = Offset(x - sz / 2f, y - sz / 2f),
                        size = Size(sz, sz),
                    )
                }
            }
        }
    }
}
