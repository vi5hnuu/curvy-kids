package com.vi5hnu.curvykids.ui.activities

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.data.export.ArtworkStore
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.TintTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.data.content.AssetPath

// ── Canvas marks ────────────────────────────────────────────────────────────────

/** Anything painted on the canvas, kept in draw order so Undo can pop the most recent. */
private sealed interface Mark
private data class StrokeMark(val points: List<Offset>, val color: Color, val width: Float) : Mark
private data class StampMark(
    val shape: StampShape,
    val x: Float,
    val y: Float,
    val size: Float,
    val color: Color,
) : Mark

// ── Palette / brushes / content ─────────────────────────────────────────────────

private val PALETTE = listOf(
    Color(0xFFFF5A52), Color(0xFFFF9F1C), Color(0xFFFFC24A), Color(0xFF4FCB94),
    Color(0xFF1FC2AE), Color(0xFF46A6F0), Color(0xFFA855F7), Color(0xFFFF8FB6),
    Color(0xFF2B3A4A), Color(0xFF8B5E3C),
)

private val BRUSH_SIZES = listOf(10.dp, 16.dp, 26.dp)

/** @param svg Picture for the picker chip; null for "blank", which shows a pencil instead. */
private data class Template(val id: String, val name: String, val svg: AssetPath?)

/** "Blank" plus colour-in outline pictures. */
private val TEMPLATES = listOf(
    Template("blank", "Blank", null),
    Template("sun", "Sun", KidAssets.weather("sun")),
    Template("house", "House", KidAssets.place("house")),
    Template("fish", "Fish", KidAssets.seaAnimal("fish")),
    Template("flower", "Flower", KidAssets.nature("tulip")),
    Template("car", "Car", KidAssets.transport("car")),
    Template("star", "Star", KidAssets.weather("star")),
    Template("heart", "Heart", KidAssets.body("heart")),
)

/**
 * Shapes the child can stamp onto the canvas.
 *
 * Drawn as vectors rather than pasted pictures so each stamp takes the currently selected
 * crayon colour — the same star can be red or blue — and so nothing depends on the device's
 * emoji font.
 */
private enum class StampShape { Star, Heart, Flower, Circle, Square, Triangle, Moon, Cloud }

