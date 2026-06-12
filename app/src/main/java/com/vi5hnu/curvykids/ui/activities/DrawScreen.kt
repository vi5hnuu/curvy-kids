package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStyle
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.TintTeal

// ── Canvas marks ────────────────────────────────────────────────────────────────

/** Anything painted on the canvas, kept in draw order so Undo can pop the most recent. */
private sealed interface Mark
private data class StrokeMark(val points: List<Offset>, val color: Color, val width: Float) : Mark
private data class StampMark(val emoji: String, val x: Float, val y: Float, val size: Float) : Mark

// ── Palette / brushes / content ─────────────────────────────────────────────────

private val PALETTE = listOf(
    Color(0xFFFF5A52), Color(0xFFFF9F1C), Color(0xFFFFC24A), Color(0xFF4FCB94),
    Color(0xFF1FC2AE), Color(0xFF46A6F0), Color(0xFFA855F7), Color(0xFFFF8FB6),
    Color(0xFF2B3A4A), Color(0xFF8B5E3C),
)

private val BRUSH_SIZES = listOf(10.dp, 16.dp, 26.dp)

private data class Template(val id: String, val name: String, val emoji: String)

/** "Blank" plus colour-in outline pictures. */
private val TEMPLATES = listOf(
    Template("blank", "Blank", "✏️"),
    Template("sun", "Sun", "☀️"),
    Template("house", "House", "🏠"),
    Template("fish", "Fish", "🐟"),
    Template("flower", "Flower", "🌸"),
    Template("car", "Car", "🚗"),
    Template("star", "Star", "⭐"),
    Template("heart", "Heart", "❤️"),
)

private val STAMPS = listOf("⭐", "❤️", "🌸", "🦋", "🌈", "☁️", "🐝", "🍎", "😊", "🌙")

private val GUIDE_COLOR = Color(0xFFCBD9E3)

/**
 * Free Draw — a real creative play space for kids:
 *  - **Colour-in templates**: pick a picture (sun, house, fish…) to trace and fill.
 *  - **Stamps**: tap to place fun stickers.
 *  - **Draw**: crayon strokes with a 10-colour palette and 3 brush sizes.
 *  - **Undo / Clear** so mistakes are never scary.
 */
