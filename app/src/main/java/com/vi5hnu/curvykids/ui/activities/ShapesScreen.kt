package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.data.content.SHAPES
import com.vi5hnu.curvykids.data.content.Shape
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink

/** Shapes — Learn (tap to hear the name) + Play ("Find the Shape" quiz). */
@Composable
fun ShapesScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = SHAPES,
        quizPromptLabel = "FIND THE SHAPE",
        keyOf = { it.id },
        speakFor = { "Find the ${it.name}" },
        onReward = onReward,
        speaker = speaker,
        celebrateTitle = "Shape Star!",
        learnContent = { ShapesLearnGrid(onReward = onReward, speaker = speaker) },
        quizPrompt = { target ->
            Text(
                text = target.name,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 38.sp,
                color = target.color,
            )
        },
        quizOption = { shape ->
            Canvas(modifier = Modifier.size(66.dp)) { drawShape(shape.id, shape.color) }
        },
    )
}

/** The original explore grid — tap a shape to hear its name and earn a star the first time. */
@Composable
private fun ShapesLearnGrid(
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker?,
) {
    var seen by remember { mutableStateOf(setOf<String>()) }

    val rows = SHAPES.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
        rows.forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                pair.forEach { shape ->
                    ShapeCard(
                        shape = shape,
                        seen = seen.contains(shape.id),
                        onTap = {
                            speaker?.speak(shape.name)
                            if (!seen.contains(shape.id)) {
                                seen = seen + shape.id
                                onReward(2)
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (pair.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ShapeCard(
    shape: Shape,
    seen: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onTap,
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 3.dp,
    ) {
        Box(modifier = Modifier.padding(18.dp, 18.dp, 10.dp, 14.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Canvas(modifier = Modifier.size(88.dp)) {
                    drawShape(shape.id, shape.color)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = shape.name,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = Ink,
                )
            }
            if (seen) {
                Text(
                    text = "⭐",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
        }
    }
}

/** Draws the named shape filling the canvas. Shared by Learn cards and quiz options. */
private fun DrawScope.drawShape(id: String, color: Color) {
    val w = size.width
    val h = size.height
    when (id) {
        "circle" -> drawCircle(color, radius = w * 0.4f, center = Offset(w / 2f, h / 2f))
        "square" -> drawRoundRect(color, topLeft = Offset(w * 0.14f, h * 0.14f), size = Size(w * 0.72f, h * 0.72f), cornerRadius = CornerRadius(w * 0.12f))
        "rectangle" -> drawRoundRect(color, topLeft = Offset(w * 0.08f, h * 0.26f), size = Size(w * 0.84f, h * 0.48f), cornerRadius = CornerRadius(w * 0.1f))
        "oval" -> drawOval(color, topLeft = Offset(w * 0.08f, h * 0.22f), size = Size(w * 0.84f, h * 0.56f))
        "triangle" -> {
            val path = Path().apply {
                moveTo(w * 0.5f, h * 0.12f)
                lineTo(w * 0.88f, h * 0.84f)
                lineTo(w * 0.12f, h * 0.84f)
                close()
            }
            drawPath(path, color)
        }
        "star" -> {
            val path = Path().apply {
                val cx = w / 2f; val cy = h / 2f; val r = w * 0.42f; val r2 = w * 0.18f
                for (i in 0 until 10) {
                    val angle = (i * 36.0 - 90.0) * Math.PI / 180.0
                    val rad = if (i % 2 == 0) r else r2
                    val px = cx + (rad * Math.cos(angle)).toFloat()
                    val py = cy + (rad * Math.sin(angle)).toFloat()
                    if (i == 0) moveTo(px, py) else lineTo(px, py)
                }
                close()
            }
            drawPath(path, color)
        }
        "heart" -> {
            val path = Path().apply {
                val cx = w / 2f; val cy = h * 0.5f
                moveTo(cx, cy + h * 0.36f)
                cubicTo(w * 0.1f, h * 0.5f, w * 0.1f, h * 0.2f, cx, h * 0.3f)
                cubicTo(cx, h * 0.2f, w * 0.9f, h * 0.2f, w * 0.9f, h * 0.5f)
                cubicTo(w * 0.9f, h * 0.6f, cx, cy + h * 0.36f, cx, cy + h * 0.36f)
                close()
            }
            drawPath(path, color)
        }
        "diamond" -> {
            val path = Path().apply {
                moveTo(w / 2f, h * 0.1f)
                lineTo(w * 0.86f, h / 2f)
                lineTo(w / 2f, h * 0.9f)
                lineTo(w * 0.14f, h / 2f)
                close()
            }
            drawPath(path, color)
        }
        "pentagon" -> drawPath(regularPolygon(w, h, 5), color)
        "hexagon" -> drawPath(regularPolygon(w, h, 6), color)
    }
}

/** Builds a centred regular polygon with [sides] points, first vertex pointing up. */
private fun regularPolygon(w: Float, h: Float, sides: Int): Path = Path().apply {
    val cx = w / 2f; val cy = h / 2f; val r = w * 0.42f
    for (i in 0 until sides) {
        val angle = (i * 360.0 / sides - 90.0) * Math.PI / 180.0
        val px = cx + (r * Math.cos(angle)).toFloat()
        val py = cy + (r * Math.sin(angle)).toFloat()
        if (i == 0) moveTo(px, py) else lineTo(px, py)
    }
    close()
}
