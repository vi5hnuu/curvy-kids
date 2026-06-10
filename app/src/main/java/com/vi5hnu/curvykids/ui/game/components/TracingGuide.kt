package com.vi5hnu.curvykids.ui.game.components

import android.graphics.Paint
import android.graphics.PathMeasure
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import kotlin.math.min
import android.graphics.Path as AndroidPath

/**
 * All shape data derived from a single glyph. Built once per character/size change and
 * reused across animation frames so we pay the [Paint.getTextPath] + [PathMeasure] cost
 * at most once per character transition.
 *
 * @param androidPath the raw (Android) glyph outline after scaling/centering.
 * @param measure     [PathMeasure] on [androidPath]; [getPosTan]/[getSegment] are safe to
 *                    call repeatedly because they do not advance the internal contour cursor.
 * @param composePath Compose equivalent of [androidPath] used for static dashed rendering.
 */
data class GlyphData(
    val androidPath: AndroidPath,
    val measure: PathMeasure,
    val composePath: Path,
)

/**
 * Builds [GlyphData] for [character] scaled and centred inside [width] × [height] px.
 * Returns null when the character produces an empty outline (shouldn't happen for A–Z / 0–9).
 */
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

    return GlyphData(
        androidPath = path,
        measure = PathMeasure(path, false),
        composePath = path.asComposePath(),
    )
}

/** Convenience wrapper — returns only the Compose path for callers that don't animate. */
fun buildGlyphPath(
    character: String,
    width: Float,
    height: Float,
    fillRatio: Float = 0.7f,
): Path = buildGlyphData(character, width, height, fillRatio)?.composePath ?: Path()
