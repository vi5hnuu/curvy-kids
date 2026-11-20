package com.vi5hnu.curvykids.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.vi5hnu.curvykids.data.content.AssetPath
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

/** Neutral placeholder tone, matching the app's faint ink. */
private val PLACEHOLDER = Color(0xFFE4EDF4)

/**
 * Renders an SVG from `assets/art/…` verbatim, using the app-wide Coil loader
 * (configured with the SVG decoder in [com.vi5hnu.curvykids.CurvyKidsApp]).
 *
 * Pictures are bare artwork with no frame or name (see tools/art/unframe.py); use [SvgBadge] for
 * the framed, named card look.
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
 * @param asset Asset-relative path, e.g. `"art/animals/animal-cat.svg"` (no leading slash).
 *              Build these with [com.vi5hnu.curvykids.data.content.KidAssets], not by hand.
 * @param contentDescription Accessibility label.
 * @param fallback Drawn instead when [asset] is null or fails to load.
 */
@Composable
fun SvgImage(
    asset: AssetPath?,
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
            .data("file:///android_asset/${asset.path}")
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier,
        onError = { failed = true },
    )
}

/**
 * A picture as a card: a rounded tile in [color] with the picture and its [label] underneath.
 *
 * Pictures in `assets/art/` are bare artwork, so the frame and the name are drawn here, where
 * they can follow the screen's colour and size, instead of being baked into every file. A few
 * assets (numbers, barakhadi, days, colours, math symbols) still carry their own frame and text
 * until those screens draw them in Compose; those are shown as they are, so they never get a
 * second frame.
 *
 * Size it square, e.g. `Modifier.fillMaxWidth().aspectRatio(1f)`. The label is left out on very
 * small badges, where it couldn't be read anyway.
 */
@Composable
fun SvgBadge(
    asset: AssetPath?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    color: Color = BADGE_DEFAULT,
    label: String? = contentDescription,
    fallback: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val bare by produceState<Boolean?>(initialValue = null, asset) {
        value = asset?.let { a ->
            withContext(Dispatchers.IO) { runCatching { isBarePicture(context, a.path) }.getOrDefault(false) }
        } ?: false
    }
    when (bare) {
        null -> Box(modifier) // still reading; hold the space
        false -> SvgImage(asset, modifier, contentDescription, fallback)
        true -> BoxWithConstraints(
            modifier
                .clip(RoundedCornerShape(22))
                .background(Color.White)
                .background(color.copy(alpha = 0.14f))
                .border(BorderStroke(4.dp, color), RoundedCornerShape(22))
                .semantics { if (contentDescription != null) this.contentDescription = contentDescription },
        ) {
            val showLabel = label != null && maxWidth >= 72.dp
            val labelSize = with(LocalDensity.current) { (maxWidth * 0.14f).toSp() }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(maxWidth * 0.1f),
            ) {
                SvgImage(asset, Modifier.fillMaxWidth().weight(1f), contentDescription = null, fallback = fallback)
                if (showLabel) {
                    Text(
                        label!!,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = labelSize,
                        lineHeight = labelSize,
                        color = color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private val BADGE_DEFAULT = Color(0xFF1FC2AE)

/**
 * Whether [path] is a bare picture that needs a frame drawn around it: every picture drawn by
 * tools/art/draw is marked `data-bare="1"` on its root element.
 */
private fun isBarePicture(context: Context, path: String): Boolean {
    val head = context.assets.open(path).bufferedReader().use { r ->
        CharArray(200).let { buf -> String(buf, 0, r.read(buf).coerceAtLeast(0)) }
    }
    return "data-bare=\"1\"" in head
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
    asset: AssetPath?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colorFilter: ColorFilter? = null,
) {
    val context = LocalContext.current
    val stripped by produceState<ByteArray?>(initialValue = null, asset) {
        value = asset?.let { a ->
            withContext(Dispatchers.IO) { runCatching { captionlessSvg(context, a.path) }.getOrNull() }
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
        colorFilter = colorFilter,
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
