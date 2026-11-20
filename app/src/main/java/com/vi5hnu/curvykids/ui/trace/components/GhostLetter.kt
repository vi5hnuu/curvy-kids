package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/**
 * The character to copy, drawn plainly underneath the child's writing.
 *
 * Static on purpose: the letter simply sits in the card for the child to trace over, with no
 * reveal or stroke animation. Shared by the tracing screen and the barakhadi writing page so both
 * present the letter the same way.
 */
@Composable
fun GhostLetter(char: String, modifier: Modifier = Modifier) {
    // Sized in dp converted to sp, so the glyph keeps its place in the card at any OS font scale.
    val size = with(LocalDensity.current) { GHOST_SIZE.toSp() }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = char,
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = size,
            lineHeight = size,
            color = GHOST_COLOR,
        )
    }
}

private val GHOST_SIZE = 190.dp

/** Light enough to write over, dark enough to read without anything drawing it in. */
private val GHOST_COLOR = Color(0xFFD5E1EC)
