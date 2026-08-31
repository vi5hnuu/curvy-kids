package com.vi5hnu.curvykids.ui.trace.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/** Faint grey the untraced character sits in — matches the Latin stroke guide. */
private val SKELETON_COLOR = Color(0xFFE4EDF4)

private const val BODY_MS = 1500
private const val STEM_MS = 600
private const val HEADLINE_MS = 700
private const val PEN_LIFT_MS = 300L
private const val HOLD_MS = 900L

/** Size of the analysis raster; the parts it finds are scaled to the drawn glyph. */
private const val ANALYSIS_BOX = 128

/**
 * Shows a child how a Devanagari letter is written, one part at a time.
 *
 * Devanagari is taught in a consistent order — the body of the letter, then the khari pai (the
 * full-height vertical stem), and the shirorekha (the headline along the top) last. Those three
 * parts are structural, so rather than hand-authoring stroke data for 49 letters and 432
 * barakhadi forms, [analyseDevanagari] locates them in the glyph itself. Measuring the real
 * glyph also guarantees the demonstration lines up exactly with the letter the child traces.
 *
 * Each part is revealed in the direction a pen actually travels for it — the body and headline
 * left to right, the stem top to bottom — with the pen visible so it reads as writing rather
 * than as colour being poured in.
 *
 * This is not the same as true per-stroke data: within the body the reveal is still a sweep, so
 * a letter whose body takes several strokes is shown as one. It is accurate about the part
 * order, which is the thing learners most often get wrong.
 *
 * @param char    Character to demonstrate.
 * @param color   The child's current crayon colour.
 * @param demoKey Change to replay from the beginning.
 */
@Composable
fun DemoWipe(
    char: String,
    color: Color,
    demoKey: Int,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val glyphPx = with(density) { 190.dp.toPx() }

    // One paint for both measuring and drawing, so the parts found line up with the ink shown.
    val paint = remember(glyphPx) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = glyphPx
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
        }
    }
    val parts = remember(char, glyphPx) { analyseDevanagari(char, paint, ANALYSIS_BOX) }

    val body = remember { Animatable(0f) }
    val stem = remember { Animatable(0f) }
    val headline = remember { Animatable(0f) }

    LaunchedEffect(demoKey, parts) {
        body.snapTo(0f); stem.snapTo(0f); headline.snapTo(0f)
        delay(250)
        body.animateTo(1f, tween(BODY_MS, easing = LinearEasing))
        if (parts?.stem != null) {
            delay(PEN_LIFT_MS)
            stem.animateTo(1f, tween(STEM_MS, easing = LinearEasing))
        } else {
            stem.snapTo(1f)
        }
        delay(PEN_LIFT_MS)
        headline.animateTo(1f, tween(HEADLINE_MS, easing = LinearEasing))
        delay(HOLD_MS)
    }

    Canvas(modifier = modifier) {
        val bounds = android.graphics.Rect()
        paint.getTextBounds(char, 0, char.length, bounds)
        if (bounds.isEmpty) return@Canvas

        val originX = (size.width - bounds.width()) / 2f - bounds.left
        val originY = (size.height - bounds.height()) / 2f - bounds.top

        fun glyph(argb: Int) = drawIntoCanvas {
            paint.color = argb
            it.nativeCanvas.drawText(char, originX, originY, paint)
        }

        // The whole letter, faint, as the trace target.
        glyph(SKELETON_COLOR.toArgb())

        val p = parts ?: run {
            // No measurable ink: fall back to a plain sweep rather than showing nothing.
            clipRect(right = size.width * body.value) { glyph(color.copy(alpha = 0.55f).toArgb()) }
            return@Canvas
        }

        // Analysis raster coordinates -> canvas coordinates.
        val scale = bounds.width() / p.width
        val left = originX + bounds.left
        val top = originY + bounds.top
        fun mapX(v: Float) = left + (v - p.inkLeft) * scale
        fun mapY(v: Float) = top + (v - p.inkTop) * scale

        val headlineTop = mapY(p.headlineTop)
        val headlineBottom = mapY(p.headlineBottom)
        val inkBottom = mapY(p.inkBottom)
        val stemLeft = p.stem?.let { mapX(it.start) } ?: size.width
        val stemRight = p.stem?.let { mapX(it.endInclusive) } ?: size.width
        val ink = color.copy(alpha = 0.55f).toArgb()

        // 1. Body — everything under the headline and left of the stem, written left to right.
        if (body.value > 0f) {
            val edge = mapX(p.inkLeft) + (stemLeft - mapX(p.inkLeft)) * body.value
            clipRect(left = 0f, top = headlineBottom, right = edge, bottom = size.height) {
                glyph(ink)
            }
        }
        // 2. The stem, drawn downward.
        if (stem.value > 0f && p.stem != null) {
            clipRect(
                left = stemLeft,
                top = headlineBottom,
                right = size.width,
                bottom = headlineBottom + (inkBottom - headlineBottom) * stem.value,
            ) { glyph(ink) }
        }
        // 3. The shirorekha last, sweeping across the letter (not the whole canvas, or the
        // sweep spends its first moments in empty space beside the glyph).
        if (headline.value > 0f) {
            val inkLeftPx = mapX(p.inkLeft)
            val inkRightPx = mapX(p.inkRight)
            clipRect(
                left = 0f,
                top = 0f,
                right = inkLeftPx + (inkRightPx - inkLeftPx) * headline.value,
                bottom = headlineBottom,
            ) { glyph(ink) }
        }

        drawPen(p, body.value, stem.value, headline.value,
            headlineTop, headlineBottom, inkBottom, stemLeft, stemRight,
            mapX(p.inkLeft), mapX(p.inkRight), color, density.density)
    }
}

/** The moving pen, placed on whichever part is currently being written. */
private fun DrawScope.drawPen(
    p: DevanagariParts,
    body: Float,
    stem: Float,
    headline: Float,
    headlineTop: Float,
    headlineBottom: Float,
    inkBottom: Float,
    stemLeft: Float,
    stemRight: Float,
    inkLeft: Float,
    inkRight: Float,
    color: Color,
    density: Float,
) {
    val centre: Offset = when {
        body > 0f && body < 1f ->
            Offset(inkLeft + (stemLeft - inkLeft) * body, (headlineBottom + inkBottom) / 2f)
        p.stem != null && stem > 0f && stem < 1f ->
            Offset((stemLeft + stemRight) / 2f, headlineBottom + (inkBottom - headlineBottom) * stem)
        headline > 0f && headline < 1f ->
            Offset(inkLeft + (inkRight - inkLeft) * headline, (headlineTop + headlineBottom) / 2f)
        else -> null
    } ?: return

    drawCircle(Color.White, radius = 15f * density, center = centre)
    drawCircle(color, radius = 11f * density, center = centre)
}
