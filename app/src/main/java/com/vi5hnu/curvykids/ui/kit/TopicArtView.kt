package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.TopicArt
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay

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
        // The characters fill the upper left; the word's picture sits on a white disc in the
        // lower right like a sticker, overlapping a little so the two read as one.
        is TopicArt.LetterPicture -> Box(modifier) {
            FittedGlyph(art.text, glyphColor, Modifier.fillMaxWidth(0.78f).fillMaxHeight(0.72f).align(Alignment.TopStart))
            Box(
                Modifier
                    .fillMaxHeight(0.6f)
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                SvgImage(art.asset, Modifier.fillMaxSize(0.86f))
            }
        }
    }
}

/** [text] as large as fits the box, measured for real so wide scripts (Devanagari) never clip. */
@Composable
private fun FittedGlyph(text: String, color: Color, modifier: Modifier) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
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
        Text(text, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = size,
            lineHeight = size, color = color, textAlign = TextAlign.Center,
            maxLines = 1, softWrap = false)
    }
}
