package com.vi5hnu.curvykids.ui.game.components

import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import kotlin.math.min
import android.graphics.Path as AndroidPath

/**
 * All shape data derived from a single glyph. Built once per character/size change and
 * reused across animation frames so we pay the [Paint.getTextPath] cost at most once per
 * character transition.
 *
 * Glyph outlines have **multiple contours** (e.g. "A" has an outer triangle + inner hole).
 * [contours] stores each contour's Android path + its pre-measured length so animation can
 * span all contours seamlessly. [totalLength] is the sum of all contour lengths.
 */
data class GlyphData(
    val androidPath: AndroidPath,
    val contours: List<ContourData>,
    val totalLength: Float,
    val composePath: Path,
)

data class ContourData(val path: AndroidPath, val measure: PathMeasure, val length: Float)

/**
 * Builds [GlyphData] for [character] scaled and centred inside [width] × [height] px.
 * Returns null when the character produces an empty outline (shouldn't happen for A–Z / 0–9).
 */
@Suppress("DEPRECATION") // computeBounds(RectF, Boolean) deprecated in API 35; minSdk = 29
fun buildGlyphData(
    character: String,
    width: Float,
    height: Float,
    fillRatio: Float = 0.7f,
): GlyphData? {
    if (character.isEmpty() || width <= 0f || height <= 0f) return null

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textSize = height // arbitrary base; rescaled below
    }

    val path = AndroidPath()
    paint.getTextPath(character, 0, character.length, 0f, 0f, path)

    val bounds = RectF()
    path.computeBounds(bounds, true)
    if (bounds.width() <= 0f || bounds.height() <= 0f) return null

    val scale = min(width * fillRatio / bounds.width(), height * fillRatio / bounds.height())
    android.graphics.Matrix().apply {
        postTranslate(-bounds.centerX(), -bounds.centerY()) // origin at glyph centre
        postScale(scale, scale)
        postTranslate(width / 2f, height / 2f)              // move to canvas centre
    }.let { path.transform(it) }

    // Walk every contour in the glyph so animation covers the whole letter (e.g. A's inner
    // triangle, B/D/O/P/Q/R's holes) not just the first path segment.
    val contours = mutableListOf<ContourData>()
    val scanner = PathMeasure(path, false)
    do {
        if (scanner.length > 0f) {
            // Extract this contour as its own path so we can getSegment/getPosTan on it.
            val contourPath = AndroidPath()
            scanner.getSegment(0f, scanner.length, contourPath, true)
            val m = PathMeasure(contourPath, false)
            contours.add(ContourData(contourPath, m, m.length))
        }
    } while (scanner.nextContour())

    if (contours.isEmpty()) return null

    return GlyphData(
        androidPath = path,
        contours = contours,
        totalLength = contours.sumOf { it.length.toDouble() }.toFloat(),
        composePath = path.asComposePath(),
    )
}

/**
 * Returns the position on the glyph outline at [progress] (0..1 across all contours).
 * Walks contours in order, subtracting each length until the target is within the current one.
 */
fun GlyphData.positionAt(progress: Float): Offset? {
    var remaining = (totalLength * progress.coerceIn(0f, 1f))
    for (contour in contours) {
        if (remaining <= contour.length) {
            val pos = FloatArray(2)
            return if (contour.measure.getPosTan(remaining, pos, null)) Offset(pos[0], pos[1])
            else null
        }
        remaining -= contour.length
    }
    return null
}

/**
 * Builds a partial Android path covering [progress] (0..1) across all contours.
 * Used to draw the animated trail.
 */
fun GlyphData.trailPathAt(progress: Float): AndroidPath {
    val result = AndroidPath()
    var remaining = (totalLength * progress.coerceIn(0f, 1f))
    for (contour in contours) {
        when {
            remaining <= 0f -> break
            remaining >= contour.length -> {
                // Full contour fits in the trail.
                result.addPath(contour.path)
                remaining -= contour.length
            }
            else -> {
                // Partial contour — only draw up to `remaining`.
                contour.measure.getSegment(0f, remaining, result, true)
                break
            }
        }
    }
    return result
}

/** Convenience wrapper — returns only the Compose path for callers that don't animate. */
fun buildGlyphPath(
    character: String,
    width: Float,
    height: Float,
    fillRatio: Float = 0.7f,
): Path = buildGlyphData(character, width, height, fillRatio)?.composePath ?: Path()
