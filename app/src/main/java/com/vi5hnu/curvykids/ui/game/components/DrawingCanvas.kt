package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.input.pointer.pointerInput
import com.vi5hnu.curvykids.recognition.Point
import com.vi5hnu.curvykids.recognition.Stroke

private val DEFAULT_INK_COLOR = Color(0xFF5C6BC0)
private const val INK_WIDTH = 30f

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
        // Child's ink — Catmull-Rom smoothed for a natural crayon feel.
        controller.strokes.forEach { stroke ->
            drawPath(
                path = stroke.points.toSmoothedPath(),
                color = inkColor,
                style = DrawStroke(width = INK_WIDTH, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        }
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
