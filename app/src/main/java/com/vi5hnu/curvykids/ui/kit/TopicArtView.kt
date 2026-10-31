package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vi5hnu.curvykids.data.content.TopicArt
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/** Draws a [TopicArt] to fill the space it's given. Glyphs are set in [glyphColor]. */
@Composable
fun TopicArtView(art: TopicArt, glyphColor: Color, modifier: Modifier = Modifier) {
    when (art) {
        is TopicArt.Picture -> SvgImage(art.asset, modifier)
        is TopicArt.Glyph -> BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
            // Size the characters from the space, so "A" and "1+2" both fill the tile.
            val size = with(LocalDensity.current) {
                (minOf(maxHeight * 0.7f, maxWidth / (art.text.length.coerceAtLeast(1) * 0.62f))).toSp()
            }
            Text(art.text, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = size,
                lineHeight = size, color = glyphColor, textAlign = TextAlign.Center, maxLines = 1)
        }
    }
}
