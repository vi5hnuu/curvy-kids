package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.input.pointer.pointerInput
import com.vi5hnu.curvykids.recognition.Point
import com.vi5hnu.curvykids.recognition.Stroke

private val INK_COLOR = Color(0xFFA9BD3E) // matches the original web app stroke colour
private const val INK_WIDTH = 20f

/**
 * Holds the strokes drawn on the [DrawingCanvas]. Hoisted so the screen can read the
 * strokes for recognition and clear them when advancing to the next character.
 */
class DrawingController {
    // Observable so the Canvas redraws as points are added.
    internal val strokes = mutableStateListOf<Stroke>()
    private var current = mutableListOf<Point>()
    private val baseline = System.currentTimeMillis()

    val hasDrawing: Boolean get() = strokes.isNotEmpty()

    fun startStroke(x: Float, y: Float) {
        current = mutableListOf(Point(x, y, now()))
        // Push a snapshot now so the in-progress stroke renders live.
        strokes.add(Stroke(current.toList()))
    }

    fun addPoint(x: Float, y: Float) {
        current.add(Point(x, y, now()))
        if (strokes.isNotEmpty()) strokes[strokes.lastIndex] = Stroke(current.toList())
    }

    fun endStroke() {
        if (current.isEmpty()) return
        if (strokes.isNotEmpty()) strokes[strokes.lastIndex] = Stroke(current.toList())
        current = mutableListOf()
    }

    /** Immutable snapshot of completed strokes for recognition. */
    fun snapshot(): List<Stroke> = strokes.map { Stroke(it.points.toList()) }

    fun clear() {
        strokes.clear()
        current = mutableListOf()
    }

    private fun now() = System.currentTimeMillis() - baseline
}

@Composable
fun rememberDrawingController(): DrawingController = remember { DrawingController() }

/**
 * The drawing surface: shows a dashed [tracingCharacter] guide the child traces over, and
 * captures pen/touch input into [controller] as timestamped strokes.
 */
@Composable
fun DrawingCanvas(
    controller: DrawingController,
    tracingCharacter: String,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            awaitEachGesture {
                val down = awaitFirstDown()
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
        // 1) Tracing guide (dashed glyph outline) underneath the ink.
        if (tracingCharacter.isNotEmpty()) {
            val glyph = buildGlyphPath(tracingCharacter, size.width, size.height)
            drawPath(
                path = glyph,
                color = Color(0xFFBDBDBD),
                style = DrawStroke(
                    width = 6f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f)),
                ),
            )
        }

        // 2) The child's ink on top.
        controller.strokes.forEach { stroke ->
            val points = stroke.points
            if (points.isEmpty()) return@forEach
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(
                path = path,
                color = INK_COLOR,
                style = DrawStroke(
                    width = INK_WIDTH,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}
