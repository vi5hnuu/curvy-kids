package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The app's UI-chrome icons, drawn as vectors.
 *
 * These are the symbols that are *interface* rather than *content* — the star currency, the
 * listen button, the eraser. The illustrated SVG library under `assets/kids/` covers content
 * (animals, food, shapes…) but has no art for chrome, and the app must not fall back to system
 * emoji: emoji glyphs vary per OEM/Android version, ignore `color` (colour-emoji fonts render
 * their own palette), and scale with the user's font setting rather than the layout.
 *
 * Drawn with the same normalised-coordinate idiom as [TopicGlyph] so both icon systems stay
 * visually consistent.
 */
enum class KidIcon {
    Star, Flame, Speaker, Eraser, Lock, Sparkle, Check, Pointer,
    ArrowBack, ArrowForward, Undo, Trophy,
}

/**
 * Renders [icon] in [color], filling whatever size [modifier] gives it.
 *
 * @param contentDescription TalkBack label; pass null for icons that merely decorate a control
 *        whose own label already says the same thing.
 */
@Composable
fun KidIconImage(
    icon: KidIcon,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    contentDescription: String? = null,
) {
    val semantics = if (contentDescription != null) {
        Modifier.semantics { this.contentDescription = contentDescription }
    } else {
        Modifier
    }
    Canvas(modifier = modifier.then(semantics)) { drawKidIcon(icon, color) }
}

