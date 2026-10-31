package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.layout.BoxWithConstraints
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
        is TopicArt.Glyph -> BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
            // Measure the real text once, then scale it to the box: an estimate from the
            // character count breaks on wide scripts such as Devanagari ("अ आ" wrapped and clipped).
            val measurer = rememberTextMeasurer()
            val style = TextStyle(fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = MEASURE_SIZE)
            val measuredWidth = remember(art.text) { measurer.measure(art.text, style, softWrap = false).size.width }
            val size = with(LocalDensity.current) {
                val byWidth = MEASURE_SIZE.value * (maxWidth.toPx() / measuredWidth.coerceAtLeast(1)) * 0.92f
                val byHeight = (maxHeight * HEIGHT_SHARE).toSp().value
                minOf(byWidth, byHeight).sp
            }
            Text(art.text, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = size,
                lineHeight = size, color = glyphColor, textAlign = TextAlign.Center,
                maxLines = 1, softWrap = false)
        }
    }
}