@Composable
fun DrawScreen(
    topic: Topic,
    onBack: () -> Unit,
) {
    var selectedColor by remember { mutableStateOf(PALETTE[0]) }
    var selectedSize by remember { mutableStateOf(BRUSH_SIZES[1]) }
    var templateId by remember { mutableStateOf("blank") }
    var stampMode by remember { mutableStateOf(false) }
    var selectedStamp by remember { mutableStateOf(STAMPS[0]) }

    val marks = remember { mutableStateListOf<Mark>() }
    var currentPoints by remember { mutableStateOf(listOf<Offset>()) }

    // Native paint reused across frames for stamp glyph rendering.
    val stampPaint = remember {
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    val template = TEMPLATES.first { it.id == templateId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp),
    ) {
        ScreenHeader(
            title = topic.title,
            color = topic.color,
            onBack = onBack,
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Chip(
                        onClick = { marks.removeLastOrNull() },
                        modifier = Modifier.size(46.dp),
                        containerColor = Color(0xFFEAF1F6),
                        contentColor = InkSoft,
                    ) { Text("↶", fontSize = 22.sp) }
                    Chip(
                        onClick = { marks.clear() },
                        modifier = Modifier.size(46.dp),
                        containerColor = Color(0xFFFFE9E4),
                        contentColor = Color(0xFFFF8B6B),
                    ) { Text("🧹", fontSize = 18.sp) }
                }
            },
        )

        Spacer(Modifier.height(10.dp))

        // ── Template picker ──────────────────────────────────────────────
        SectionLabel(if (templateId == "blank") "Pick a picture to colour" else "Colour the ${template.name}!")
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TEMPLATES.forEach { t ->
                val selected = t.id == templateId
                Surface(
                    onClick = { templateId = t.id },
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) topic.tint else Color.White,
                    border = if (selected) BorderStroke(2.dp, topic.color) else null,
                    shadowElevation = 2.dp,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Text(t.emoji, fontSize = 18.sp)
                        Text(
                            t.name,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = if (selected) topic.color else InkSoft,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Canvas ───────────────────────────────────────────────────────
        CardSurface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(stampMode, selectedStamp, selectedColor, selectedSize) {
                        if (stampMode) {
                            detectTapGestures { offset ->
                                marks.add(StampMark(selectedStamp, offset.x, offset.y, 96f))
                            }
                        } else {
                            val widthPx = selectedSize.toPx()
                            detectDragGestures(
                                onDragStart = { offset -> currentPoints = listOf(offset) },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints = currentPoints + change.position
                                },
                                onDragEnd = {
                                    if (currentPoints.isNotEmpty()) marks.add(StrokeMark(currentPoints, selectedColor, widthPx))
                                    currentPoints = emptyList()
                                },
                                onDragCancel = {
                                    if (currentPoints.isNotEmpty()) marks.add(StrokeMark(currentPoints, selectedColor, widthPx))
                                    currentPoints = emptyList()
                                },
                            )
                        }
                    },
            ) {
                // Outline guide for the colour-in template (under everything).
                if (templateId != "blank") drawTemplate(templateId, GUIDE_COLOR)

                // Painted marks in order.
                marks.forEach { mark ->
                    when (mark) {
                        is StrokeMark -> drawSmoothStroke(mark.points, mark.color, mark.width)
                        is StampMark -> {
                            stampPaint.textSize = mark.size
                            drawContext.canvas.nativeCanvas.drawText(
                                mark.emoji, mark.x, mark.y + mark.size * 0.35f, stampPaint,
                            )
                        }
                    }
                }

                // Live stroke being drawn.
                if (currentPoints.size >= 2) drawSmoothStroke(currentPoints, selectedColor, selectedSize.toPx())
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Tool row: pencil + stamps ────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Pencil = draw mode
            ToolDot(selected = !stampMode, onClick = { stampMode = false }) {
                Text("✏️", fontSize = 20.sp)
            }
            Spacer(Modifier.width(2.dp))
            STAMPS.forEach { s ->
                ToolDot(
                    selected = stampMode && selectedStamp == s,
                    onClick = { stampMode = true; selectedStamp = s },
                ) { Text(s, fontSize = 20.sp) }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ── Brush sizes + palette ────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            BRUSH_SIZES.forEach { sz ->
                Chip(
                    onClick = { selectedSize = sz; stampMode = false },
                    modifier = Modifier.size(42.dp),
                    containerColor = if (selectedSize == sz && !stampMode) TintTeal else Color.White,
                ) {
                    Surface(shape = CircleShape, color = selectedColor, modifier = Modifier.size(sz.coerceAtMost(30.dp))) {}
                }
            }
            Spacer(Modifier.width(4.dp))
            PALETTE.forEach { c ->
                Surface(
                    onClick = { selectedColor = c; stampMode = false },
                    shape = CircleShape,
                    color = c,
                    modifier = Modifier.size(38.dp),
                    shadowElevation = if (c == selectedColor) 6.dp else 2.dp,
                    border = if (c == selectedColor) BorderStroke(3.dp, Color.White) else null,
                ) {}
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontFamily = FontDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        color = InkSoft,
        modifier = Modifier.padding(horizontal = 2.dp),
    )
}

@Composable
private fun ToolDot(selected: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) TintTeal else Color.White,
        border = if (selected) BorderStroke(2.dp, Color(0xFF1FC2AE)) else null,
        shadowElevation = 2.dp,
        modifier = Modifier.size(44.dp),
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

/** Draws a poly-line stroke with rounded joins. */
private fun DrawScope.drawSmoothStroke(points: List<Offset>, color: Color, width: Float) {
    if (points.size < 2) {
        if (points.size == 1) drawCircle(color, width / 2f, points[0])
        return
    }
    val path = Path().apply {
        moveTo(points[0].x, points[0].y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(path, color = color, style = DrawStyle(width = width, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/** Draws the named colour-in template as a light outline centred in the canvas. */
private fun DrawScope.drawTemplate(id: String, color: Color) {
    val w = size.width
    val h = size.height
    val st = DrawStyle(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    when (id) {
        "sun" -> {
            val cx = w / 2f; val cy = h / 2f; val r = minOf(w, h) * 0.18f
            drawCircle(color, r, Offset(cx, cy), style = st)
            for (i in 0 until 8) {
                val a = (i * 45.0) * Math.PI / 180.0
                val sx = cx + (r * 1.35f * Math.cos(a)).toFloat()
                val sy = cy + (r * 1.35f * Math.sin(a)).toFloat()
                val ex = cx + (r * 1.95f * Math.cos(a)).toFloat()
                val ey = cy + (r * 1.95f * Math.sin(a)).toFloat()
                drawLine(color, Offset(sx, sy), Offset(ex, ey), strokeWidth = 6.dp.toPx(), cap = StrokeCap.Round)
            }
        }
        "house" -> {
            val bx = w * 0.28f; val by = h * 0.46f; val bw = w * 0.44f; val bh = h * 0.34f
            drawRect(color, topLeft = Offset(bx, by), size = Size(bw, bh), style = st)
            // roof
            val roof = Path().apply {
                moveTo(bx - w * 0.04f, by)
                lineTo(w / 2f, by - h * 0.18f)
                lineTo(bx + bw + w * 0.04f, by)
            }
            drawPath(roof, color, style = st)
            // door
            drawRect(color, topLeft = Offset(w / 2f - w * 0.07f, by + bh * 0.4f), size = Size(w * 0.14f, bh * 0.6f), style = st)
        }
        "fish" -> {
            val cx = w / 2f; val cy = h / 2f
            drawOval(color, topLeft = Offset(cx - w * 0.26f, cy - h * 0.16f), size = Size(w * 0.46f, h * 0.32f), style = st)
            // tail
            val tail = Path().apply {
                moveTo(cx + w * 0.2f, cy)
                lineTo(cx + w * 0.34f, cy - h * 0.12f)
                lineTo(cx + w * 0.34f, cy + h * 0.12f)
                close()
            }
            drawPath(tail, color, style = st)
            // eye
            drawCircle(color, w * 0.02f, Offset(cx - w * 0.14f, cy - h * 0.03f), style = st)
        }
        "flower" -> {
            val cx = w / 2f; val cy = h / 2f; val pr = minOf(w, h) * 0.12f
            for (i in 0 until 6) {
                val a = (i * 60.0) * Math.PI / 180.0
                val px = cx + (pr * 1.6f * Math.cos(a)).toFloat()
                val py = cy + (pr * 1.6f * Math.sin(a)).toFloat()
                drawCircle(color, pr, Offset(px, py), style = st)
            }
            drawCircle(color, pr * 0.9f, Offset(cx, cy), style = st)
        }
        "car" -> {
            val bx = w * 0.16f; val by = h * 0.42f; val bw = w * 0.68f; val bh = h * 0.2f
            drawRect(color, topLeft = Offset(bx, by), size = Size(bw, bh), style = st)
            // cabin
            val cabin = Path().apply {
                moveTo(bx + bw * 0.2f, by)
                lineTo(bx + bw * 0.32f, by - bh * 0.7f)
                lineTo(bx + bw * 0.68f, by - bh * 0.7f)
                lineTo(bx + bw * 0.8f, by)
            }
            drawPath(cabin, color, style = st)
            drawCircle(color, bh * 0.45f, Offset(bx + bw * 0.26f, by + bh), style = st)
            drawCircle(color, bh * 0.45f, Offset(bx + bw * 0.74f, by + bh), style = st)
        }
        "star" -> {
            val cx = w / 2f; val cy = h / 2f; val r = minOf(w, h) * 0.26f; val r2 = r * 0.42f
            val path = Path().apply {
                for (i in 0 until 10) {
                    val a = (i * 36.0 - 90.0) * Math.PI / 180.0
                    val rad = if (i % 2 == 0) r else r2
                    val px = cx + (rad * Math.cos(a)).toFloat()
                    val py = cy + (rad * Math.sin(a)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(path, color, style = st)
        }
        "heart" -> {
            val cx = w / 2f; val cy = h * 0.5f; val s = minOf(w, h) * 0.5f
            val path = Path().apply {
                moveTo(cx, cy + s * 0.35f)
                cubicTo(cx - s * 0.6f, cy, cx - s * 0.5f, cy - s * 0.4f, cx, cy - s * 0.12f)
                cubicTo(cx + s * 0.5f, cy - s * 0.4f, cx + s * 0.6f, cy, cx, cy + s * 0.35f)
                close()
            }
            drawPath(path, color, style = st)
        }
    }
}
