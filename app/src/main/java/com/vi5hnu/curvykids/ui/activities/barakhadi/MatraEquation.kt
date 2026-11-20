package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Matra
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.KidInk

/**
 * How a syllable is built, as a sum a child can follow: **[क] + [आ ◌ा] = [का]**.
 *
 * The consonant stays in ink, the vowel and its sign take the matra's colour, and the result is
 * the largest tile, so the eye lands on what was made. Works for every one of the 432 forms; for
 * अ, which has no sign, it shows क + अ = क.
 *
 * @param tile edge of the two input tiles; the result tile is a third bigger.
 */
@Composable
fun MatraEquation(
    consonant: String,
    matra: Matra,
    syllable: String,
    modifier: Modifier = Modifier,
    tile: Dp = 64.dp,
) {
    val color = matraColor(matra)
    val tint = matraTint(matra)
    val sign = matraSignLabel(matra)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(tile * 0.14f),
    ) {
        Tile(tile, ConsonantFace) { Glyph(consonant, KidInk, tile) }
        Operator("+", tile)
        // A little wider than tall, so the vowel and its sign can both be read at a glance.
        Tile(tile, tint, width = if (sign.isEmpty()) tile else tile * 1.2f) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Glyph(matra.vowel, color, if (sign.isEmpty()) tile else tile * 0.78f, lineScale = 0.56f)
                if (sign.isNotEmpty()) Glyph(sign, color, tile * 0.62f, lineScale = 0.56f)
            }
        }
        Operator("=", tile)
        Tile(tile * 1.34f, color) { Glyph(syllable, Color.White, tile * 1.34f) }
    }
}

/** A pale ink face for the consonant, visible on a white card and on the sky alike. */
private val ConsonantFace = Color(0xFFEDEFF4)

@Composable
private fun Tile(edge: Dp, face: Color, width: Dp = edge, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = width, height = edge)
            .clip(RoundedCornerShape(edge * 0.28f))
            .background(face),
        contentAlignment = Alignment.Center,
    ) { content() }
}

/**
 * A glyph sized to sit inside a tile of [box]; Devanagari needs room above and below, so the
 * line is taller than the type unless [lineScale] packs two lines into one tile.
 */
@Composable
private fun Glyph(text: String, color: Color, box: Dp, lineScale: Float = 0.62f) {
    Text(
        text = text,
        color = color,
        fontWeight = FontWeight.ExtraBold,
        fontSize = (box.value * 0.5f).sp,
        lineHeight = (box.value * lineScale).sp,
        maxLines = 1,
    )
}

@Composable
private fun Operator(symbol: String, tile: Dp) {
    Text(
        text = symbol,
        fontFamily = FontDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = (tile.value * 0.42f).sp,
        color = InkSoft,
    )
}
