package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen-size rules shared by the child screens, so phones and tablets are handled the same way
 * everywhere. Children often play on a family tablet, where a phone layout stretched edge to
 * edge leaves huge tiles and long reaches.
 */
object Responsive {
    /** Width from which a layout gets an extra column (small tablets, phones on their side). */
    val Wide: Dp = 600.dp

    /** Content never grows wider than this; the scene fills the rest. */
    val MaxContent: Dp = 1000.dp

    fun isWide(width: Dp) = width >= Wide

    /** Landscape with room for two panes (tablet on its side). */
    fun isTwoPane(width: Dp, height: Dp) = width >= 700.dp && width > height * 1.2f
}

/** Fills the width up to [Responsive.MaxContent]; centre it with the parent's alignment. */
fun Modifier.kidContentWidth(): Modifier = this.widthIn(max = Responsive.MaxContent).fillMaxWidth()
