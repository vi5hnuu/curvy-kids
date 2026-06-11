package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.TintTeal

/** One recorded pen stroke — named PenStroke to avoid shadowing the draw-style Stroke class. */
private data class PenStroke(val points: List<Pair<Float, Float>>, val color: Color, val width: Float)

private val PALETTE = listOf(
    Color(0xFFFF5A52), Color(0xFFFF9F1C), Color(0xFFFFC24A), Color(0xFF4FCB94),
    Color(0xFF1FC2AE), Color(0xFF46A6F0), Color(0xFFA855F7), Color(0xFFFF8FB6),
    Color(0xFF2B3A4A), Color(0xFF8B5E3C),
)

private val BRUSH_SIZES = listOf(10.dp, 16.dp, 26.dp)

/** Free Draw screen — full canvas with 10-color palette and 3 brush sizes. */
@Composable
fun DrawScreen(
    topic: Topic,
    onBack: () -> Unit,
) {
    var selectedColor by remember { mutableStateOf(PALETTE[0]) }
    var selectedSize by remember { mutableStateOf(BRUSH_SIZES[1]) }
    val strokes = remember { mutableStateListOf<PenStroke>() }
    var currentPoints by remember { mutableStateOf(listOf<Pair<Float, Float>>()) }
    var isDrawing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 24.dp),
    ) {
        ScreenHeader(
            title = topic.title,
            color = topic.color,
            onBack = onBack,
            trailing = {
                Chip(
                    onClick = { strokes.clear() },
                    modifier = Modifier.size(46.dp),
                    containerColor = Color(0xFFFFE9E4),
                    contentColor = Color(0xFFFF8B6B),
                ) {
                    Text("🧹", fontSize = 20.sp)
                }
            },
        )
        Spacer(Modifier.height(12.dp))

        // Drawing canvas
        CardSurface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDrawing = true
                                currentPoints = listOf(Pair(offset.x, offset.y))
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPoints = currentPoints + Pair(change.position.x, change.position.y)
                            },
                            onDragEnd = {
                                if (currentPoints.isNotEmpty()) {
                                    strokes.add(PenStroke(currentPoints, selectedColor, selectedSize.toPx()))
                                }
                                currentPoints = emptyList()
                                isDrawing = false
                            },
                            onDragCancel = {
                                if (currentPoints.isNotEmpty()) {
                                    strokes.add(PenStroke(currentPoints, selectedColor, selectedSize.toPx()))
                                }
                                currentPoints = emptyList()
                                isDrawing = false
                            },
                        )
                    },
            ) {
                // Draw committed strokes
                strokes.forEach { stroke ->
                    if (stroke.points.size < 2) return@forEach
                    val path = Path().apply {
                        moveTo(stroke.points[0].first, stroke.points[0].second)
                        stroke.points.drop(1).forEach { (x, y) -> lineTo(x, y) }
                    }
                    drawPath(
                        path,
                        color = stroke.color,
                        style = DrawStyle(
                            width = stroke.width,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
                // Live current stroke
                if (currentPoints.size >= 2) {
                    val path = Path().apply {
                        moveTo(currentPoints[0].first, currentPoints[0].second)
                        currentPoints.drop(1).forEach { (x, y) -> lineTo(x, y) }
                    }
                    drawPath(
                        path,
                        color = selectedColor,
                        style = DrawStyle(
                            width = selectedSize.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Brush size row
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            BRUSH_SIZES.forEach { sz ->
                Chip(
                    onClick = { selectedSize = sz },
                    modifier = Modifier.size(46.dp),
                    containerColor = if (selectedSize == sz) TintTeal else Color.White,
                ) {
                    Surface(
                        shape = CircleShape,
                        color = selectedColor,
                        modifier = Modifier.size(sz.coerceAtMost(36.dp)),
                    ) {}
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Color palette
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            PALETTE.forEach { c ->
                Surface(
                    onClick = { selectedColor = c },
                    shape = CircleShape,
                    color = c,
                    modifier = Modifier.size(38.dp),
                    shadowElevation = if (c == selectedColor) 6.dp else 2.dp,
                    border = if (c == selectedColor)
                        androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                    else null,
                ) {}
            }
        }
    }
}
