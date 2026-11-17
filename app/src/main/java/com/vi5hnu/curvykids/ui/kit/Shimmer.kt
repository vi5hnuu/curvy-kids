package com.vi5hnu.curvykids.ui.kit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val ShimmerBase = Color(0xFFE9E4F5)
private val ShimmerHighlight = Color(0xFFF9F7FD)

/** Width of the moving highlight, in px; wide enough to read as a soft sweep on small shapes too. */
private const val SWEEP_PX = 600f

/**
 * A soft highlight sweeping across, for something that is still being read (whether Pro is
 * owned, a price) so the screen holds its shape instead of showing a guess and changing it.
 */
fun Modifier.shimmer(shape: Shape = RoundedCornerShape(8.dp)): Modifier = composed {
    val x by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = -SWEEP_PX,
        targetValue = SWEEP_PX * 2,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
        label = "sweep",
    )
    clip(shape).background(
        Brush.linearGradient(
            colors = listOf(ShimmerBase, ShimmerHighlight, ShimmerBase),
            start = Offset(x, 0f),
            end = Offset(x + SWEEP_PX, SWEEP_PX / 3),
        )
    )
}

/** A shimmering placeholder of a fixed size, standing in for text, a price or a badge. */
@Composable
fun ShimmerBox(width: Dp, height: Dp, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(8.dp)) {
    Box(modifier.size(width, height).shimmer(shape))
}
