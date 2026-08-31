package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/**
 * A framed badge whose content is a character — a Devanagari syllable, a letter, a numeral.
 *
 * Rendered natively rather than from an SVG on purpose. The badge assets for these are not
 * illustrations: each is a rounded rectangle plus a single `<text>` element, so rasterising one
 * buys nothing and adds a real failure mode — the SVG renderer has to resolve a font for the
 * glyph, and none of the families those files name are resolvable on Android, which is exactly
 * where Devanagari breaks. Compose's text stack does proper system font fallback, so the
 * syllable always renders, stays sharp at any size, and costs no image decode.
 *
 * Proportions mirror the badge assets (20% corner radius, 5% border, 40% glyph) so this sits
 * beside the illustrated badges without reading as a different component.
 *
 * @param text  The character(s) to show.
 * @param color Accent used for the border and the glyph.
 * @param tint  Pale fill behind the glyph.
 */
@Composable
fun LetterBadge(
    text: String,
    color: Color,
    tint: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        // Sized from the constraints rather than a post-layout callback, so the glyph is present
        // on the first frame instead of popping in.
        val side = minOf(maxWidth, maxHeight)
        val density = LocalDensity.current

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (contentDescription != null) {
                        Modifier.semantics { this.contentDescription = contentDescription }
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(percent = 20),
            color = tint,
            border = BorderStroke(width = side * 0.05f, color = color),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = with(density) { (side * 0.40f).toSp() },
                    lineHeight = with(density) { (side * 0.50f).toSp() },
                    color = color,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
    }
}