/** Builds an n-pointed star path centred on [cx],[cy]. Shared by [KidIcon.Star] and [KidIcon.Sparkle]. */
private fun starPath(cx: Float, cy: Float, outer: Float, inner: Float, points: Int): Path {
    val path = Path()
    // Start at -90° so a point faces straight up rather than sideways.
    val step = PI / points
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outer else inner
        val a = -PI / 2 + i * step
        val x = cx + (r * cos(a)).toFloat()
        val y = cy + (r * sin(a)).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun DrawScope.drawKidIcon(icon: KidIcon, color: Color) {
    val w = size.width
    val h = size.height
    val s = minOf(w, h)
    val cx = w / 2f
    val cy = h / 2f
    val sw = s * 0.11f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (icon) {
        KidIcon.Star -> drawPath(starPath(cx, cy, s * 0.46f, s * 0.19f, 5), color)

        KidIcon.Sparkle -> {
            // A four-point twinkle plus two small satellites — reads as "new / earned".
            drawPath(starPath(cx, cy * 0.95f, s * 0.4f, s * 0.1f, 4), color)
            drawPath(starPath(w * 0.82f, h * 0.76f, s * 0.15f, s * 0.04f, 4), color)
            drawPath(starPath(w * 0.2f, h * 0.8f, s * 0.11f, s * 0.03f, 4), color)
        }

        KidIcon.Flame -> {
            // Outer flame body: a teardrop that curls to a point at the top.
            val flame = Path().apply {
                moveTo(cx, h * 0.08f)
                cubicTo(w * 0.86f, h * 0.36f, w * 0.84f, h * 0.62f, cx, h * 0.94f)
                cubicTo(w * 0.16f, h * 0.62f, w * 0.14f, h * 0.36f, cx, h * 0.08f)
                close()
            }
            drawPath(flame, color)
            // Inner core, punched out at 45% alpha so the flame reads on a flat fill.
            val core = Path().apply {
                moveTo(cx, h * 0.44f)
                cubicTo(w * 0.68f, h * 0.6f, w * 0.66f, h * 0.74f, cx, h * 0.86f)
                cubicTo(w * 0.34f, h * 0.74f, w * 0.32f, h * 0.6f, cx, h * 0.44f)
                close()
            }
            drawPath(core, color.copy(alpha = 0.45f))
        }

        KidIcon.Speaker -> {
            // Cone
            val cone = Path().apply {
                moveTo(w * 0.44f, h * 0.34f)
                lineTo(w * 0.24f, h * 0.34f)
                lineTo(w * 0.24f, h * 0.66f)
                lineTo(w * 0.44f, h * 0.66f)
                lineTo(w * 0.44f, h * 0.86f)
                lineTo(w * 0.14f, h * 0.5f)
                lineTo(w * 0.44f, h * 0.14f)
                close()
            }
            drawPath(cone, color)
            // Two sound waves
            drawArc(color, -55f, 110f, false,
                topLeft = Offset(w * 0.34f, h * 0.28f), size = Size(w * 0.34f, h * 0.44f), style = stroke)
            drawArc(color, -55f, 110f, false,
                topLeft = Offset(w * 0.42f, h * 0.16f), size = Size(w * 0.46f, h * 0.68f), style = stroke)
        }

        KidIcon.Eraser -> {
            // A tilted eraser block with a lighter "worn" tip, plus crumbs.
            val body = Path().apply {
                moveTo(w * 0.3f, h * 0.72f)
                lineTo(w * 0.62f, h * 0.16f)
                lineTo(w * 0.88f, h * 0.34f)
                lineTo(w * 0.56f, h * 0.9f)
                close()
            }
            drawPath(body, color)
            val tip = Path().apply {
                moveTo(w * 0.3f, h * 0.72f)
                lineTo(w * 0.42f, h * 0.51f)
                lineTo(w * 0.68f, h * 0.69f)
                lineTo(w * 0.56f, h * 0.9f)
                close()
            }
            drawPath(tip, color.copy(alpha = 0.5f))
            drawCircle(color, s * 0.05f, Offset(w * 0.16f, h * 0.86f))
            drawCircle(color, s * 0.035f, Offset(w * 0.3f, h * 0.94f))
        }

        KidIcon.Lock -> {
            // Shackle
            drawArc(color, 180f, 180f, false,
                topLeft = Offset(w * 0.28f, h * 0.14f), size = Size(w * 0.44f, h * 0.44f), style = stroke)
            // Body
            drawRoundRect(color,
                topLeft = Offset(w * 0.2f, h * 0.44f), size = Size(w * 0.6f, h * 0.44f),
                cornerRadius = CornerRadius(s * 0.12f))
            // Keyhole, punched in the background colour's place by reducing alpha.
            drawCircle(color.copy(alpha = 0.35f), s * 0.08f, Offset(cx, h * 0.62f))
            drawLine(color.copy(alpha = 0.35f), Offset(cx, h * 0.62f), Offset(cx, h * 0.76f),
                strokeWidth = sw * 0.9f, cap = StrokeCap.Round)
        }

        KidIcon.Check -> {
            val tick = Path().apply {
                moveTo(w * 0.2f, h * 0.54f)
                lineTo(w * 0.42f, h * 0.75f)
                lineTo(w * 0.8f, h * 0.27f)
            }
            drawPath(tick, color, style = Stroke(width = s * 0.16f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }

        KidIcon.Pointer -> {
            // A pointing hand: index finger plus a rounded fist, with a tap ripple.
            drawRoundRect(color,
                topLeft = Offset(w * 0.42f, h * 0.22f), size = Size(w * 0.16f, h * 0.34f),
                cornerRadius = CornerRadius(s * 0.08f))
            drawRoundRect(color,
                topLeft = Offset(w * 0.3f, h * 0.46f), size = Size(w * 0.42f, h * 0.36f),
                cornerRadius = CornerRadius(s * 0.14f))
            drawArc(color.copy(alpha = 0.5f), 200f, 140f, false,
                topLeft = Offset(w * 0.12f, h * 0.06f), size = Size(w * 0.76f, h * 0.5f),
                style = Stroke(width = sw * 0.8f, cap = StrokeCap.Round))
        }

        KidIcon.ArrowBack, KidIcon.ArrowForward -> {
            // One geometry, mirrored — keeps the two arrows exactly symmetrical.
            val dir = if (icon == KidIcon.ArrowBack) -1f else 1f
            val tipX = cx + dir * s * 0.26f
            val tailX = cx - dir * s * 0.26f
            drawLine(color, Offset(tailX, cy), Offset(tipX, cy), strokeWidth = sw, cap = StrokeCap.Round)
            val head = Path().apply {
                moveTo(tipX, cy)
                lineTo(tipX - dir * s * 0.24f, cy - s * 0.22f)
                moveTo(tipX, cy)
                lineTo(tipX - dir * s * 0.24f, cy + s * 0.22f)
            }
            drawPath(head, color, style = stroke)
        }

        KidIcon.Undo -> {
            // Counter-clockwise arc with an arrowhead at the start.
            drawArc(color, 20f, 300f, false,
                topLeft = Offset(w * 0.18f, h * 0.18f), size = Size(w * 0.64f, h * 0.64f), style = stroke)
            val head = Path().apply {
                moveTo(w * 0.2f, h * 0.34f)
                lineTo(w * 0.2f, h * 0.1f)
                moveTo(w * 0.2f, h * 0.34f)
                lineTo(w * 0.44f, h * 0.34f)
            }
            drawPath(head, color, style = stroke)
        }

        KidIcon.Trophy -> {
            // Cup
            val cup = Path().apply {
                moveTo(w * 0.3f, h * 0.16f)
                lineTo(w * 0.7f, h * 0.16f)
                lineTo(w * 0.66f, h * 0.5f)
                cubicTo(w * 0.64f, h * 0.62f, w * 0.36f, h * 0.62f, w * 0.34f, h * 0.5f)
                close()
            }
            drawPath(cup, color)
            // Handles
            drawArc(color, 270f, 180f, false,
                topLeft = Offset(w * 0.62f, h * 0.2f), size = Size(w * 0.26f, h * 0.26f), style = stroke)
            drawArc(color, 90f, 180f, false,
                topLeft = Offset(w * 0.12f, h * 0.2f), size = Size(w * 0.26f, h * 0.26f), style = stroke)
            // Stem + base
            drawRect(color, topLeft = Offset(w * 0.45f, h * 0.6f), size = Size(w * 0.1f, h * 0.16f))
            drawRoundRect(color,
                topLeft = Offset(w * 0.28f, h * 0.76f), size = Size(w * 0.44f, h * 0.12f),
                cornerRadius = CornerRadius(s * 0.05f))
        }
    }
}
