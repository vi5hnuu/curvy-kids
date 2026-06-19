package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/**
 * Renders a kids-asset SVG badge from `assets/kids/…` using the app-wide Coil loader
 * (configured with the SVG decoder in [com.vi5hnu.curvykids.CurvyKidsApp]).
 *
 * Falls back to the [fallbackEmoji] text whenever [asset] is null or the SVG fails to load, so any
 * item without a matching asset keeps rendering its original emoji — nothing is ever lost.
 *
 * NOTE: this draws the SVG verbatim. The picture badges embed their illustration as a *color-emoji*
 * `<text>` glyph, which Coil's AndroidSVG decoder cannot render — for those, prefer [SvgBadge],
 * which redraws the emoji with Compose. This plain renderer is kept for call sites that only ever
 * use path-based SVGs and as [SvgBadge]'s base layer.
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

// The badge frame geometry baked into every kids SVG (`viewBox="0 0 100 100"`):
// `<rect x=7 y=7 width=86 height=86 rx=20 stroke-width=5/>`. We mirror it in Compose to repaint
// the border in the screen theme without editing the asset files.
private const val FRAME_INSET = 7f
private const val FRAME_SIZE = 86f
private const val FRAME_RADIUS = 20f
private const val FRAME_STROKE = 5f

/**
 * The preferred renderer for kids-asset badges. It draws the SVG faithfully (frame + name + any
 * baked vector art) and fixes the two things plain [SvgImage] can't, entirely in Compose — the SVG
 * files are never modified:
 *
 *  1. **Color emoji.** The picture badges store their illustration as a `<text>🦡</text>`
 *     color-emoji glyph, which AndroidSVG renders as blank/tofu. [SvgBadge] redraws that emoji on
 *     top with a Compose [Text] (which renders full-color emoji). Pass [emoji] = the item's emoji;
 *     pass null for text/path badges (e.g. Barakhadi syllables) that need no overlay.
 *  2. **Theme color.** When [themeColor] is given, a border stroke matching the SVG's own frame
 *     geometry is painted over it, so the badge's outline takes the screen's topic color instead of
 *     the asset's baked color.
 *
 * The badge sizes itself to fill the (square) space the caller gives it — use e.g.
 * `Modifier.fillMaxWidth().aspectRatio(1f)`. The SVG already carries its own rounded frame, so it
 * must be rendered on its own, never inside another card.
 *
 * @param emoji          Emoji to draw over the frame; null for badges with no emoji slot.
 * @param themeColor     Recolors the badge border to the screen theme; null keeps the asset's color.
 * @param emojiCenterY   Vertical centre of the emoji as a fraction of height (picture cards ≈0.42,
 *                       letter cards ≈0.50), matching where the SVG places it.
 * @param emojiSize      Emoji height as a fraction of the badge height (SVG uses ~0.44–0.46).
 */
@Composable
fun SvgBadge(
    asset: String?,
    emoji: String?,
    fallbackEmoji: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    themeColor: Color? = null,
    emojiCenterY: Float = 0.42f,
    emojiSize: Float = 0.46f,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        // Base: the SVG exactly as authored (frame, label, and any path art). In a square box with
        // the square viewBox it fills edge-to-edge, so our overlays line up with it 1:1.
        SvgImage(
            asset = asset,
            fallbackEmoji = if (emoji.isNullOrEmpty()) fallbackEmoji else null,
            modifier = Modifier.fillMaxSize(),
            contentDescription = contentDescription,
        )

        // Repaint the frame border in the theme colour (over the SVG's identical stroke).
        if (themeColor != null) {
            Canvas(Modifier.fillMaxSize()) {
                val u = size.minDimension / 100f
                drawRoundRect(
                    color = themeColor,
                    topLeft = Offset(FRAME_INSET * u, FRAME_INSET * u),
                    size = Size(FRAME_SIZE * u, FRAME_SIZE * u),
                    cornerRadius = CornerRadius(FRAME_RADIUS * u, FRAME_RADIUS * u),
                    // Slightly wider than the SVG's 5u stroke so it fully covers it (no colour seam).
                    style = Stroke(width = FRAME_STROKE * 1.1f * u),
                )
            }
        }

        // Redraw the emoji AndroidSVG can't, at the slot the SVG reserves for it.
        if (!emoji.isNullOrEmpty()) {
            Text(
                text = emoji,
                fontSize = (emojiSize * maxHeight.value).sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = ((emojiCenterY - 0.5f) * maxHeight.value).dp),
            )
        }
    }
}
