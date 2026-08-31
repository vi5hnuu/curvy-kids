package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import kotlinx.coroutines.delay

/**
 * Where the shirorekha ends, measured up from the text baseline as a fraction of the font size.
 *
 * Taken from the baseline rather than from the line box: the line box carries ascent and descent
 * padding well beyond the ink, so a percentage of it does not locate the headline at all — the
 * first attempt at this put the split above the glyph entirely and the headline was revealed
 * along with the body.
 */
private const val HEADLINE_BOTTOM_ABOVE_BASELINE = 0.56f

private const val BODY_MS = 1500
private const val HEADLINE_MS = 650
private const val PEN_LIFT_MS = 320L
private const val HOLD_MS = 900L

/** Faint grey the untraced character sits in — matches the Latin stroke guide. */
private val SKELETON_COLOR = Color(0xFFE4EDF4)

/**
 * Shows how a Devanagari character is written.
 *
 * Devanagari has no per-letter stroke data in this app yet — see StrokeGuide, where guessing at
 * it was deliberately avoided because teaching the wrong stroke order is worse than teaching
 * none. But one rule holds for essentially every letter and is the thing learners most often get
 * wrong: the **shirorekha, the headline along the top, goes on last**. So the body is revealed
 * first, the pen lifts, and only then does the headline sweep across.
 *
 * That is a real improvement on the plain left-to-right curtain this replaces, which slid the
 * finished letter into view and taught nothing about how it is formed, and it works for every
 * vowel, consonant and barakhadi form without per-character authoring.
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
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // Density-fixed rather than a raw sp literal, so the glyph keeps its layout when the OS
    // font size is turned up instead of overflowing the drawing card.
    val glyphSize = with(density) { 190.dp.toSp() }
    val style = remember(glyphSize) {
        TextStyle(
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = glyphSize,
            lineHeight = glyphSize,
        )
    }

    val body = remember { Animatable(0f) }
    val headline = remember { Animatable(0f) }
    var trailVisible by remember { mutableStateOf(true) }

    LaunchedEffect(demoKey) {
        trailVisible = true
        body.snapTo(0f)
        headline.snapTo(0f)
        delay(250)
        body.animateTo(1f, tween(BODY_MS, easing = LinearEasing))
        delay(PEN_LIFT_MS)   // the pen lifts before the headline goes on
        headline.animateTo(1f, tween(HEADLINE_MS, easing = LinearEasing))
        delay(HOLD_MS)
        // Clear the coloured trail so the child traces on a clean guide.
        trailVisible = false
    }

    Canvas(modifier = modifier) {
        val layout = measurer.measure(char, style)
        val topLeft = Offset(
            (size.width - layout.size.width) / 2f,
            (size.height - layout.size.height) / 2f,
        )
        val fontPx = style.fontSize.toPx()
        val baseline = topLeft.y + layout.firstBaseline
        val headlineBottom = baseline - fontPx * HEADLINE_BOTTOM_ABOVE_BASELINE

        // The whole character, faint, as the trace target.
        drawText(layout, color = SKELETON_COLOR, topLeft = topLeft)

        if (!trailVisible) return@Canvas

        val ink = color.copy(alpha = 0.55f)

        // 1. Body first — everything below the headline band.
        if (body.value > 0f) {
            clipRect(
                left = 0f,
                top = headlineBottom,
                right = size.width * body.value,
                bottom = size.height,
            ) {
                drawText(layout, color = ink, topLeft = topLeft)
            }
        }

        // 2. Then the headline, sweeping across the top.
        if (headline.value > 0f) {
            clipRect(
                left = 0f,
                top = 0f,
                right = size.width * headline.value,
                bottom = headlineBottom,
            ) {
                drawText(layout, color = ink, topLeft = topLeft)
            }
        }
    }
}