private val STAMPS = StampShape.entries

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
    onAddBadge: (String) -> Unit = {},
) {
    var selectedColor by remember { mutableStateOf(PALETTE[0]) }
    var selectedSize by remember { mutableStateOf(BRUSH_SIZES[1]) }
    var templateId by remember { mutableStateOf("blank") }
    var stampMode by remember { mutableStateOf(false) }
    var selectedStamp by remember { mutableStateOf(STAMPS.first()) }

    val marks = remember { mutableStateListOf<Mark>() }

    // Unlocks the "Lil Artist" sticker the first time the child actually draws something.
    // The sticker used to be hardcoded as always-earned, so it rewarded nothing.
    LaunchedEffect(marks.isNotEmpty()) {
        if (marks.isNotEmpty()) onAddBadge("artist")
    }
    var currentPoints by remember { mutableStateOf(listOf<Offset>()) }

    val template = TEMPLATES.first { it.id == templateId }

    // Capture layer for "keep this drawing": records exactly what the canvas paints.
    val artLayer = rememberGraphicsLayer()
    val context = LocalContext.current
    val store = remember(context) { ArtworkStore(context) }
    val scope = rememberCoroutineScope()
    var savedUri by remember { mutableStateOf<Uri?>(null) }
    var saveMessage by remember { mutableStateOf<String?>(null) }

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
                        modifier = Modifier.size(48.dp),
                        containerColor = Color(0xFFEAF1F6),
                        contentColor = InkSoft,
                    ) { KidIconImage(KidIcon.Undo, Modifier.size(22.dp)) }
                    Chip(
                        onClick = { marks.clear() },
                        modifier = Modifier.size(48.dp),
                        containerColor = Color(0xFFFFE9E4),
                        contentColor = Color(0xFFFF8B6B),
                    ) { KidIconImage(KidIcon.Eraser, Modifier.size(18.dp)) }
                    Chip(
                        onClick = {
                            if (marks.isEmpty()) {
                                savedUri = null
                                saveMessage = "Draw something first!"
                            } else {
                                scope.launch {
                                    val uri = store.save(artLayer.toImageBitmap().asAndroidBitmap())
                                    savedUri = uri
                                    saveMessage = if (uri != null) "Saved to your photos!"
                                                  else "Could not save right now."
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        containerColor = TintTeal,
                        contentColor = Color(0xFF1FC2AE),
                    ) { KidIconImage(KidIcon.Check, Modifier.size(20.dp)) }
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
                        if (t.svg != null) {
                            SvgArt(t.svg, modifier = Modifier.size(18.dp))
                        } else {
                            KidIconImage(KidIcon.Pencil, Modifier.size(16.dp), InkSoft)
                        }
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
                    .drawWithContent {
                        // Record into the layer, then draw the layer — so what is saved is
                        // pixel-for-pixel what the child sees.
                        artLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(artLayer)
                    }
                    .pointerInput(stampMode, selectedStamp, selectedColor, selectedSize) {
                        if (stampMode) {
                            detectTapGestures { offset ->
                                marks.add(StampMark(selectedStamp, offset.x, offset.y, 96f, selectedColor))
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
                // Opaque ground. On screen the card behind supplies the white, but the saved
                // PNG only contains what this canvas paints — without this the exported image
                // is transparent, which most galleries show as black.
                drawRect(Color.White)

                // Outline guide for the colour-in template (under everything).
                if (templateId != "blank") drawTemplate(templateId, GUIDE_COLOR)

                // Painted marks in order.
                marks.forEach { mark ->
                    when (mark) {
                        is StrokeMark -> drawSmoothStroke(mark.points, mark.color, mark.width)
                        is StampMark -> drawStamp(mark.shape, mark.x, mark.y, mark.size, mark.color)
                    }
                }

                // Live stroke being drawn.
                if (currentPoints.size >= 2) drawSmoothStroke(currentPoints, selectedColor, selectedSize.toPx())
            }
        }

        // ── Save confirmation ─────────────────────────────────────────────
        saveMessage?.let { message ->
            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (savedUri != null) TintTeal else Color(0xFFFFE9E4),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = message,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = if (savedUri != null) Color(0xFF158D80) else Color(0xFFCF5B3E),
                        modifier = Modifier.weight(1f),
                    )
                    savedUri?.let { uri ->
                        Surface(
                            onClick = { store.share(uri) },
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White,
                            modifier = Modifier.height(36.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = "Share",
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1FC2AE),
                                )
                            }
                        }
                    }
                }
            }
            // Clear the banner on its own so it does not sit there for the rest of the session.
            LaunchedEffect(message) {
                delay(6000)
                saveMessage = null
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
                KidIconImage(KidIcon.Pencil, Modifier.size(20.dp), selectedColor)
            }
            Spacer(Modifier.width(2.dp))
            STAMPS.forEach { s ->
                ToolDot(
                    selected = stampMode && selectedStamp == s,
                    onClick = { stampMode = true; selectedStamp = s },
                ) {
                    Canvas(Modifier.size(22.dp)) { drawStamp(s, size.width / 2f, size.height / 2f, size.minDimension, selectedColor) }
                }
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
                    modifier = Modifier.size(48.dp),
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
        // 48dp is the accessible minimum; these are the drawing tools a small hand taps most.
        modifier = Modifier.size(48.dp),
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

/**
 * Paints one stamp centred on [cx],[cy] within a [size]-wide box, in [color].
 * Coordinates are normalised against the box so every shape scales together.
 */
private fun DrawScope.drawStamp(shape: StampShape, cx: Float, cy: Float, size: Float, color: Color) {
    val r = size / 2f
    fun pt(fx: Float, fy: Float) = Offset(cx + fx * r, cy + fy * r)

    when (shape) {
        StampShape.Circle -> drawCircle(color, r * 0.8f, Offset(cx, cy))

        StampShape.Square -> drawRoundRect(
            color,
            topLeft = Offset(cx - r * 0.72f, cy - r * 0.72f),
            size = Size(r * 1.44f, r * 1.44f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.16f),
        )

        StampShape.Triangle -> drawPath(
            Path().apply {
                moveTo(cx, cy - r * 0.82f)
                lineTo(cx + r * 0.78f, cy + r * 0.62f)
                lineTo(cx - r * 0.78f, cy + r * 0.62f)
                close()
            },
            color,
        )

        StampShape.Star -> drawPath(
            Path().apply {
                val outer = r * 0.9f
                val inner = r * 0.38f
                for (i in 0 until 10) {
                    val rad = if (i % 2 == 0) outer else inner
                    val a = -Math.PI / 2 + i * Math.PI / 5
                    val x = cx + (rad * kotlin.math.cos(a)).toFloat()
                    val y = cy + (rad * kotlin.math.sin(a)).toFloat()
                    if (i == 0) moveTo(x, y) else lineTo(x, y)
                }
                close()
            },
            color,
        )

        StampShape.Heart -> drawPath(
            Path().apply {
                moveTo(cx, cy + r * 0.8f)
                cubicTo(cx - r * 1.3f, cy - r * 0.1f, cx - r * 0.55f, cy - r * 0.95f, cx, cy - r * 0.32f)
                cubicTo(cx + r * 0.55f, cy - r * 0.95f, cx + r * 1.3f, cy - r * 0.1f, cx, cy + r * 0.8f)
                close()
            },
            color,
        )

        StampShape.Flower -> {
            repeat(6) { i ->
                val a = i * Math.PI / 3
                drawCircle(
                    color,
                    r * 0.34f,
                    Offset(cx + (r * 0.5f * kotlin.math.cos(a)).toFloat(),
                           cy + (r * 0.5f * kotlin.math.sin(a)).toFloat()),
                )
            }
            drawCircle(color.copy(alpha = 0.55f), r * 0.28f, Offset(cx, cy))
        }

        StampShape.Moon -> {
            // Crescent: a disc with a second disc lifted out of it.
            drawCircle(color, r * 0.82f, Offset(cx, cy))
            drawCircle(color.copy(alpha = 0f), r * 0.7f, Offset(cx + r * 0.42f, cy - r * 0.18f),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear)
        }

        StampShape.Cloud -> {
            drawCircle(color, r * 0.42f, pt(-0.5f, 0.12f))
            drawCircle(color, r * 0.54f, pt(0f, -0.08f))
            drawCircle(color, r * 0.4f, pt(0.5f, 0.14f))
            drawRoundRect(
                color,
                topLeft = Offset(cx - r * 0.86f, cy + r * 0.02f),
                size = Size(r * 1.72f, r * 0.52f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.26f),
            )
        }
    }
}
