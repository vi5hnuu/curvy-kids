package com.vi5hnu.curvykids.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class CurvyMood { Happy, Cheer, Idle, Wow }

/**
 * The "Curvy" mascot — a friendly bean-shaped teal character.
 * SVG paths translated from data.jsx to Compose Canvas drawOps.
 *
 * @param floating When true, applies a gentle vertical float animation.
 */
@Composable
fun CurvyMascot(
    size: Dp = 120.dp,
    mood: CurvyMood = CurvyMood.Happy,
    floating: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (floating) -7f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1750),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatY",
    )

    // The viewBox is 120×134; we scale to the requested dp size
    Canvas(
        modifier = modifier.size(size, size * 134f / 120f),
    ) {
        val scaleX = this.size.width / 120f
        val scaleY = this.size.height / 134f
        val dy = floatY * scaleY

        drawCurvy(scaleX, scaleY, dy, mood)
    }
}

private fun DrawScope.drawCurvy(sx: Float, sy: Float, dy: Float, mood: CurvyMood) {

    fun x(v: Float) = v * sx
    fun y(v: Float) = v * sy + dy
    fun r(v: Float) = v * sx.coerceAtMost(sy)

    // ── Curl sprout ─────────────────────────────────────────────────────────
    val curlPath = Path().apply {
        moveTo(x(60f), y(16f))
        cubicTo(x(60f), y(6f), x(70f), y(4f), x(73f), y(11f))
        cubicTo(x(75f), y(16f), x(69f), y(18f), x(67f), y(14f))
    }
    drawPath(curlPath, color = Color(0xFF16A89A), style = Stroke(width = r(6f), cap = StrokeCap.Round))

    // ── Feet ─────────────────────────────────────────────────────────────────
    drawOval(Color(0xFF16A89A), topLeft = Offset(x(44f - 13f), y(122f - 8f)), size = Size(r(26f), r(16f)))
    drawOval(Color(0xFF16A89A), topLeft = Offset(x(76f - 13f), y(122f - 8f)), size = Size(r(26f), r(16f)))

    // ── Body bean ────────────────────────────────────────────────────────────
    val body = Path().apply {
        moveTo(x(60f), y(18f))
        cubicTo(x(92f), y(18f), x(104f), y(44f), x(104f), y(70f))
        cubicTo(x(104f), y(102f), x(86f), y(120f), x(60f), y(120f))
        cubicTo(x(34f), y(120f), x(16f), y(102f), x(16f), y(70f))
        cubicTo(x(16f), y(44f), x(28f), y(18f), x(60f), y(18f))
        close()
    }
    drawPath(body, color = Color(0xFF1FC2AE))

    // ── Belly highlight ──────────────────────────────────────────────────────
    drawOval(
        color = Color(0xFF3FD3C2).copy(alpha = 0.55f),
        topLeft = Offset(x(60f - 30f), y(78f - 28f)),
        size = Size(r(60f), r(56f)),
    )

    // ── Arms ─────────────────────────────────────────────────────────────────
    // Left arm (rotated -20°) — approximate with oval
    drawOval(Color(0xFF1FC2AE), topLeft = Offset(x(16f - 9f), y(78f - 13f)), size = Size(r(18f), r(26f)))
    // Right arm (rotated +20°)
    drawOval(Color(0xFF1FC2AE), topLeft = Offset(x(104f - 9f), y(78f - 13f)), size = Size(r(18f), r(26f)))

    // ── Cheeks ───────────────────────────────────────────────────────────────
    drawCircle(Color(0xFFFF8FB6).copy(alpha = 0.55f), radius = r(9f), center = Offset(x(36f), y(62f)))
    drawCircle(Color(0xFFFF8FB6).copy(alpha = 0.55f), radius = r(9f), center = Offset(x(84f), y(62f)))

    // ── Eye whites ───────────────────────────────────────────────────────────
    drawOval(Color.White, topLeft = Offset(x(46f - 9f), y(50f - 10.5f)), size = Size(r(18f), r(21f)))
    drawOval(Color.White, topLeft = Offset(x(74f - 9f), y(50f - 10.5f)), size = Size(r(18f), r(21f)))

    // ── Pupils ───────────────────────────────────────────────────────────────
    drawCircle(Color(0xFF2B3A4A), radius = r(4.6f), center = Offset(x(47.5f), y(52f)))
    drawCircle(Color(0xFF2B3A4A), radius = r(4.6f), center = Offset(x(75.5f), y(52f)))

    // ── Eye shine ────────────────────────────────────────────────────────────
    drawCircle(Color.White, radius = r(1.6f), center = Offset(x(49f), y(50f)))
    drawCircle(Color.White, radius = r(1.6f), center = Offset(x(77f), y(50f)))

    // ── Mouth (mood-dependent) ────────────────────────────────────────────────
    when (mood) {
        CurvyMood.Wow -> {
            // Open-O mouth
            drawOval(Color(0xFFE5577F), topLeft = Offset(x(60f - 8f), y(68f - 10f)), size = Size(r(16f), r(20f)))
        }
        CurvyMood.Cheer -> {
            // Filled smile arc
            val path = Path().apply {
                moveTo(x(42f), y(60f))
                cubicTo(x(60f), y(88f), x(78f), y(60f), x(78f), y(60f))
                cubicTo(x(60f), y(72f), x(42f), y(60f), x(42f), y(60f))
                close()
            }
            drawPath(path, color = Color(0xFFE5577F))
            drawPath(path, color = Color(0xFF2B3A4A), style = Stroke(width = r(4f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        CurvyMood.Idle -> {
            val path = Path().apply {
                moveTo(x(44f), y(66f))
                cubicTo(x(60f), y(74f), x(60f), y(74f), x(76f), y(66f))
            }
            drawPath(path, color = Color(0xFF2B3A4A), style = Stroke(width = r(4f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        CurvyMood.Happy -> {
            val path = Path().apply {
                moveTo(x(40f), y(64f))
                cubicTo(x(60f), y(82f), x(60f), y(82f), x(80f), y(64f))
            }
            drawPath(path, color = Color(0xFF2B3A4A), style = Stroke(width = r(4f), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}
