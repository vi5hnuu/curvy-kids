package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.GuideSeg
import com.vi5hnu.curvykids.data.content.GuideStroke
import com.vi5hnu.curvykids.data.content.StrokeGuide
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import kotlinx.coroutines.delay

/** Pen speed. Constant across letters, so a long stroke simply takes longer than a short one. */
private const val PIXELS_PER_SECOND = 320f

/** Bounds on a single stroke's duration, so tiny dots are still visible and long curves aren't dull. */
private const val MIN_STROKE_MS = 380L
private const val MAX_STROKE_MS = 1500L

/** How far along a stroke its numbered label sits, as a fraction of the stroke's length. */
private const val LABEL_OFFSET_FRACTION = 0.12f

/** Beat between strokes, so the child sees the pen lift and reposition. */
private const val BETWEEN_STROKES_MS = 260L

/** How long the finished letter stays on screen before the trail clears for the child to trace. */
private const val HOLD_WHEN_DONE_MS = 900L

/** True when this character has authored stroke-order data and [StrokeOrderDemo] can teach it. */
fun hasStrokeGuide(char: String): Boolean = StrokeGuide.forCharacter(char) != null

/**
 * Shows a child *how* to form a character: the pen travels each stroke in the correct order and
 * direction, one stroke at a time, leaving a trail behind it.
 *
 * This replaces an earlier left-to-right curtain reveal. A wipe shows what a letter looks like
 * but not how it is made — for 'X' it slid a shape into view rather than teaching "down from the
 * top-left, then down from the top-right", which is the whole point of a tracing app.
 *
 * A faint skeleton of the full character is always drawn underneath as the trace target, and
 * numbered dots mark where each stroke begins, so the order stays readable after the animation
 * has finished.
 *
 * @param char     Character to demonstrate; must have data (see [hasStrokeGuide]).
 * @param color    The child's current crayon colour — the trail is drawn in it.
 * @param demoKey  Change this to replay from the beginning.
 */
