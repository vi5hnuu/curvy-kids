package com.vi5hnu.curvykids.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Below this many steps a child can take in the beads at a glance; above it they are a blur. */
private const val MAX_BEADS = 8

/**
 * Shows how far through a set of activities the child is, without using numerals.
 *
 * "12/36" tells a four-year-old nothing — they cannot read it, and the app's whole audience is
 * pre-readers. How much of the row is coloured in says the same thing at a glance, and says it
 * to someone who has never seen a digit.
 *
 * Short sets get one bead per step, which a child can point at and count. Longer ones get a
 * filled bar, because thirty-six beads is not something anyone reads at a glance.
 *
 * @param current How many steps are behind the child, counting the one they are on.
 * @param total   How many there are altogether.
 * @param color   The topic's colour, so the row belongs to the screen it sits on.
 */
@Composable
fun ProgressTrack(
    current: Int,
    total: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (total <= 0) return
    val done = current.coerceIn(0, total)
    val track = color.copy(alpha = 0.18f)

    if (total <= MAX_BEADS) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(total) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == done - 1) 12.dp else 9.dp)
                        .background(if (i < done) color else track, CircleShape)
                )
            }
        }
        return
    }

    // Animated so that moving on shows the row growing, which is the reward for going on.
    val fraction by animateFloatAsState(
        targetValue = done.toFloat() / total,
        animationSpec = tween(durationMillis = 400),
        label = "progress",
    )
    Box(
        modifier = modifier
            .width(72.dp)
            .height(12.dp)
            .background(track, RoundedCornerShape(999.dp)),
    ) {
        if (fraction > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(999.dp))
            )
        }
    }
}
