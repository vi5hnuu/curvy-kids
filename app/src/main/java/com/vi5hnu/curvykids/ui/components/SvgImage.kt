package com.vi5hnu.curvykids.ui.components

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/** Neutral placeholder tone, matching the app's faint ink. */
private val PLACEHOLDER = Color(0xFFE4EDF4)

/**
 * Renders a kids-asset SVG from `assets/kids/…` verbatim, using the app-wide Coil loader
 * (configured with the SVG decoder in [com.vi5hnu.curvykids.CurvyKidsApp]).
 *
 * The badge SVGs are self-contained, hand-drawn illustrations — they carry their own rounded
 * frame, artwork and name label — so they're rendered exactly as authored (`ContentScale.Fit`)
 * and must be placed on their own, never inside another card.
 *
 * There is deliberately no *emoji* fallback: emoji render differently on every OEM and Android
 * version, ignore the colour they are given, and scale with the user's font setting rather than
 * the layout, so mixing them with the hand-drawn set made the app look like two different apps.
 *
 * A caller may still supply a [fallback] composable for the case where the asset cannot be
 * drawn. That matters for badges whose content is a character rather than an illustration —
 * the SVG renderer has to resolve a font for those, and for Devanagari it may not manage it.
 * Without a fallback the child is shown an empty card. Default is a quiet placeholder.
 *
 * @param asset Asset-relative path, e.g. `"kids/animals/animal-cat.svg"` (no leading slash).
 *              Build these with [com.vi5hnu.curvykids.data.content.KidAssets], not by hand.
 * @param contentDescription Accessibility label.
 * @param fallback Drawn instead when [asset] is null or fails to load.
 */
@Composable
fun SvgImage(
    asset: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    fallback: (@Composable () -> Unit)? = null,
) {
    var failed by remember(asset) { mutableStateOf(false) }

    if (asset == null || failed) {
        Box(modifier = modifier) {
            if (fallback != null) {
                fallback()
            } else {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(PLACEHOLDER, cornerRadius = CornerRadius(size.minDimension * 0.18f))
                }
            }
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
 * Semantic alias for a kids-asset badge. The asset SVGs already carry their frame, art and name
 * and size themselves to the (square) space the caller gives them — use e.g.
 * `Modifier.fillMaxWidth().aspectRatio(1f)`. Kept as a named wrapper so call sites read as
 * "badge" and so any future badge-only treatment lives in one place.
 */
@Composable
fun SvgBadge(
    asset: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    fallback: (@Composable () -> Unit)? = null,
) {
    SvgImage(
        asset = asset,
        modifier = modifier,
        contentDescription = contentDescription,
        fallback = fallback,
    )
}

/**
 * Renders a badge with its printed name removed.
 *
 * The illustrated badges carry the item's name along the bottom, which is exactly right when the
 * child is exploring ("this is a Lion") and exactly wrong in a quiz, where the picture is the
 * question and the caption is the answer. Quiz options therefore render through here.
 *
 * The caption is identified by its geometry rather than by editing 1166 files: it is small type
 * low in the viewBox. The large glyphs that *are* the content — numerals, Latin and Devanagari
 * letterforms, math symbols — sit higher and are set much larger, so they are left alone.
 */
@Composable
fun SvgArt(
    asset: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val context = LocalContext.current
    val stripped by produceState<ByteArray?>(initialValue = null, asset) {
        value = asset?.let { path ->
            withContext(Dispatchers.IO) { runCatching { captionlessSvg(context, path) }.getOrNull() }
        }
    }

    val bytes = stripped
    if (bytes == null) {
        // Still loading, or unreadable: hold the space rather than flashing a placeholder in.
        Box(modifier = modifier)
        return
    }

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(ByteBuffer.wrap(bytes))
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier,
    )
}

/** Small type low in the viewBox is a caption, not artwork. */
private val CAPTION = Regex(
    """<text[^>]*?y="([\d.]+)"[^>]*?font-size="([\d.]+)"[^>]*?>[^<]*</text>"""
)

private const val CAPTION_MAX_FONT_SIZE = 14f
private const val CAPTION_MIN_Y = 70f

/** Reads [path] from assets and returns its bytes with any caption text removed. */
private fun captionlessSvg(context: Context, path: String): ByteArray {
    val raw = context.assets.open(path).bufferedReader().use { it.readText() }
    val cleaned = CAPTION.replace(raw) { match ->
        val y = match.groupValues[1].toFloatOrNull() ?: return@replace match.value
        val size = match.groupValues[2].toFloatOrNull() ?: return@replace match.value
        if (size <= CAPTION_MAX_FONT_SIZE && y >= CAPTION_MIN_Y) "" else match.value
    }
    return cleaned.toByteArray()
}
