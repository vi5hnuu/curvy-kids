package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.recognition.Point
import com.vi5hnu.curvykids.recognition.Stroke

private val DEFAULT_INK_COLOR = Color(0xFF5C6BC0)

/** Crayon thickness. In dp, not raw pixels, so the stroke looks the same on every density. */
private val INK_WIDTH = 10.dp

/**
 * Holds the strokes drawn on [DrawingCanvas]. Hoisted so the screen can pass strokes
 * to the recogniser and clear them when advancing to the next character.
 *
 * Each finished stroke's smoothed [Path] is cached alongside its points. Rebuilding every
 * path on every frame made drawing progressively laggier the more the child drew, since a
 * single new sample invalidated and re-derived the whole canvas.
 */
class DrawingController(initial: List<Stroke> = emptyList()) {

    /** Finished strokes, with their smoothed path built once when the stroke ended. */
    private val finished = mutableStateListOf<RenderedStroke>().apply {
        addAll(initial.map { RenderedStroke(it, it.points.toSmoothedPath()) })
    }

    /** The stroke currently under the child's finger; its path is rebuilt as points arrive. */
    private var live by mutableStateOf<RenderedStroke?>(null)

    private val baseline = System.currentTimeMillis()

    /** Everything to paint this frame — finished strokes plus the in-progress one. */
    internal val renderable: List<RenderedStroke> get() = finished + listOfNotNull(live)

    val hasDrawing: Boolean get() = finished.isNotEmpty() || live != null

    /** True while the child's finger is down — lets the UI hide overlays during drawing. */
    var isDrawing by mutableStateOf(false)
        private set

    fun startStroke(x: Float, y: Float) {
        isDrawing = true
        val points = listOf(Point(x, y, now()))
        live = RenderedStroke(Stroke(points), points.toSmoothedPath())
    }

    fun addPoint(x: Float, y: Float) {
        val current = live ?: return
        val points = current.stroke.points + Point(x, y, now())
        live = RenderedStroke(Stroke(points), points.toSmoothedPath())
    }

    fun endStroke() {
        isDrawing = false
        live?.let { finished.add(it) }
        live = null
    }

    /** Immutable snapshot of the strokes, safe to pass to the recogniser. */
    fun snapshot(): List<Stroke> = renderable.map { Stroke(it.stroke.points.toList()) }

    fun clear() {
        isDrawing = false
        finished.clear()
        live = null
    }

    private fun now() = System.currentTimeMillis() - baseline

    companion object {
        /**
         * Persists the drawn strokes across configuration changes. Without this a rotation
         * silently erased whatever the child had drawn, which reads as the app breaking.
         *
         * Points are flattened to a float list; the capture times are not saved because the
         * recogniser only uses relative timing within a stroke and a restored drawing is about
         * to be re-timed by the next touch anyway.
         */
        val Saver: Saver<DrawingController, List<List<Float>>> = Saver(
            save = { controller ->
                controller.snapshot().map { stroke ->
                    stroke.points.flatMap { listOf(it.x, it.y) }
                }
            },
            restore = { saved ->
                DrawingController(
                    saved.map { flat ->
                        Stroke(flat.chunked(2).map { (x, y) -> Point(x, y, 0L) })
                    }
                )
            },
        )
    }
}

/** A stroke together with the smoothed path derived from it, so the path is built only once. */
internal data class RenderedStroke(val stroke: Stroke, val path: Path)

@Composable
fun rememberDrawingController(): DrawingController =
    rememberSaveable(saver = DrawingController.Saver) { DrawingController() }

/**
 * Drawing surface — renders the child's ink using Catmull-Rom cubic smoothing.
 * Visual guidance (ghost letter, DemoWipe animation) is handled by the parent composable;
 * this canvas is intentionally clean so ink stands out clearly.
 *
 * @param onDrawStart Called on the first touch of each gesture — lets the parent pause
 *                    any overlay animations while the child is drawing.
 */
@Composable
fun DrawingCanvas(
    controller: DrawingController,
    modifier: Modifier = Modifier,
    inkColor: Color = DEFAULT_INK_COLOR,
    inkWidth: Dp = INK_WIDTH,
    onDrawStart: () -> Unit = {},
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    onDrawStart()
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
        val style = DrawStroke(
            width = inkWidth.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        controller.renderable.forEach { drawPath(path = it.path, color = inkColor, style = style) }
    }
}

/**
 * Converts raw captured [Point]s to a Catmull-Rom cubic Bezier [Path].
 * Phantom duplicate endpoints ensure the first and last segments are also smooth.
 */
private fun List<Point>.toSmoothedPath(): Path {
    if (isEmpty()) return Path()
    val path = Path()
    path.moveTo(first().x, first().y)
    if (size < 3) {
        drop(1).forEach { path.lineTo(it.x, it.y) }
        return path
    }
    val pts = listOf(first()) + this + listOf(last())
    for (i in 1 until pts.size - 2) {
        val p0 = pts[i - 1]; val p1 = pts[i]; val p2 = pts[i + 1]; val p3 = pts[i + 2]
        val cp1x = p1.x + (p2.x - p0.x) / 6f
        val cp1y = p1.y + (p2.y - p0.y) / 6f
        val cp2x = p2.x - (p3.x - p1.x) / 6f
        val cp2y = p2.y - (p3.y - p1.y) / 6f
        path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
    }
    return path
}
