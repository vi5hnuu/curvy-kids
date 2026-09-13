package com.vi5hnu.curvykids.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * Builds a smooth [Path] through a run of sampled points, using Catmull-Rom cubic segments.
 *
 * The curve passes through every point rather than near it, so the result is the same shape the
 * points describe, just without the corners a straight-line join leaves at each sample. Phantom
 * duplicates of the first and last point keep the end segments smooth too.
 *
 * Used to draw the child's own ink as smooth strokes.
 */
fun List<Offset>.toSmoothPath(): Path {
    val path = Path()
    if (isEmpty()) return path
    path.moveTo(first().x, first().y)
    if (size < 3) {
        drop(1).forEach { path.lineTo(it.x, it.y) }
        return path
    }
    val pts = listOf(first()) + this + listOf(last())
    for (i in 1 until pts.size - 2) {
        val p0 = pts[i - 1]; val p1 = pts[i]; val p2 = pts[i + 1]; val p3 = pts[i + 2]
        path.cubicTo(
            p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f,
            p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f,
            p2.x, p2.y,
        )
    }
    return path
}