@Composable
fun StrokeOrderDemo(
    char: String,
    color: Color,
    demoKey: Int,
    modifier: Modifier = Modifier,
) {
    val guide = remember(char) { StrokeGuide.forCharacter(char) } ?: return

    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    val textMeasurer = rememberTextMeasurer()

    // The character is laid out in a centred square so it never distorts on a wide or tall card.
    val geometry = remember(guide, boxSize) {
        if (boxSize == IntSize.Zero) null else buildGeometry(guide, boxSize)
    }

    // One clock for the whole character; progress is in "strokes", so 1.7 means stroke 2 is 70% in.
    val progress = remember { Animatable(0f) }
    var trailVisible by remember { mutableStateOf(true) }

    LaunchedEffect(demoKey, geometry) {
        val g = geometry ?: return@LaunchedEffect
        trailVisible = true
        progress.snapTo(0f)
        delay(200)
        g.strokes.forEachIndexed { i, stroke ->
            progress.animateTo(
                targetValue = i + 1f,
                animationSpec = tween(durationMillis = stroke.durationMs.toInt(), easing = LinearEasing),
            )
            if (i != g.strokes.lastIndex) delay(BETWEEN_STROKES_MS)
        }
        delay(HOLD_WHEN_DONE_MS)
        // Clear the coloured trail so the child traces on a clean surface; the faint skeleton
        // and the numbered start dots stay as the guide.
        trailVisible = false
    }

    Canvas(modifier = modifier.onSizeChanged { boxSize = it }) {
        val g = geometry ?: return@Canvas
        val skeletonStyle = Stroke(
            width = 26.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val trailStyle = Stroke(
            width = 22.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )

        // ── The trace target ──────────────────────────────────────────────────────
        g.strokes.forEach { drawPath(it.path, SKELETON_COLOR, style = skeletonStyle) }

        if (!trailVisible) {
            drawStartMarkers(g, color, textMeasurer)
            return@Canvas
        }

        // ── The pen's trail ───────────────────────────────────────────────────────
        val p = progress.value
        g.strokes.forEachIndexed { i, stroke ->
            val local = (p - i).coerceIn(0f, 1f)
            if (local <= 0f) return@forEachIndexed
            val drawn = Path()
            stroke.measure.getSegment(0f, stroke.measure.length * local, drawn, true)
            drawPath(drawn, color.copy(alpha = 0.55f), style = trailStyle)
        }

        // ── The pen itself, on the stroke currently being drawn ───────────────────
        val activeIndex = p.toInt().coerceAtMost(g.strokes.lastIndex)
        val active = g.strokes[activeIndex]
        val local = (p - activeIndex).coerceIn(0f, 1f)
        if (local > 0f && local < 1f) {
            val pos = active.measure.getPosition(active.measure.length * local)
            drawCircle(Color.White, radius = 15.dp.toPx(), center = pos)
            drawCircle(color, radius = 11.dp.toPx(), center = pos)
        }

        drawStartMarkers(g, color, textMeasurer)
    }
}

/** Faint grey the untraced character sits in — the same tone the old ghost letter used. */
private val SKELETON_COLOR = Color(0xFFE4EDF4)

/**
 * Numbered dots showing where each stroke starts. Kept visible for the whole time the guide is
 * on screen so a child who missed the animation can still read the order.
 */
private fun DrawScope.drawStartMarkers(
    geometry: Geometry,
    color: Color,
    textMeasurer: TextMeasurer,
) {
    // A single-stroke character has no order to communicate, so a "1" would just be clutter.
    if (geometry.strokes.size < 2) return

    geometry.strokes.forEachIndexed { i, stroke ->
        val r = 11.dp.toPx()
        drawCircle(Color.White, radius = r, center = stroke.label)
        drawCircle(color, radius = r, center = stroke.label, style = Stroke(width = 2.5.dp.toPx()))
        val label = textMeasurer.measure(
            text = "${i + 1}",
            style = TextStyle(
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = color,
            ),
        )
        drawText(
            textLayoutResult = label,
            topLeft = Offset(
                stroke.label.x - label.size.width / 2f,
                stroke.label.y - label.size.height / 2f,
            ),
        )
    }
}

/** A stroke resolved into canvas pixels, with everything the draw and timing code needs. */
private class StrokeGeometry(
    val path: Path,
    val measure: PathMeasure,
    val start: Offset,
    /** Where the numbered label sits: just along the stroke, so co-located starts don't stack. */
    val label: Offset,
    val durationMs: Long,
)

private class Geometry(val strokes: List<StrokeGeometry>)

/**
 * Scales the unit-box guide into the largest centred square that fits [boxSize], then
 * pre-measures each stroke so the animation can pace itself by real path length.
 */
private fun buildGeometry(guide: List<GuideStroke>, boxSize: IntSize): Geometry {
    val side = minOf(boxSize.width, boxSize.height).toFloat()
    val originX = (boxSize.width - side) / 2f
    val originY = (boxSize.height - side) / 2f
    fun px(x: Float, y: Float) = Offset(originX + x * side, originY + y * side)

    return Geometry(
        guide.map { stroke ->
            val start = px(stroke.startX, stroke.startY)
            val path = Path().apply {
                moveTo(start.x, start.y)
                stroke.segments.forEach { seg ->
                    when (seg) {
                        is GuideSeg.Line -> {
                            val p = px(seg.x, seg.y)
                            lineTo(p.x, p.y)
                        }
                        is GuideSeg.Quad -> {
                            val c = px(seg.cx, seg.cy)
                            val p = px(seg.x, seg.y)
                            quadraticTo(c.x, c.y, p.x, p.y)
                        }
                    }
                }
            }
            val measure = PathMeasure().apply { setPath(path, false) }
            val duration = (measure.length / PIXELS_PER_SECOND * 1000f).toLong()
                .coerceIn(MIN_STROKE_MS, MAX_STROKE_MS)
            // Nudge the label a little way along the stroke. Two strokes that begin at the same
            // point (A's apex, M's and N's first corner) then separate along their own paths.
            val label = measure.getPosition(
                (measure.length * LABEL_OFFSET_FRACTION).coerceAtMost(measure.length)
            )
            StrokeGeometry(path, measure, start, label, duration)
        }
    )
}
