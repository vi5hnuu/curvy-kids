package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/** Writing topics legitimately want their letters shown — others get a vector illustration. */
private val LETTER_TOPICS = setOf("upper", "lower", "numbers")

/**
 * The icon shown inside a topic card's badge. Writing topics ("Aa", "bd", "12") keep their
 * readable letters; every other topic renders a friendly white vector illustration instead of
 * an emoji, so the cards feel hand-crafted and consistent.
 *
 * @param topicId Stable topic id (drives which illustration to draw).
 * @param fallback The topic's glyph text, used for the letter/number writing topics.
 * @param color   Illustration color (white on the colored badge).
 */
@Composable
fun TopicGlyph(
    topicId: String,
    fallback: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (topicId in LETTER_TOPICS) {
            Text(
                text = fallback,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (fallback.length >= 2) 24.sp else 28.sp,
                color = color,
            )
        } else {
            Canvas(modifier = Modifier.size(30.dp)) { drawTopicGlyph(topicId, color) }
        }
    }
}

private fun DrawScope.drawTopicGlyph(id: String, color: Color) {
    val w = size.width
    val h = size.height
    val s = minOf(w, h)
    val cx = w / 2f
    val cy = h / 2f
    val sw = s * 0.09f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (id) {
        "shapes" -> {
            drawCircle(color, s * 0.15f, Offset(w * 0.32f, h * 0.32f))
            val tri = Path().apply {
                moveTo(w * 0.72f, h * 0.18f)
                lineTo(w * 0.92f, h * 0.52f)
                lineTo(w * 0.52f, h * 0.52f)
                close()
            }
            drawPath(tri, color)
            drawRoundRect(color, topLeft = Offset(w * 0.34f, h * 0.6f), size = Size(s * 0.32f, s * 0.32f), cornerRadius = CornerRadius(s * 0.06f))
        }
        "colors" -> {
            // paint palette blob + thumb hole + 3 paint dots
            drawCircle(color, s * 0.4f, Offset(cx, cy), style = stroke)
            drawCircle(color, s * 0.08f, Offset(w * 0.66f, h * 0.66f))
            drawCircle(color, s * 0.07f, Offset(w * 0.34f, h * 0.38f))
            drawCircle(color, s * 0.07f, Offset(w * 0.6f, h * 0.34f))
            drawCircle(color, s * 0.07f, Offset(w * 0.34f, h * 0.62f))
        }
        "count" -> {
            // three growing dots
            drawCircle(color, s * 0.08f, Offset(w * 0.26f, h * 0.62f))
            drawCircle(color, s * 0.11f, Offset(cx, h * 0.52f))
            drawCircle(color, s * 0.14f, Offset(w * 0.74f, h * 0.4f))
        }
        "words" -> {
            // speech bubble with two text lines
            drawRoundRect(color, topLeft = Offset(w * 0.16f, h * 0.2f), size = Size(w * 0.68f, h * 0.44f), cornerRadius = CornerRadius(s * 0.14f), style = stroke)
            val tail = Path().apply {
                moveTo(w * 0.34f, h * 0.62f)
                lineTo(w * 0.3f, h * 0.82f)
                lineTo(w * 0.5f, h * 0.62f)
                close()
            }
            drawPath(tail, color)
            drawLine(color, Offset(w * 0.28f, h * 0.36f), Offset(w * 0.72f, h * 0.36f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.28f, h * 0.48f), Offset(w * 0.58f, h * 0.48f), strokeWidth = sw, cap = StrokeCap.Round)
        }
        "animals" -> {
            // cat face: ears + face + eyes + nose
            val earL = Path().apply { moveTo(w * 0.26f, h * 0.34f); lineTo(w * 0.22f, h * 0.12f); lineTo(w * 0.42f, h * 0.24f); close() }
            val earR = Path().apply { moveTo(w * 0.74f, h * 0.34f); lineTo(w * 0.78f, h * 0.12f); lineTo(w * 0.58f, h * 0.24f); close() }
            drawPath(earL, color); drawPath(earR, color)
            drawCircle(color, s * 0.3f, Offset(cx, h * 0.56f), style = stroke)
            drawCircle(color, s * 0.04f, Offset(w * 0.4f, h * 0.52f))
            drawCircle(color, s * 0.04f, Offset(w * 0.6f, h * 0.52f))
            drawCircle(color, s * 0.035f, Offset(cx, h * 0.63f))
        }
        "body" -> {
            // friendly face: circle + eyes + smile
            drawCircle(color, s * 0.36f, Offset(cx, cy), style = stroke)
            drawCircle(color, s * 0.045f, Offset(w * 0.4f, h * 0.44f))
            drawCircle(color, s * 0.045f, Offset(w * 0.6f, h * 0.44f))
            drawArc(color, startAngle = 20f, sweepAngle = 140f, useCenter = false,
                topLeft = Offset(w * 0.34f, h * 0.4f), size = Size(w * 0.32f, h * 0.3f), style = stroke)
        }
        "days" -> {
            // calendar: tabs + body + header + dots
            drawLine(color, Offset(w * 0.36f, h * 0.12f), Offset(w * 0.36f, h * 0.26f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.64f, h * 0.12f), Offset(w * 0.64f, h * 0.26f), strokeWidth = sw, cap = StrokeCap.Round)
            drawRoundRect(color, topLeft = Offset(w * 0.18f, h * 0.2f), size = Size(w * 0.64f, h * 0.62f), cornerRadius = CornerRadius(s * 0.1f), style = stroke)
            drawLine(color, Offset(w * 0.18f, h * 0.4f), Offset(w * 0.82f, h * 0.4f), strokeWidth = sw)
            for (r in 0..1) for (c in 0..2) {
                drawCircle(color, s * 0.035f, Offset(w * (0.32f + c * 0.18f), h * (0.55f + r * 0.16f)))
            }
        }
        "draw" -> {
            // pencil
            val bodyTop = Offset(w * 0.28f, h * 0.22f)
            val bodyBot = Offset(w * 0.68f, h * 0.62f)
            drawLine(color, bodyTop, bodyBot, strokeWidth = s * 0.22f, cap = StrokeCap.Butt)
            // tip
            val tip = Path().apply {
                moveTo(w * 0.62f, h * 0.68f)
                lineTo(w * 0.8f, h * 0.8f)
                lineTo(w * 0.74f, h * 0.56f)
                close()
            }
            drawPath(tip, color)
        }
        "memory" -> {
            // two overlapping cards
            drawRoundRect(color, topLeft = Offset(w * 0.2f, h * 0.26f), size = Size(s * 0.34f, s * 0.46f), cornerRadius = CornerRadius(s * 0.08f), style = stroke)
            drawRoundRect(color, topLeft = Offset(w * 0.46f, h * 0.26f), size = Size(s * 0.34f, s * 0.46f), cornerRadius = CornerRadius(s * 0.08f), style = stroke)
        }
        "bigsmall" -> {
            // a big circle and a small circle
            drawCircle(color, s * 0.26f, Offset(w * 0.36f, h * 0.5f), style = stroke)
            drawCircle(color, s * 0.12f, Offset(w * 0.74f, h * 0.62f), style = stroke)
        }
        "oddone" -> {
            // magnifying glass
            drawCircle(color, s * 0.24f, Offset(w * 0.42f, h * 0.42f), style = stroke)
            drawLine(color, Offset(w * 0.6f, h * 0.6f), Offset(w * 0.78f, h * 0.78f), strokeWidth = s * 0.12f, cap = StrokeCap.Round)
        }
        "catch" -> {
            // target with three rings
            drawCircle(color, s * 0.36f, Offset(cx, cy), style = stroke)
            drawCircle(color, s * 0.22f, Offset(cx, cy), style = stroke)
            drawCircle(color, s * 0.07f, Offset(cx, cy))
        }

        // ── World group ────────────────────────────────────────────────────────

        "months" -> {
            // calendar with ring binders and 4×3 dot grid (12 months)
            drawLine(color, Offset(w * 0.36f, h * 0.10f), Offset(w * 0.36f, h * 0.26f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.64f, h * 0.10f), Offset(w * 0.64f, h * 0.26f), strokeWidth = sw, cap = StrokeCap.Round)
            drawRoundRect(color, topLeft = Offset(w * 0.18f, h * 0.20f), size = Size(w * 0.64f, h * 0.68f), cornerRadius = CornerRadius(s * 0.10f), style = stroke)
            drawLine(color, Offset(w * 0.18f, h * 0.38f), Offset(w * 0.82f, h * 0.38f), strokeWidth = sw)
            for (r in 0..3) for (c in 0..2)
                drawCircle(color, s * 0.025f, Offset(w * (0.31f + c * 0.19f), h * (0.50f + r * 0.11f)))
        }

        "seasons" -> {
            // top-left: sun circle + 4 rays; bottom-right: snowflake (6-point asterisk)
            drawCircle(color, s * 0.15f, Offset(w * 0.34f, h * 0.34f), style = stroke)
            drawLine(color, Offset(w * 0.34f, h * 0.10f), Offset(w * 0.34f, h * 0.16f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.10f, h * 0.34f), Offset(w * 0.16f, h * 0.34f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.16f, h * 0.16f), Offset(w * 0.20f, h * 0.20f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.48f, h * 0.20f), Offset(w * 0.52f, h * 0.16f), strokeWidth = sw, cap = StrokeCap.Round)
            // snowflake
            val scx = w * 0.70f; val scy = h * 0.70f; val sr = s * 0.16f
            drawLine(color, Offset(scx, scy - sr), Offset(scx, scy + sr), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(scx - sr, scy), Offset(scx + sr, scy), strokeWidth = sw, cap = StrokeCap.Round)
            val d = sr * 0.70f
            drawLine(color, Offset(scx - d, scy - d), Offset(scx + d, scy + d), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(scx + d, scy - d), Offset(scx - d, scy + d), strokeWidth = sw, cap = StrokeCap.Round)
        }

        "emotions" -> {
            // exaggerated smiley with cheek dots (wider smile than "body")
            drawCircle(color, s * 0.36f, Offset(cx, cy), style = stroke)
            drawCircle(color, s * 0.042f, Offset(w * 0.38f, h * 0.40f))
            drawCircle(color, s * 0.042f, Offset(w * 0.62f, h * 0.40f))
            drawCircle(color, s * 0.055f, Offset(w * 0.28f, h * 0.60f))  // left cheek
            drawCircle(color, s * 0.055f, Offset(w * 0.72f, h * 0.60f))  // right cheek
            drawArc(color, startAngle = 12f, sweepAngle = 156f, useCenter = false,
                topLeft = Offset(w * 0.30f, h * 0.44f), size = Size(w * 0.40f, h * 0.26f), style = stroke)
        }

        "vehicles" -> {
            // car outline: hexagonal body + two wheels
            val car = Path().apply {
                moveTo(w * 0.16f, h * 0.58f)
                lineTo(w * 0.84f, h * 0.58f)
                lineTo(w * 0.84f, h * 0.46f)
                lineTo(w * 0.70f, h * 0.26f)
                lineTo(w * 0.36f, h * 0.26f)
                lineTo(w * 0.16f, h * 0.46f)
                close()
            }
            drawPath(car, color, style = stroke)
            drawCircle(color, s * 0.11f, Offset(w * 0.30f, h * 0.68f), style = stroke)
            drawCircle(color, s * 0.11f, Offset(w * 0.70f, h * 0.68f), style = stroke)
        }

        "fruitsveggies" -> {
            // apple: stem + curved leaf + circular body
            drawLine(color, Offset(cx, h * 0.22f), Offset(cx, h * 0.12f), strokeWidth = sw, cap = StrokeCap.Round)
            val leaf = Path().apply {
                moveTo(cx, h * 0.20f)
                cubicTo(w * 0.62f, h * 0.06f, w * 0.80f, h * 0.24f, w * 0.64f, h * 0.34f)
                close()
            }
            drawPath(leaf, color)
            drawCircle(color, s * 0.30f, Offset(cx, h * 0.58f), style = stroke)
        }

        // ── Math group ─────────────────────────────────────────────────────────

        "addition" -> {
            // bold plus sign
            drawLine(color, Offset(cx, h * 0.18f), Offset(cx, h * 0.82f), strokeWidth = sw * 1.5f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.18f, cy), Offset(w * 0.82f, cy), strokeWidth = sw * 1.5f, cap = StrokeCap.Round)
        }

        "subtraction" -> {
            // 3 dots on top row + bold minus line + crossed-out dot bottom-right
            drawCircle(color, s * 0.07f, Offset(w * 0.24f, h * 0.32f))
            drawCircle(color, s * 0.07f, Offset(w * 0.42f, h * 0.32f))
            drawCircle(color, s * 0.07f, Offset(w * 0.60f, h * 0.32f))
            drawLine(color, Offset(w * 0.16f, cy), Offset(w * 0.84f, cy), strokeWidth = sw * 1.5f, cap = StrokeCap.Round)
            drawCircle(color, s * 0.10f, Offset(w * 0.68f, h * 0.72f), style = stroke)
            drawLine(color, Offset(w * 0.60f, h * 0.64f), Offset(w * 0.76f, h * 0.80f), strokeWidth = sw * 0.9f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.76f, h * 0.64f), Offset(w * 0.60f, h * 0.80f), strokeWidth = sw * 0.9f, cap = StrokeCap.Round)
        }

        "patterns" -> {
            // 2×2 ABAB grid: filled circle (A) / filled rect (B) / filled circle (A) / outlined rect (?)
            drawCircle(color, s * 0.14f, Offset(w * 0.26f, h * 0.28f))
            drawRoundRect(color, topLeft = Offset(w * 0.52f, h * 0.14f), size = Size(s * 0.26f, s * 0.26f), cornerRadius = CornerRadius(s * 0.05f))
            drawCircle(color, s * 0.14f, Offset(w * 0.26f, h * 0.72f))
            drawRoundRect(color, topLeft = Offset(w * 0.52f, h * 0.58f), size = Size(s * 0.26f, s * 0.26f), cornerRadius = CornerRadius(s * 0.05f), style = stroke)
        }

        // ── Language group ─────────────────────────────────────────────────────

        "phonics" -> {
            // letter A (lines) + small sound-wave arcs on the right
            drawLine(color, Offset(cx, h * 0.14f), Offset(w * 0.24f, h * 0.82f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(cx, h * 0.14f), Offset(w * 0.76f, h * 0.82f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.34f, h * 0.54f), Offset(w * 0.66f, h * 0.54f), strokeWidth = sw, cap = StrokeCap.Round)
        }

        "opposites" -> {
            // up arrow (left) + down arrow (right) — classic opposites symbol
            drawLine(color, Offset(w * 0.28f, h * 0.76f), Offset(w * 0.28f, h * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.14f, h * 0.44f), Offset(w * 0.28f, h * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.42f, h * 0.44f), Offset(w * 0.28f, h * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.72f, h * 0.24f), Offset(w * 0.72f, h * 0.72f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.58f, h * 0.56f), Offset(w * 0.72f, h * 0.72f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.86f, h * 0.56f), Offset(w * 0.72f, h * 0.72f), strokeWidth = sw, cap = StrokeCap.Round)
        }

        "rhyming" -> {
            // two connected music notes (beamed eighth notes)
            // note heads
            drawCircle(color, s * 0.12f, Offset(w * 0.30f, h * 0.70f))
            drawCircle(color, s * 0.12f, Offset(w * 0.64f, h * 0.64f))
            // stems
            drawLine(color, Offset(w * 0.40f, h * 0.70f), Offset(w * 0.40f, h * 0.24f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.74f, h * 0.64f), Offset(w * 0.74f, h * 0.18f), strokeWidth = sw, cap = StrokeCap.Round)
            // connecting beam
            drawLine(color, Offset(w * 0.40f, h * 0.24f), Offset(w * 0.74f, h * 0.18f), strokeWidth = sw * 1.4f, cap = StrokeCap.Round)
        }
    }
}
