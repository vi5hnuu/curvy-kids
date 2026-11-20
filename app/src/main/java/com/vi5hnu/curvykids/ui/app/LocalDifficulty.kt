package com.vi5hnu.curvykids.ui.app

import androidx.compose.runtime.compositionLocalOf
import com.vi5hnu.curvykids.data.app.Difficulty

/**
 * The active [Difficulty], provided once at the navigation root.
 *
 * Supplied as a CompositionLocal rather than a parameter because it is a cross-cutting setting
 * that a handful of leaf composables care about and two dozen intermediate ones do not — passing
 * it through every activity screen's signature would be noise on all of them.
 */
val LocalDifficulty = compositionLocalOf { Difficulty.NORMAL }
