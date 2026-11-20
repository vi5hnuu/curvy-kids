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
import androidx.compose.ui.geometry.Rect
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Writing topics legitimately want their letters shown — others get a vector illustration. */
private val LETTER_TOPICS = setOf(
    "upper", "lower", "numbers",
    // The Hindi tracks are writing topics too — their Devanagari glyph is the thing being taught.
    "hindi_vowels", "hindi_consonants", "barakhadi",
)

/** Topic ids that [drawTopicGlyph] has a hand-drawn vector for; others fall back to their glyph. */
private val GLYPH_TOPICS = setOf(
    "shapes", "colors", "count", "words", "animals", "body", "days", "draw",
    "memory", "bigsmall", "oddone", "catch", "months", "seasons", "emotions",
    "vehicles", "fruitsveggies", "addition", "subtraction", "patterns",
    "phonics", "opposites", "rhyming",
    "time", "fractions", "stories", "birds", "sea", "insects", "nature", "weather",
    "food", "clothes", "toys", "instruments", "home", "jobs", "places", "sports",
)

/**
 * The icon shown inside a topic card's badge. Writing topics ("Aa", "bd", "12") keep their
 * readable letters; every other topic renders a friendly white vector illustration instead of
 * an svg, so the cards feel hand-crafted and consistent.
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
        when {
            // Writing topics ("Aa", "bd", "12") show their readable letters.
            topicId in LETTER_TOPICS -> Text(
                text = fallback,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = if (fallback.length >= 2) 24.sp else 28.sp,
                color = color,
            )
            // Topics with a bespoke vector illustration.
            topicId in GLYPH_TOPICS -> Canvas(modifier = Modifier.size(30.dp)) { drawTopicGlyph(topicId, color) }
            // Unreachable in practice: every topic id is covered above. Kept as a visible
            // marker so a newly added topic shows something rather than an empty badge.
            else -> Text(
                text = fallback.ifEmpty { "?" },
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = color,
            )
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
            // Apple: solid body with a stem and leaf, so it reads at badge size.
            drawCircle(color, s * 0.30f, Offset(cx, h * 0.60f))
            drawLine(color, Offset(cx, h * 0.30f), Offset(cx, h * 0.16f), strokeWidth = sw, cap = StrokeCap.Round)
            val leaf = Path().apply {
                moveTo(cx, h * 0.24f)
                cubicTo(w * 0.64f, h * 0.08f, w * 0.84f, h * 0.26f, w * 0.66f, h * 0.36f)
                close()
            }
            drawPath(leaf, color)
        }

        // ── Math group ─────────────────────────────────────────────────────────

        "addition" -> {
            // bold plus sign
            drawLine(color, Offset(cx, h * 0.18f), Offset(cx, h * 0.82f), strokeWidth = sw * 1.5f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.18f, cy), Offset(w * 0.82f, cy), strokeWidth = sw * 1.5f, cap = StrokeCap.Round)
        }

        "subtraction" -> {
            // A row of dots with two taken away. Dots above *and* below a bar spell out the
            // division sign, so the removed ones sit on the same line, crossed through.
            drawLine(color, Offset(w * 0.14f, cy), Offset(w * 0.86f, cy), strokeWidth = sw * 1.6f, cap = StrokeCap.Round)
            drawCircle(color, s * 0.09f, Offset(w * 0.26f, h * 0.26f))
            drawCircle(color, s * 0.09f, Offset(w * 0.50f, h * 0.26f))
            drawCircle(color, s * 0.09f, Offset(w * 0.74f, h * 0.26f))
            drawCircle(color.copy(alpha = 0.4f), s * 0.09f, Offset(w * 0.38f, h * 0.74f))
            drawCircle(color.copy(alpha = 0.4f), s * 0.09f, Offset(w * 0.62f, h * 0.74f))
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
        // ── Time & number sense ────────────────────────────────────────────────

        "time" -> {
            // clock face with hands at 3 o'clock
            drawCircle(color, s * 0.38f, Offset(cx, cy), style = stroke)
            drawLine(color, Offset(cx, cy), Offset(cx, h * 0.28f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(cx, cy), Offset(w * 0.74f, cy), strokeWidth = sw, cap = StrokeCap.Round)
        }

        "fractions" -> {
            // a circle split in half, one half filled
            val half = Path().apply {
                moveTo(cx, cy)
                arcTo(Rect(Offset(w * 0.14f, h * 0.14f), Size(s * 0.72f, s * 0.72f)), -90f, 180f, false)
                close()
            }
            drawPath(half, color)
            drawCircle(color, s * 0.36f, Offset(cx, cy), style = stroke)
        }

        "stories" -> {
            // open book: two pages meeting at a spine
            val left = Path().apply {
                moveTo(w * 0.5f, h * 0.3f)
                cubicTo(w * 0.36f, h * 0.2f, w * 0.24f, h * 0.22f, w * 0.14f, h * 0.26f)
                lineTo(w * 0.14f, h * 0.74f)
                cubicTo(w * 0.26f, h * 0.7f, w * 0.38f, h * 0.7f, w * 0.5f, h * 0.78f)
                close()
            }
            val right = Path().apply {
                moveTo(w * 0.5f, h * 0.3f)
                cubicTo(w * 0.64f, h * 0.2f, w * 0.76f, h * 0.22f, w * 0.86f, h * 0.26f)
                lineTo(w * 0.86f, h * 0.74f)
                cubicTo(w * 0.74f, h * 0.7f, w * 0.62f, h * 0.7f, w * 0.5f, h * 0.78f)
                close()
            }
            drawPath(left, color, style = stroke)
            drawPath(right, color, style = stroke)
        }

        // ── Nature & animals ───────────────────────────────────────────────────

        "birds" -> {
            // side-on bird: round body, wing, beak
            drawCircle(color, s * 0.26f, Offset(w * 0.46f, h * 0.56f))
            drawCircle(color, s * 0.15f, Offset(w * 0.68f, h * 0.34f))
            val beak = Path().apply {
                moveTo(w * 0.8f, h * 0.34f); lineTo(w * 0.94f, h * 0.4f); lineTo(w * 0.8f, h * 0.44f); close()
            }
            drawPath(beak, color)
            drawArc(color, 160f, 130f, false,
                topLeft = Offset(w * 0.26f, h * 0.36f), size = Size(s * 0.4f, s * 0.4f), style = stroke)
        }

        "sea" -> {
            // fish: teardrop body + tail fin
            val body = Path().apply {
                moveTo(w * 0.74f, cy)
                cubicTo(w * 0.62f, h * 0.28f, w * 0.3f, h * 0.28f, w * 0.2f, cy)
                cubicTo(w * 0.3f, h * 0.72f, w * 0.62f, h * 0.72f, w * 0.74f, cy)
                close()
            }
            drawPath(body, color)
            val tail = Path().apply {
                moveTo(w * 0.74f, cy); lineTo(w * 0.92f, h * 0.34f); lineTo(w * 0.92f, h * 0.66f); close()
            }
            drawPath(tail, color)
        }

        "insects" -> {
            // ladybug: body, centre line, spots, antennae
            drawCircle(color, s * 0.32f, Offset(cx, h * 0.58f))
            drawLine(color, Offset(cx, h * 0.26f), Offset(cx, h * 0.9f), strokeWidth = sw * 0.7f)
            drawLine(color, Offset(w * 0.4f, h * 0.2f), Offset(w * 0.34f, h * 0.08f), strokeWidth = sw * 0.7f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.6f, h * 0.2f), Offset(w * 0.66f, h * 0.08f), strokeWidth = sw * 0.7f, cap = StrokeCap.Round)
        }

        "nature" -> {
            // leafy tree: canopy circles + trunk
            drawCircle(color, s * 0.2f, Offset(w * 0.36f, h * 0.4f))
            drawCircle(color, s * 0.22f, Offset(w * 0.62f, h * 0.36f))
            drawCircle(color, s * 0.2f, Offset(cx, h * 0.56f))
            drawLine(color, Offset(cx, h * 0.6f), Offset(cx, h * 0.9f), strokeWidth = sw * 1.2f, cap = StrokeCap.Round)
        }

        "weather" -> {
            // sun peeking from behind a cloud
            drawCircle(color, s * 0.17f, Offset(w * 0.66f, h * 0.32f))
            for (i in 0 until 8) {
                val a = (i * 45f) * (PI / 180f).toFloat()
                val ox = w * 0.66f; val oy = h * 0.32f
                drawLine(color,
                    Offset(ox + cos(a) * s * 0.23f, oy + sin(a) * s * 0.23f),
                    Offset(ox + cos(a) * s * 0.3f, oy + sin(a) * s * 0.3f),
                    strokeWidth = sw * 0.7f, cap = StrokeCap.Round)
            }
            drawCircle(color, s * 0.16f, Offset(w * 0.36f, h * 0.66f))
            drawCircle(color, s * 0.2f, Offset(w * 0.56f, h * 0.68f))
            drawRoundRect(color, topLeft = Offset(w * 0.26f, h * 0.66f), size = Size(s * 0.46f, s * 0.22f),
                cornerRadius = CornerRadius(s * 0.11f))
        }

        // ── Things ─────────────────────────────────────────────────────────────

        "food" -> {
            // Pizza slice: crust along the bottom edge plus topping dots, so it is not just a
            // bare triangle.
            val slice = Path().apply {
                moveTo(cx, h * 0.12f)
                lineTo(w * 0.84f, h * 0.74f)
                lineTo(w * 0.16f, h * 0.74f)
                close()
            }
            drawPath(slice, color)
            drawRoundRect(
                color,
                topLeft = Offset(w * 0.12f, h * 0.74f),
                size = Size(w * 0.76f, h * 0.14f),
                cornerRadius = CornerRadius(s * 0.07f),
            )
            val holes = color.copy(alpha = 0.35f)
            drawCircle(holes, s * 0.06f, Offset(w * 0.44f, h * 0.42f))
            drawCircle(holes, s * 0.05f, Offset(w * 0.60f, h * 0.56f))
            drawCircle(holes, s * 0.045f, Offset(w * 0.38f, h * 0.62f))
        }

        "clothes" -> {
            // t-shirt outline
            val shirt = Path().apply {
                moveTo(w * 0.34f, h * 0.2f)
                lineTo(w * 0.16f, h * 0.34f); lineTo(w * 0.26f, h * 0.46f); lineTo(w * 0.3f, h * 0.42f)
                lineTo(w * 0.3f, h * 0.84f); lineTo(w * 0.7f, h * 0.84f); lineTo(w * 0.7f, h * 0.42f)
                lineTo(w * 0.74f, h * 0.46f); lineTo(w * 0.84f, h * 0.34f); lineTo(w * 0.66f, h * 0.2f)
                cubicTo(w * 0.62f, h * 0.3f, w * 0.38f, h * 0.3f, w * 0.34f, h * 0.2f)
                close()
            }
            drawPath(shirt, color)
        }

        "toys" -> {
            // teddy bear: ears + head + muzzle
            drawCircle(color, s * 0.13f, Offset(w * 0.28f, h * 0.28f))
            drawCircle(color, s * 0.13f, Offset(w * 0.72f, h * 0.28f))
            drawCircle(color, s * 0.32f, Offset(cx, h * 0.56f))
            drawCircle(color.copy(alpha = 0.45f), s * 0.13f, Offset(cx, h * 0.66f))
        }

        "instruments" -> {
            // guitar: body, waist, neck, strings
            drawCircle(color, s * 0.22f, Offset(w * 0.4f, h * 0.68f))
            drawCircle(color, s * 0.16f, Offset(w * 0.56f, h * 0.48f))
            drawLine(color, Offset(w * 0.62f, h * 0.42f), Offset(w * 0.88f, h * 0.16f),
                strokeWidth = sw * 1.3f, cap = StrokeCap.Round)
        }

        "home" -> {
            // sofa: back, seat, arms
            drawRoundRect(color, topLeft = Offset(w * 0.18f, h * 0.34f), size = Size(s * 0.64f, s * 0.24f),
                cornerRadius = CornerRadius(s * 0.1f))
            drawRoundRect(color, topLeft = Offset(w * 0.14f, h * 0.52f), size = Size(s * 0.72f, s * 0.22f),
                cornerRadius = CornerRadius(s * 0.08f))
            drawLine(color, Offset(w * 0.24f, h * 0.74f), Offset(w * 0.24f, h * 0.86f), strokeWidth = sw, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.76f, h * 0.74f), Offset(w * 0.76f, h * 0.86f), strokeWidth = sw, cap = StrokeCap.Round)
        }

        // ── People & places ────────────────────────────────────────────────────

        "jobs" -> {
            // person in a hard hat
            drawArc(color, 180f, 180f, true,
                topLeft = Offset(w * 0.28f, h * 0.16f), size = Size(s * 0.44f, s * 0.34f))
            drawLine(color, Offset(w * 0.22f, h * 0.34f), Offset(w * 0.78f, h * 0.34f), strokeWidth = sw, cap = StrokeCap.Round)
            drawCircle(color, s * 0.13f, Offset(cx, h * 0.5f))
            drawArc(color, 180f, 180f, true,
                topLeft = Offset(w * 0.26f, h * 0.62f), size = Size(s * 0.48f, s * 0.4f))
        }

        "places" -> {
            // house: roof + body + door
            val roof = Path().apply {
                moveTo(cx, h * 0.16f); lineTo(w * 0.88f, h * 0.48f); lineTo(w * 0.12f, h * 0.48f); close()
            }
            drawPath(roof, color)
            drawRect(color, topLeft = Offset(w * 0.24f, h * 0.48f), size = Size(s * 0.52f, s * 0.36f))
            drawRoundRect(color.copy(alpha = 0.45f), topLeft = Offset(w * 0.43f, h * 0.62f),
                size = Size(s * 0.14f, s * 0.22f), cornerRadius = CornerRadius(s * 0.03f))
        }

        "sports" -> {
            // football with pentagon panels suggested by lines
            drawCircle(color, s * 0.36f, Offset(cx, cy), style = stroke)
            drawCircle(color, s * 0.11f, Offset(cx, cy))
            drawLine(color, Offset(cx, h * 0.36f), Offset(cx, h * 0.16f), strokeWidth = sw * 0.8f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.38f, h * 0.58f), Offset(w * 0.2f, h * 0.68f), strokeWidth = sw * 0.8f, cap = StrokeCap.Round)
            drawLine(color, Offset(w * 0.62f, h * 0.58f), Offset(w * 0.8f, h * 0.68f), strokeWidth = sw * 0.8f, cap = StrokeCap.Round)
        }
    }
}