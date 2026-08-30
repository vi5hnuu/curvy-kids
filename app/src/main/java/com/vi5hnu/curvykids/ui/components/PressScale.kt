package com.vi5hnu.curvykids.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

/** How far a control shrinks while held. Small enough to feel responsive, not wobbly. */
private const val PRESSED_SCALE = 0.93f

/**
 * Shrinks the element slightly while it is held down.
 *
 * Young children rely on immediate physical-feeling feedback to know a tap registered — a
 * control that looks identical before and during a press reads as broken to them, and they
 * tap it repeatedly. Compose's default ripple is too subtle on the app's large pastel cards.
 *
 * Pass the same [interactionSource] the clickable uses so the scale tracks the real press
 * state; when a caller has none, this creates its own and observes nothing, which is the
 * correct no-op.
 */
fun Modifier.pressScale(
    interactionSource: InteractionSource? = null,
    scaleWhenPressed: Float = PRESSED_SCALE,
): Modifier = composed {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) scaleWhenPressed else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "pressScale",
    )
    this.scale(scale)
}

/**
 * Remembers an [MutableInteractionSource] for a control that wants [pressScale].
 * Kept here so call sites read as one idea rather than two unrelated lines.
 */
@Composable
fun rememberPressInteractionSource(): MutableInteractionSource =
    remember { MutableInteractionSource() }
