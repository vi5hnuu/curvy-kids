package com.vi5hnu.curvykids.ui.trace.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect

/**
 * Where a Devanagari glyph's structural parts sit, measured from the glyph itself.
 *
 * All values are in the coordinate space of the raster used to find them; callers scale them.
 * Any of the parts may be absent for a given letter (not every letter has a full stem).
 */
data class DevanagariParts(
    val inkLeft: Float,
    val inkTop: Float,
    val inkRight: Float,
    val inkBottom: Float,
    /** Top edge of the shirorekha, the headline that runs along the letter. */
    val headlineTop: Float,
    /** Bottom edge of the shirorekha. */
    val headlineBottom: Float,
    /** Column range of the khari pai, the full-height vertical stem, or null when there isn't one. */
    val stem: ClosedFloatingPointRange<Float>?,
) {
    val width: Float get() = inkRight - inkLeft
    val height: Float get() = inkBottom - inkTop
}

/**
 * Finds a Devanagari letter's parts by rasterising it and reading the ink.
 *
 * This exists so the tracing demo can show a letter being *written* rather than filled in.
 * Devanagari is taught in a consistent order — the body first, then the vertical stem, and the
 * headline last — and those three parts are structural, so they can be located in the glyph
 * instead of hand-authoring stroke data for 49 letters and 432 barakhadi forms. Measuring the
 * real glyph also means the guide matches exactly what the child sees on screen.
 *
 * @param paint The same paint the glyph will be drawn with, so the measurements line up.
 * @param box   Size of the square raster to analyse; 128 is ample and costs ~16k pixel reads.
 */
fun analyseDevanagari(char: String, paint: Paint, box: Int = 128): DevanagariParts? {
    if (char.isEmpty()) return null

    // Drawn as black on white in ARGB rather than into an ALPHA_8 mask: copying an ALPHA_8
    // bitmap to a readable config does not reliably carry the coverage through, which produced
    // a nonsense ink box and split the letter across its middle.
    val bitmap = Bitmap.createBitmap(box, box, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    val measure = Paint(paint).apply {
        textSize = box * 0.62f
        color = android.graphics.Color.BLACK
    }

    val bounds = Rect()
    measure.getTextBounds(char, 0, char.length, bounds)
    if (bounds.isEmpty) return null
    canvas.drawText(
        char,
        (box - bounds.width()) / 2f - bounds.left,
        (box - bounds.height()) / 2f - bounds.top,
        measure,
    )

    val pixels = IntArray(box * box)
    bitmap.getPixels(pixels, 0, box, 0, 0, box, box)
    bitmap.recycle()

    /** Ink is anything appreciably darker than the white ground. */
    fun on(x: Int, y: Int) = (pixels[y * box + x] and 0xFF) < 140

    var left = box; var right = -1; var top = box; var bottom = -1
    for (y in 0 until box) for (x in 0 until box) if (on(x, y)) {
        if (x < left) left = x; if (x > right) right = x
        if (y < top) top = y; if (y > bottom) bottom = y
    }
    if (right < 0) return null

    val inkW = (right - left + 1).toFloat()
    val inkH = (bottom - top + 1).toFloat()

    // The shirorekha is the widest horizontal run in the letter, so anchor on that rather than
    // on "the rows near the top": matras like the one in ओ, and the anusvara dot, sit ABOVE the
    // headline and shifted the ink top, which made the scan miss the headline entirely.
    fun runAt(y: Int): Int {
        var run = 0
        for (x in left..right) if (on(x, y)) run++
        return run
    }
    var bestY = top
    var bestRun = 0
    for (y in top..minOf(bottom, top + (inkH * 0.70f).toInt())) {
        val run = runAt(y)
        if (run > bestRun) { bestRun = run; bestY = y }
    }
    var headlineTop = top.toFloat()
    var headlineBottom = top.toFloat()
    if (bestRun >= inkW * 0.5f) {
        var y = bestY
        while (y > top && runAt(y - 1) >= inkW * 0.5f) y--
        headlineTop = y.toFloat()
        y = bestY
        while (y < bottom && runAt(y + 1) >= inkW * 0.5f) y++
        headlineBottom = (y + 1).toFloat()
    }

    // The khari pai: a column carrying ink down most of the body. Take the rightmost such run,
    // which is where the stem sits in almost every letter that has one.
    val bodyTop = headlineBottom.toInt()
    val bodyHeight = (bottom - bodyTop + 1).coerceAtLeast(1)
    val tall = BooleanArray(box)
    for (x in left..right) {
        var run = 0
        for (y in bodyTop..bottom) if (on(x, y)) run++
        tall[x] = run >= bodyHeight * 0.80f
    }
    var stemStart = -1; var stemEnd = -1
    var x = right
    while (x >= left) {
        if (tall[x]) {
            stemEnd = x
            while (x >= left && tall[x]) x--
            stemStart = x + 1
            break
        }
        x--
    }
    // A stem that swallows most of the letter is not a stem; that is a letter like a plain bar.
    val stem = if (stemStart >= 0 && (stemEnd - stemStart + 1) < inkW * 0.45f) {
        stemStart.toFloat()..(stemEnd + 1).toFloat()
    } else {
        null
    }

    return DevanagariParts(
        inkLeft = left.toFloat(),
        inkTop = top.toFloat(),
        inkRight = (right + 1).toFloat(),
        inkBottom = (bottom + 1).toFloat(),
        headlineTop = headlineTop,
        headlineBottom = headlineBottom,
        stem = stem,
    )
}
