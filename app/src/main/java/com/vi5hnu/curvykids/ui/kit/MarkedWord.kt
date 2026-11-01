package com.vi5hnu.curvykids.ui.kit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.KidInk

/**
 * A word with one part of it picked out on a soft pill, the way a primer underlines the letter
 * being taught: "**का**न", "Fo**x**", "ग**ङ्गा**".
 *
 * The pill is drawn from the laid-out text, so it hugs the real glyphs in any script. [mark] must
 * fall on whole written syllables (see `syllableSpan`): Devanagari is shaped per cluster, and a
 * range that split one would draw the pill over half a glyph.
 *
 * @param mark indices of [word] to pick out; an empty or out-of-range mark shows the plain word.
 * @param color colour of the marked letters; the pill is a light wash of it.
 */
@Composable
fun MarkedWord(
    word: String,
    mark: IntRange,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 40.sp,
    ink: Color = KidInk,
) {
    val valid = !mark.isEmpty() && mark.first >= 0 && mark.last < word.length
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    val text = remember(word, mark, color, ink) {
        buildAnnotatedString {
            if (!valid) {
                withStyle(SpanStyle(color = ink)) { append(word) }
                return@buildAnnotatedString
            }
            withStyle(SpanStyle(color = ink)) { append(word.substring(0, mark.first)) }
            withStyle(SpanStyle(color = color)) { append(word.substring(mark.first, mark.last + 1)) }
            withStyle(SpanStyle(color = ink)) { append(word.substring(mark.last + 1)) }
        }
    }
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight.ExtraBold,
        onTextLayout = { layout = it },
        maxLines = 1,
        modifier = modifier.drawBehind {
            val l = layout ?: return@drawBehind
            if (!valid) return@drawBehind
            val box = l.getPathForRange(mark.first, mark.last + 1).getBounds()
            if (box.isEmpty) return@drawBehind
            val pad = size.height * 0.08f
            drawRoundRect(
                color = color.copy(alpha = 0.18f),
                topLeft = Offset(box.left - pad, pad * 0.5f),
                size = Size(box.width + pad * 2, size.height - pad),
                cornerRadius = CornerRadius(size.height * 0.28f),
            )
        },
    )
}
