package com.vi5hnu.curvykids.ui.kit

import androidx.compose.runtime.staticCompositionLocalOf
import com.vi5hnu.curvykids.audio.PhonicsSpeaker

/**
 * The app's one voice, for shared pieces of UI that need to know whether it is still talking
 * (a celebration waiting for its praise to end, a back button waiting for a line) without every
 * screen passing it down. Null in previews and wherever nothing provides it; callers then fall
 * back to their plain timings.
 */
val LocalSpeaker = staticCompositionLocalOf<PhonicsSpeaker?> { null }
