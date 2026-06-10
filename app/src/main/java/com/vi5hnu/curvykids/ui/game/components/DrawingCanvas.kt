package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.vi5hnu.curvykids.recognition.Point
import com.vi5hnu.curvykids.recognition.Stroke
import android.graphics.Path as AndroidPath

private val INK_COLOR = Color(0xFF5C6BC0)    // friendly crayon blue
private val GUIDE_COLOR = Color(0xFFFFB3C1)  // soft pink dashed tracing guide
private val TRAIL_COLOR = Color(0xFFFF4081)  // bright pink animated trail
private val DOT_COLOR = Color(0xFFFF1744)    // red-ish glowing dot
private const val INK_WIDTH = 30f
private const val GUIDE_WIDTH = 10f
private const val TRAIL_WIDTH = 14f
private const val DOT_RADIUS = 20f

/**
 * Holds the strokes drawn on [DrawingCanvas]. Hoisted so the screen can pass strokes
 * to the recogniser and clear them when advancing to the next character.
 */
class DrawingController {
    // Observable so Canvas redraws as points are added.
    internal val strokes = mutableStateListOf<Stroke>()
    private var current = mutableListOf<Point>()
    private val baseline = System.currentTimeMillis()

    val hasDrawing: Boolean get() = strokes.isNotEmpty()

    /** True while the child's finger is down — lets the UI hide overlays during drawing. */
    var isDrawing by mutableStateOf(false)
        private set

    fun startStroke(x: Float, y: Float) {
        isDrawing = true
        current = mutableListOf(Point(x, y, now()))
        strokes.add(Stroke(current.toList()))
    }

    fun addPoint(x: Float, y: Float) {
        current.add(Point(x, y, now()))
        if (strokes.isNotEmpty()) strokes[strokes.lastIndex] = Stroke(current.toList())
    }

    fun endStroke() {
        isDrawing = false
        if (current.isEmpty()) return
        if (strokes.isNotEmpty()) strokes[strokes.lastIndex] = Stroke(current.toList())
        current = mutableListOf()
    }

    /** Immutable snapshot of completed strokes, safe to pass to the recogniser. */
    fun snapshot(): List<Stroke> = strokes.map { Stroke(it.points.toList()) }

    fun clear() {
        isDrawing = false
        strokes.clear()
        current = mutableListOf()
    }

    private fun now() = System.currentTimeMillis() - baseline
}

@Composable
fun rememberDrawingController(): DrawingController = remember { DrawingController() }

/**
 * The drawing surface. Layers from back to front:
 *   1. Soft dashed glyph outline (tracing guide).
 *   2. Animated trail + glowing dot (when [tracingAnimProgress] > 0).
 *   3. Child's ink, rendered with Catmull-Rom cubic smoothing for a crayon feel.
 *
 * @param tracingAnimProgress 0 = no animation; 0..1 = fraction of the guide outline shown.
 * @param onDrawStart         called on first touch — use to cancel the tracing animation.
 */
@Composable
fun DrawingCanvas(
    controller: DrawingController,
    tracingCharacter: String,
    modifier: Modifier = Modifier,
    inkColor: Color = INK_COLOR,
    tracingAnimProgress: Float = 0f,
    onDrawStart: () -> Unit = {},
) {
    // Track canvas size in composable scope so GlyphData can be cached across frames.
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Rebuild only when character or canvas size changes — not on every animation frame.
    val glyphData = remember(tracingCharacter, canvasSize) {
        if (canvasSize == IntSize.Zero || tracingCharacter.isEmpty()) null
        else buildGlyphData(
            tracingCharacter,
            canvasSize.width.toFloat(),
            canvasSize.height.toFloat(),
        )
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { canvasSize = it }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onDrawStart()   // lets the parent cancel the tracing animation
                    controller.startStroke(down.position.x, down.position.y)
                    down.consume()
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.pressed }
                        if (change != null) {
                            controller.addPoint(change.position.x, change.position.y)
                            change.consume()
                        }
                    } while (event.changes.any { it.pressed })
                    controller.endStroke()
                }
            }
    ) {
        // --- Layer 1: Static dashed guide ---
        if (tracingCharacter.isNotEmpty()) {
            val guidePath = glyphData?.composePath
                ?: buildGlyphPath(tracingCharacter, size.width, size.height)
            drawPath(
                path = guidePath,
                color = GUIDE_COLOR,
                style = DrawStroke(
                    width = GUIDE_WIDTH,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(28f, 26f)),
                ),
            )
        }

        // --- Layer 2: Animated trail + dot (spans ALL glyph contours) ---
        if (tracingAnimProgress > 0f) {
            glyphData?.let { data ->
                // Trail covers all contours up to the current progress fraction.
                drawPath(
                    path = data.trailPathAt(tracingAnimProgress).asComposePath(),
                    color = TRAIL_COLOR,
                    style = DrawStroke(width = TRAIL_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round),
                )
                // Glowing dot at the leading edge.
                data.positionAt(tracingAnimProgress)?.let { centre ->
                    drawCircle(DOT_COLOR.copy(alpha = 0.25f), radius = DOT_RADIUS * 2f, center = centre)
                    drawCircle(DOT_COLOR, radius = DOT_RADIUS, center = centre)
                }
            }
        }

        // --- Layer 3: Child's ink (Catmull-Rom smoothed) ---
        controller.strokes.forEach { stroke ->
            val path = stroke.points.toSmoothedPath()
            drawPath(
                path = path,
                color = inkColor,
                style = DrawStroke(width = INK_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
    }
}

/**
 * Converts raw captured [Point]s to a Catmull-Rom cubic Bezier [Path].
 * This turns the polygonal segments of direct `lineTo` into smooth crayon-like curves,
 * using phantom duplicate endpoints so the first and last segments are also smooth.
 */
private fun List<Point>.toSmoothedPath(): Path {
    if (isEmpty()) return Path()
    val path = Path()
    path.moveTo(first().x, first().y)
    if (size < 3) {
        drop(1).forEach { path.lineTo(it.x, it.y) }
        return path
    }
    val pts = listOf(first()) + this + listOf(last()) // phantom start/end
    for (i in 1 until pts.size - 2) {
        val p0 = pts[i - 1]; val p1 = pts[i]; val p2 = pts[i + 1]; val p3 = pts[i + 2]
        // Catmull-Rom to cubic Bezier control points.
        val cp1x = p1.x + (p2.x - p0.x) / 6f
        val cp1y = p1.y + (p2.y - p0.y) / 6f
        val cp2x = p2.x - (p3.x - p1.x) / 6f
        val cp2y = p2.y - (p3.y - p1.y) / 6f
        path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
    }
    return path
}
