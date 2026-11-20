package com.vi5hnu.curvykids.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sizes for screens a child uses. Young children tap imprecisely and can't read small print, so
 * targets and text start well above the Material defaults (research for ages 3–6 suggests
 * targets of at least ~75 px with a forgiving hit area, and large, simple type).
 */
object KidDimens {
    /** Smallest thing a child is expected to tap. */
    val MinTarget = 72.dp

    /** Back button and other header controls. */
    val HeaderButton = 64.dp

    /** Depth of the solid "candy" edge under a raised card or button. */
    val CandyEdge = 6.dp

    val CardRadius = 30.dp
    val ScreenPadding = 18.dp
    val Gap = 14.dp

    /** Smallest text a child sees. */
    val MinText = 18.sp
    val TileTitle = 22.sp
    val ScreenTitle = 30.sp
}
