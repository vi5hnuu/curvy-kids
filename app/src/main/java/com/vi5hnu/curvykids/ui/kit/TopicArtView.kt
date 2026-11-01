package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.TopicArt
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidInk

/** Reference size the glyph is measured at before being scaled to fit. */
private val MEASURE_SIZE = 100.sp

/** Share of the box height the glyph may take, leaving room for its ascenders and marks. */
private const val HEIGHT_SHARE = 0.7f

/** Draws a [TopicArt] to fill the space it's given. Glyphs are set in [glyphColor]. */
@Composable
fun TopicArtView(art: TopicArt, glyphColor: Color, modifier: Modifier = Modifier) {
    when (art) {
        is TopicArt.Picture -> SvgImage(art.asset, modifier)
        is TopicArt.Glyph -> FittedGlyph(art.text, glyphColor, modifier)
        is TopicArt.LetterPicture -> LetterWithPicture(art, glyphColor, modifier)
    }
}

/**
 * Characters in front of a big, faded picture of their word: the apple fills the tile behind
 * "ABC", the pigeon behind "क". The picture is large enough to recognise at a glance, faded so
 * it never competes with the characters, and the characters carry a soft glow so they stay
 * crisp wherever they cross it (the usual fix for text over an image; a hard outline fills in
 * Devanagari's counters and headline).
 */
@Composable
private fun LetterWithPicture(art: TopicArt.LetterPicture, glyphColor: Color, modifier: Modifier) {
    // White characters sit on a coloured tile and take a soft dark edge; coloured characters
    // sit on a pale tile and take a white one.
    val onColour = glyphColor.luminance() > 0.8f
    val outline = if (onColour) KidInk.copy(alpha = 0.45f) else Color.White
    // The picture leans to the lower right and the characters to the upper left, so the two
    // overlap only in part: the characters stay whole on top, and enough of the picture shows
    // around them to tell what it is.
    BoxWithConstraints(modifier) {
        // On a wide tile the characters keep to the left part and start at its left edge, so
        // they cross only the edge of the picture instead of sitting on its middle.
        val wide = maxWidth > maxHeight * 1.2f
        SvgImage(
            art.asset,
            Modifier.fillMaxSize(if (wide) 0.98f else 0.84f).align(Alignment.BottomEnd).alpha(PICTURE_ALPHA),
        )
        FittedGlyph(
            art.text, glyphColor,
            Modifier.fillMaxWidth(if (wide) 0.7f else 0.86f).fillMaxHeight(0.78f).align(Alignment.TopStart),
            outline,
            alignment = if (wide) Alignment.CenterStart else Alignment.Center,
        )
    }
}

/** How strongly the picture behind the characters shows: clearly there, never louder. */
private const val PICTURE_ALPHA = 0.55f

/** [text] as large as fits the box, measured for real so wide scripts (Devanagari) never clip. */
@Composable
private fun FittedGlyph(
    text: String,
    color: Color,
    modifier: Modifier,
    outline: Color? = null,
    alignment: Alignment = Alignment.Center,
) {
    BoxWithConstraints(modifier, contentAlignment = alignment) {
        // Measure the real text once, then scale it to the box: an estimate from the
        // character count breaks on wide scripts such as Devanagari ("अ आ" wrapped and clipped).
        val measurer = rememberTextMeasurer()
        val style = TextStyle(fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = MEASURE_SIZE)
        val measuredWidth = remember(text) { measurer.measure(text, style, softWrap = false).size.width }
        val size = with(LocalDensity.current) {
            val byWidth = MEASURE_SIZE.value * (maxWidth.toPx() / measuredWidth.coerceAtLeast(1)) * 0.92f
            val byHeight = (maxHeight * HEIGHT_SHARE).toSp().value
            minOf(byWidth, byHeight).sp
        }
        // A blurred shadow of the glow colour right under the text: a halo that lifts it off
        // the picture behind.
        val glow = outline?.let { Shadow(it, Offset.Zero, with(LocalDensity.current) { size.toPx() } * 0.12f) }
        Text(text, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = size,
            lineHeight = size, color = color, textAlign = TextAlign.Center,
            maxLines = 1, softWrap = false, style = TextStyle(shadow = glow))
    }
}
