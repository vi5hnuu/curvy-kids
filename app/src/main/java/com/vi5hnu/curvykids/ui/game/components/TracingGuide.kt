package com.vi5hnu.curvykids.ui.game.components

import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposePath
import kotlin.math.min
import android.graphics.Path as AndroidPath

/**
 * Builds a centred, scaled glyph outline for [character] that can be drawn on the canvas as
 * a dashed tracing guide. The shape is derived from the font at runtime (no image assets).
 *
 * @param width  target canvas width in px.
 * @param height target canvas height in px.
 * @param fillRatio fraction of the canvas the glyph should occupy.
 */
fun buildGlyphPath(
    character: String,
    width: Float,
    height: Float,
    fillRatio: Float = 0.7f,
): Path {
    if (character.isEmpty() || width <= 0f || height <= 0f) return Path()

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        textSize = height // arbitrary base size; we rescale to fit below
    }

    val path = AndroidPath()
    paint.getTextPath(character, 0, character.length, 0f, 0f, path)

    val bounds = RectF()
    path.computeBounds(bounds, true)
    if (bounds.width() <= 0f || bounds.height() <= 0f) return Path()

    val scale = min(width * fillRatio / bounds.width(), height * fillRatio / bounds.height())
    val matrix = android.graphics.Matrix().apply {
        postTranslate(-bounds.centerX(), -bounds.centerY()) // origin at glyph centre
        postScale(scale, scale)
        postTranslate(width / 2f, height / 2f)              // move to canvas centre
    }
    path.transform(matrix)
    return path.asComposePath()
}
