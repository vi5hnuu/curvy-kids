package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Renders a kids-asset SVG badge from `assets/kids/…` verbatim, using the app-wide Coil loader
 * (configured with the SVG decoder in [com.vi5hnu.curvykids.CurvyKidsApp]).
 *
 * The badge SVGs are self-contained, hand-drawn illustrations — they carry their own rounded frame,
 * artwork and name label — so they're rendered exactly as authored (`ContentScale.Fit`) and must be
 * placed on their own, never inside another card.
 *
 * Falls back to the [fallbackEmoji] text whenever [asset] is null or the SVG fails to load, so any
 * item without a matching asset keeps rendering something — nothing is ever lost.
 *
 * @param asset          Asset-relative path, e.g. `"kids/animals/animal-cat.svg"` (no leading slash).
 * @param fallbackEmoji  Emoji shown if [asset] is missing/unloadable.
 * @param fallbackSize   Font size for the fallback emoji (matches the visual the SVG would fill).
 * @param contentDescription Accessibility label.
 */
@Composable
fun SvgImage(
    asset: String?,
    fallbackEmoji: String?,
    modifier: Modifier = Modifier,
    fallbackSize: TextUnit = 44.sp,
    contentDescription: String? = null,
) {
    var failed by remember(asset) { mutableStateOf(false) }

    if (asset == null || failed) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            if (!fallbackEmoji.isNullOrEmpty()) Text(fallbackEmoji, fontSize = fallbackSize)
        }
        return
    }

    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/$asset")
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier,
        onError = { failed = true },
    )
}

/**
 * Semantic alias for a kids-asset badge. The asset SVGs already carry their frame, art and name and
 * size themselves to the (square) space the caller gives them — use e.g.
 * `Modifier.fillMaxWidth().aspectRatio(1f)`. Kept as a named wrapper so call sites read as "badge"
 * and so any future badge-only treatment lives in one place.
 */
@Composable
fun SvgBadge(
    asset: String?,
    fallbackEmoji: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    SvgImage(
        asset = asset,
        fallbackEmoji = fallbackEmoji,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}
