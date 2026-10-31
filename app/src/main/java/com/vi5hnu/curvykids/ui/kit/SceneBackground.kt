package com.vi5hnu.curvykids.ui.kit

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.vi5hnu.curvykids.ui.theme.SceneHillBack
import com.vi5hnu.curvykids.ui.theme.SceneHillFront
import com.vi5hnu.curvykids.ui.theme.SceneSkyBottom
import com.vi5hnu.curvykids.ui.theme.SceneSkyTop

/**
 * The outdoor scene behind every child-facing screen: sky, a sun, drifting clouds and rolling
 * hills. Drawn in code, so it costs no assets and scales to any screen.
 *
 * The clouds move very slowly (a minute per crossing); the system "remove animations" setting
 * stops them because infinite transitions follow the animator duration scale.
 *
 * @param tint Mixed lightly into the hills, so each world's screens feel like their own place.
 */
@Composable
fun SceneBackground(
    modifier: Modifier = Modifier,
    tint: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val drift by rememberInfiniteTransition(label = "clouds").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(60_000, easing = LinearEasing), RepeatMode.Restart),
        label = "cloudDrift",
    )
    Box(modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawRect(Brush.verticalGradient(listOf(SceneSkyTop, SceneSkyBottom)))
            drawSun(Offset(size.width * 0.6f, size.height * 0.035f), size.minDimension * 0.06f)
            drawCloud(Offset(wrap(0.12f + drift) * size.width, size.height * 0.10f), size.width * 0.2f)
            drawCloud(Offset(wrap(0.55f + drift) * size.width, size.height * 0.05f), size.width * 0.14f)
            drawCloud(Offset(wrap(0.80f + drift * 1.4f) * size.width, size.height * 0.17f), size.width * 0.17f)
            drawHill(0.80f, 0.06f, mix(SceneHillBack, tint))
            drawHill(0.88f, 0.05f, mix(SceneHillFront, tint))
        }
        content()
    }
}

/** Positions a cloud on a loop that runs a little past both edges, so it slides fully out and in. */
private fun wrap(x: Float) = (x % 1f) * 1.3f - 0.15f

private fun mix(base: Color, tint: Color?): Color = if (tint == null) base else Color(
    red = base.red * 0.75f + tint.red * 0.25f,
    green = base.green * 0.75f + tint.green * 0.25f,
    blue = base.blue * 0.75f + tint.blue * 0.25f,
)

private fun DrawScope.drawSun(center: Offset, r: Float) {
    drawCircle(Color(0x33FFE27A), r * 1.6f, center)
    drawCircle(Color(0xFFFFD54A), r, center)
}

private fun DrawScope.drawCloud(center: Offset, w: Float) {
    val c = Color.White.copy(alpha = 0.92f)
    drawRoundRect(c, Offset(center.x - w / 2, center.y), Size(w, w * 0.3f),
        androidx.compose.ui.geometry.CornerRadius(w * 0.15f))
    drawCircle(c, w * 0.2f, Offset(center.x - w * 0.15f, center.y + w * 0.02f))
    drawCircle(c, w * 0.26f, Offset(center.x + w * 0.1f, center.y - w * 0.03f))
}

/** A soft hill along the bottom; [top] is where its crest sits (fraction of height). */
private fun DrawScope.drawHill(top: Float, wave: Float, color: Color) {
    val y = size.height * top
    val a = size.height * wave
    val path = Path().apply {
        moveTo(0f, y)
        cubicTo(size.width * 0.25f, y - a, size.width * 0.5f, y + a, size.width * 0.75f, y - a * 0.5f)
        cubicTo(size.width * 0.88f, y - a, size.width, y, size.width, y)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path, color)
}
