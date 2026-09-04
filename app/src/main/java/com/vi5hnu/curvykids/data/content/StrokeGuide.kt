package com.vi5hnu.curvykids.data.content

/**
 * One segment of a guide stroke, in the unit box described by [StrokeGuide].
 * Only lines and quadratic curves are needed to describe manuscript letterforms.
 */
sealed interface GuideSeg {
    data class Line(val x: Float, val y: Float) : GuideSeg
    data class Quad(val cx: Float, val cy: Float, val x: Float, val y: Float) : GuideSeg
}

/**
 * A single pen-down..pen-up stroke: where the pen starts and the path it travels.
 * Stroke order within a character, and direction within a stroke, are both meaningful —
 * they are what the child is being taught.
 */
data class GuideStroke(val startX: Float, val startY: Float, val segments: List<GuideSeg>)

/**
 * Handwriting stroke order for the Latin characters the app teaches.
 *
 * Each character is a list of strokes in the order a child should draw them, described in a
 * unit box: (0,0) is top-left and (1,1) bottom-right, y increasing downward — the same
 * orientation as a Compose canvas, so rendering is a straight multiply by the box size.
 *
 * Vertical landmarks follow standard manuscript teaching:
 *   0.12 cap height / ascender · 0.40 x-height · 0.88 baseline · 0.99 descender.
 *
 * Devanagari letters are authored the same way, against the real letterform, and hang from the
 * shirorekha: the body is written first, then the khari pai, then the headline last. Letters
 * without data yet fall back to the plain reveal, which shows the shape but not the motion.
 */
object StrokeGuide {

    /** The strokes for [character], or null when no guide has been authored for it. */
    fun forCharacter(character: String): List<GuideStroke>? {
        DEVANAGARI[character]?.let { return it }
        val c = character.singleOrNull() ?: return null
        return when (c) {
            in 'A'..'Z' -> UPPERCASE[c]
            in 'a'..'z' -> LOWERCASE[c]
            in '0'..'9' -> DIGITS[c]
            else -> null
        }
    }

    // ── Tiny builders so the tables below read as shapes, not as constructor noise ──────
    private fun s(x: Float, y: Float, vararg seg: GuideSeg) = GuideStroke(x, y, seg.toList())
    private fun l(x: Float, y: Float): GuideSeg = GuideSeg.Line(x, y)
    private fun q(cx: Float, cy: Float, x: Float, y: Float): GuideSeg = GuideSeg.Quad(cx, cy, x, y)

    /** The bowl shared by a/d/g/q, drawn anticlockwise from the right — "around". */
    private fun lowerBowl() = s(
        0.72f, 0.50f,
        q(0.64f, 0.40f, 0.48f, 0.40f), q(0.26f, 0.40f, 0.26f, 0.64f),
        q(0.26f, 0.88f, 0.48f, 0.88f), q(0.68f, 0.88f, 0.72f, 0.74f), l(0.72f, 0.50f),
    )

    private val UPPERCASE: Map<Char, List<GuideStroke>> = mapOf(
        'A' to listOf(
            s(0.50f, 0.12f, l(0.18f, 0.88f)),
            s(0.50f, 0.12f, l(0.82f, 0.88f)),
            s(0.29f, 0.62f, l(0.71f, 0.62f)),
        ),
        'B' to listOf(
            s(0.26f, 0.12f, l(0.26f, 0.88f)),
            s(0.26f, 0.12f, q(0.72f, 0.12f, 0.72f, 0.31f), q(0.72f, 0.50f, 0.26f, 0.50f)),
            s(0.26f, 0.50f, q(0.78f, 0.50f, 0.78f, 0.69f), q(0.78f, 0.88f, 0.26f, 0.88f)),
        ),
        'C' to listOf(
            s(
                0.78f, 0.28f, q(0.70f, 0.12f, 0.50f, 0.12f), q(0.20f, 0.12f, 0.20f, 0.50f),
                q(0.20f, 0.88f, 0.50f, 0.88f), q(0.70f, 0.88f, 0.78f, 0.72f),
            ),
        ),
        'D' to listOf(
            s(0.26f, 0.12f, l(0.26f, 0.88f)),
            s(0.26f, 0.12f, q(0.80f, 0.12f, 0.80f, 0.50f), q(0.80f, 0.88f, 0.26f, 0.88f)),
        ),
        'E' to listOf(
            s(0.28f, 0.12f, l(0.28f, 0.88f)),
            s(0.28f, 0.12f, l(0.76f, 0.12f)),
            s(0.28f, 0.50f, l(0.68f, 0.50f)),
            s(0.28f, 0.88f, l(0.76f, 0.88f)),
        ),
        'F' to listOf(
            s(0.28f, 0.12f, l(0.28f, 0.88f)),
            s(0.28f, 0.12f, l(0.76f, 0.12f)),
            s(0.28f, 0.50f, l(0.68f, 0.50f)),
        ),
        'G' to listOf(
            s(
                0.78f, 0.28f, q(0.70f, 0.12f, 0.50f, 0.12f), q(0.20f, 0.12f, 0.20f, 0.50f),
                q(0.20f, 0.88f, 0.50f, 0.88f), q(0.78f, 0.88f, 0.78f, 0.60f), l(0.56f, 0.60f),
            ),
        ),
        'H' to listOf(
            s(0.24f, 0.12f, l(0.24f, 0.88f)),
            s(0.76f, 0.12f, l(0.76f, 0.88f)),
            s(0.24f, 0.50f, l(0.76f, 0.50f)),
        ),
        'I' to listOf(
            s(0.50f, 0.12f, l(0.50f, 0.88f)),
            s(0.30f, 0.12f, l(0.70f, 0.12f)),
            s(0.30f, 0.88f, l(0.70f, 0.88f)),
        ),
        'J' to listOf(
            s(0.62f, 0.12f, l(0.62f, 0.70f), q(0.62f, 0.88f, 0.44f, 0.88f), q(0.28f, 0.88f, 0.26f, 0.74f)),
            s(0.40f, 0.12f, l(0.80f, 0.12f)),
        ),
        'K' to listOf(
            s(0.26f, 0.12f, l(0.26f, 0.88f)),
            s(0.76f, 0.12f, l(0.26f, 0.52f)),
            s(0.42f, 0.42f, l(0.78f, 0.88f)),
        ),
        'L' to listOf(
            s(0.28f, 0.12f, l(0.28f, 0.88f)),
            s(0.28f, 0.88f, l(0.76f, 0.88f)),
        ),
        // Manuscript M and N start at the baseline and pull up.
        'M' to listOf(
            s(0.20f, 0.88f, l(0.20f, 0.12f)),
            s(0.20f, 0.12f, l(0.50f, 0.62f)),
            s(0.50f, 0.62f, l(0.80f, 0.12f)),
            s(0.80f, 0.12f, l(0.80f, 0.88f)),
        ),
        'N' to listOf(
            s(0.24f, 0.88f, l(0.24f, 0.12f)),
            s(0.24f, 0.12f, l(0.76f, 0.88f)),
            s(0.76f, 0.88f, l(0.76f, 0.12f)),
        ),
        'O' to listOf(
            s(
                0.50f, 0.12f, q(0.20f, 0.12f, 0.20f, 0.50f), q(0.20f, 0.88f, 0.50f, 0.88f),
                q(0.80f, 0.88f, 0.80f, 0.50f), q(0.80f, 0.12f, 0.50f, 0.12f),
            ),
        ),
        'P' to listOf(
            s(0.26f, 0.12f, l(0.26f, 0.88f)),
            s(0.26f, 0.12f, q(0.78f, 0.12f, 0.78f, 0.33f), q(0.78f, 0.54f, 0.26f, 0.54f)),
        ),
        'Q' to listOf(
            s(
                0.50f, 0.12f, q(0.20f, 0.12f, 0.20f, 0.50f), q(0.20f, 0.88f, 0.50f, 0.88f),
                q(0.80f, 0.88f, 0.80f, 0.50f), q(0.80f, 0.12f, 0.50f, 0.12f),
            ),
            s(0.60f, 0.66f, l(0.84f, 0.94f)),
        ),
        'R' to listOf(
            s(0.26f, 0.12f, l(0.26f, 0.88f)),
            s(0.26f, 0.12f, q(0.76f, 0.12f, 0.76f, 0.33f), q(0.76f, 0.54f, 0.26f, 0.54f)),
            s(0.46f, 0.54f, l(0.78f, 0.88f)),
        ),
        'S' to listOf(
            s(
                0.76f, 0.24f, q(0.70f, 0.12f, 0.48f, 0.12f), q(0.24f, 0.12f, 0.24f, 0.32f),
                q(0.24f, 0.50f, 0.52f, 0.52f), q(0.80f, 0.55f, 0.78f, 0.72f),
                q(0.76f, 0.88f, 0.48f, 0.88f), q(0.28f, 0.88f, 0.22f, 0.76f),
            ),
        ),
        'T' to listOf(
            s(0.20f, 0.12f, l(0.80f, 0.12f)),
            s(0.50f, 0.12f, l(0.50f, 0.88f)),
        ),
        'U' to listOf(
            s(
                0.22f, 0.12f, l(0.22f, 0.62f), q(0.22f, 0.88f, 0.50f, 0.88f),
                q(0.78f, 0.88f, 0.78f, 0.62f), l(0.78f, 0.12f),
            ),
        ),
        'V' to listOf(s(0.20f, 0.12f, l(0.50f, 0.88f), l(0.80f, 0.12f))),
        'W' to listOf(s(0.16f, 0.12f, l(0.34f, 0.88f), l(0.50f, 0.36f), l(0.66f, 0.88f), l(0.84f, 0.12f))),
        'X' to listOf(
            s(0.22f, 0.12f, l(0.78f, 0.88f)),
            s(0.78f, 0.12f, l(0.22f, 0.88f)),
        ),
        'Y' to listOf(
            s(0.22f, 0.12f, l(0.50f, 0.50f)),
            s(0.78f, 0.12f, l(0.50f, 0.50f), l(0.50f, 0.88f)),
        ),
        'Z' to listOf(s(0.22f, 0.12f, l(0.78f, 0.12f), l(0.22f, 0.88f), l(0.78f, 0.88f))),
    )

    private val LOWERCASE: Map<Char, List<GuideStroke>> = mapOf(
        // a/d/g/q share the bowl, then add their stem — "around, then down".
        'a' to listOf(lowerBowl(), s(0.72f, 0.40f, l(0.72f, 0.88f))),
        'b' to listOf(
            s(0.28f, 0.12f, l(0.28f, 0.88f)),
            s(
                0.28f, 0.56f, q(0.36f, 0.40f, 0.52f, 0.40f), q(0.74f, 0.40f, 0.74f, 0.64f),
                q(0.74f, 0.88f, 0.52f, 0.88f), q(0.34f, 0.88f, 0.28f, 0.74f),
            ),
        ),
        'c' to listOf(
            s(
                0.72f, 0.50f, q(0.64f, 0.40f, 0.48f, 0.40f), q(0.26f, 0.40f, 0.26f, 0.64f),
                q(0.26f, 0.88f, 0.48f, 0.88f), q(0.64f, 0.88f, 0.72f, 0.78f),
            ),
        ),
        'd' to listOf(lowerBowl(), s(0.72f, 0.12f, l(0.72f, 0.88f))),
        'e' to listOf(
            s(
                0.26f, 0.64f, l(0.70f, 0.64f), q(0.70f, 0.40f, 0.48f, 0.40f),
                q(0.26f, 0.40f, 0.26f, 0.64f), q(0.26f, 0.88f, 0.48f, 0.88f),
                q(0.64f, 0.88f, 0.72f, 0.78f),
            ),
        ),
        'f' to listOf(
            s(0.70f, 0.20f, q(0.62f, 0.10f, 0.50f, 0.13f), q(0.40f, 0.16f, 0.40f, 0.32f), l(0.40f, 0.88f)),
            s(0.24f, 0.42f, l(0.62f, 0.42f)),
        ),
        'g' to listOf(
            lowerBowl(),
            s(0.72f, 0.40f, l(0.72f, 0.86f), q(0.72f, 0.99f, 0.52f, 0.99f), q(0.38f, 0.99f, 0.32f, 0.92f)),
        ),
        'h' to listOf(
            s(0.28f, 0.12f, l(0.28f, 0.88f)),
            s(0.28f, 0.56f, q(0.36f, 0.40f, 0.52f, 0.40f), q(0.72f, 0.40f, 0.72f, 0.60f), l(0.72f, 0.88f)),
        ),
        'i' to listOf(
            s(0.50f, 0.42f, l(0.50f, 0.88f)),
            s(0.50f, 0.20f, l(0.50f, 0.24f)),
        ),
        'j' to listOf(
            s(0.56f, 0.42f, l(0.56f, 0.86f), q(0.56f, 0.99f, 0.40f, 0.99f), q(0.28f, 0.99f, 0.24f, 0.92f)),
            s(0.56f, 0.20f, l(0.56f, 0.24f)),
        ),
        'k' to listOf(
            s(0.28f, 0.12f, l(0.28f, 0.88f)),
            s(0.70f, 0.42f, l(0.28f, 0.68f)),
            s(0.44f, 0.58f, l(0.72f, 0.88f)),
        ),
        'l' to listOf(s(0.50f, 0.12f, l(0.50f, 0.88f))),
        'm' to listOf(
            s(0.22f, 0.42f, l(0.22f, 0.88f)),
            s(0.22f, 0.54f, q(0.28f, 0.40f, 0.40f, 0.40f), q(0.51f, 0.40f, 0.51f, 0.58f), l(0.51f, 0.88f)),
            s(0.51f, 0.54f, q(0.57f, 0.40f, 0.69f, 0.40f), q(0.80f, 0.40f, 0.80f, 0.58f), l(0.80f, 0.88f)),
        ),
        'n' to listOf(
            s(0.28f, 0.42f, l(0.28f, 0.88f)),
            s(0.28f, 0.56f, q(0.34f, 0.40f, 0.50f, 0.40f), q(0.72f, 0.40f, 0.72f, 0.60f), l(0.72f, 0.88f)),
        ),
        'o' to listOf(
            s(
                0.50f, 0.40f, q(0.26f, 0.40f, 0.26f, 0.64f), q(0.26f, 0.88f, 0.50f, 0.88f),
                q(0.74f, 0.88f, 0.74f, 0.64f), q(0.74f, 0.40f, 0.50f, 0.40f),
            ),
        ),
        'p' to listOf(
            s(0.28f, 0.40f, l(0.28f, 0.99f)),
            s(
                0.28f, 0.56f, q(0.36f, 0.40f, 0.52f, 0.40f), q(0.74f, 0.40f, 0.74f, 0.64f),
                q(0.74f, 0.88f, 0.52f, 0.88f), q(0.34f, 0.88f, 0.28f, 0.76f),
            ),
        ),
        'q' to listOf(lowerBowl(), s(0.72f, 0.40f, l(0.72f, 0.99f))),
        'r' to listOf(
            s(0.30f, 0.42f, l(0.30f, 0.88f)),
            s(0.30f, 0.56f, q(0.38f, 0.40f, 0.64f, 0.40f)),
        ),
        's' to listOf(
            s(
                0.68f, 0.48f, q(0.62f, 0.40f, 0.48f, 0.40f), q(0.30f, 0.40f, 0.30f, 0.54f),
                q(0.30f, 0.64f, 0.52f, 0.66f), q(0.72f, 0.68f, 0.72f, 0.78f),
                q(0.72f, 0.88f, 0.50f, 0.88f), q(0.34f, 0.88f, 0.28f, 0.80f),
            ),
        ),
        't' to listOf(
            s(0.46f, 0.18f, l(0.46f, 0.78f), q(0.46f, 0.88f, 0.60f, 0.88f), q(0.68f, 0.88f, 0.72f, 0.83f)),
            s(0.28f, 0.42f, l(0.64f, 0.42f)),
        ),
        'u' to listOf(
            s(
                0.28f, 0.40f, l(0.28f, 0.72f), q(0.28f, 0.88f, 0.50f, 0.88f),
                q(0.70f, 0.88f, 0.70f, 0.70f), l(0.70f, 0.40f),
            ),
            s(0.70f, 0.40f, l(0.70f, 0.88f)),
        ),
        'v' to listOf(s(0.26f, 0.40f, l(0.50f, 0.88f), l(0.74f, 0.40f))),
        'w' to listOf(s(0.20f, 0.40f, l(0.36f, 0.88f), l(0.50f, 0.54f), l(0.64f, 0.88f), l(0.80f, 0.40f))),
        'x' to listOf(
            s(0.28f, 0.40f, l(0.72f, 0.88f)),
            s(0.72f, 0.40f, l(0.28f, 0.88f)),
        ),
        'y' to listOf(
            s(0.28f, 0.40f, l(0.50f, 0.84f)),
            s(0.72f, 0.40f, l(0.42f, 0.99f)),
        ),
        'z' to listOf(s(0.28f, 0.42f, l(0.72f, 0.42f), l(0.28f, 0.88f), l(0.72f, 0.88f))),
    )


    /**
     * Devanagari letters: how each one is actually written, stroke by stroke.
     *
     * Both the route and the order come from Wikimedia Commons' Devanagari stroke-order files,
     * not from guesswork. Guessing produced wrong letters several times over - क, for one, puts
     * its vertical stem down before the right hook, and its headline last, which is not what the
     * shape suggests.
     *
     * The vowels are read from the stroke-order SVGs, which are filmstrips: panel k shows the
     * letter with strokes 1..k written, a dot on the stroke that panel adds and an arrow for the
     * direction, so each stroke's shape, start and direction are all given. The consonants are
     * read from the stroke-order animations, where the pixels that darken between two frames are
     * where the pen travelled, and that route is then followed along the finished letter's
     * skeleton so it lands exactly on the letterform. Where the pen lifts is read off the same
     * animation: within a stroke, the ink a frame adds still touches the ink the frame before it
     * added; at a lift it does not, and the two are tens of pixels apart.
     *
     * A stroke that crosses one already drawn is the exception - the middle is already black, so
     * the frames add ink either side of it and the gap looks exactly like a lift. क is the case
     * in point: its loop crosses its stem, and read naively the stem came out drawn in two goes,
     * which is not how anyone writes it. Such a stem is joined back into one stroke, top to
     * bottom, and the join is kept only where it puts the guide over more of the letter.
     *
     * अं and अः have no file of their own, so they are composed: they are अ, whose strokes are
     * known, plus the mark, written last.
     *
     * The conjuncts क्ष त्र ज्ञ are absent. No stroke-order source for them exists, and they are
     * ligatures rather than their parts written in turn, so composing them would be guesswork.
     * They fall back to showing the letterform, which teaches less but teaches nothing wrong -
     * a guessed stroke order would teach a child the wrong hand.
     *
     * Points are samples along the stroke, drawn as a smooth curve through them (see
     * StrokeDemo); they are not corner points and should not be read as a polygon.
     *
     * Keyed by the full character string rather than a Char because several are more than one
     * code point.
     */
    private val DEVANAGARI: Map<String, List<GuideStroke>> = mapOf(
        "अ" to listOf(
            s(0.205f, 0.142f, l(0.367f, 0.135f), l(0.46f, 0.207f), l(0.468f, 0.318f), l(0.374f, 0.412f), l(0.457f, 0.541f), l(0.464f, 0.685f), l(0.385f, 0.75f), l(0.223f, 0.736f), l(0.104f, 0.631f), l(0.05f, 0.491f)),
            s(0.453f, 0.448f, l(0.54f, 0.491f), l(0.647f, 0.502f), l(0.719f, 0.408f)),
            s(0.769f, 0.135f, l(0.769f, 0.93f)),
            s(0.543f, 0.119f, l(0.996f, 0.119f)),
        ),
        "आ" to listOf(
            s(0.153f, 0.236f, l(0.282f, 0.228f), l(0.343f, 0.279f), l(0.349f, 0.367f), l(0.279f, 0.434f), l(0.338f, 0.523f), l(0.349f, 0.633f), l(0.287f, 0.686f), l(0.169f, 0.676f), l(0.094f, 0.619f), l(0.038f, 0.493f)),
            s(0.338f, 0.458f, l(0.48f, 0.501f), l(0.536f, 0.432f)),
            s(0.573f, 0.228f, l(0.573f, 0.82f)),
            s(0.828f, 0.228f, l(0.828f, 0.82f)),
            s(0.405f, 0.216f, l(1f, 0.216f)),
        ),
        "इ" to listOf(
            s(0.611f, 0.119f, l(0.59f, 0.246f), l(0.379f, 0.266f), l(0.324f, 0.314f), l(0.317f, 0.365f), l(0.375f, 0.481f), l(0.44f, 0.498f), l(0.56f, 0.457f), l(0.642f, 0.474f), l(0.7f, 0.526f), l(0.696f, 0.662f), l(0.618f, 0.724f), l(0.481f, 0.754f), l(0.498f, 0.826f), l(0.539f, 0.863f), l(0.625f, 0.928f), l(0.669f, 0.918f)),
            s(0.14f, 0.049f, l(0.86f, 0.049f)),
        ),
        "ई" to listOf(
            s(0.582f, 0.348f, l(0.567f, 0.439f), l(0.405f, 0.457f), l(0.364f, 0.528f), l(0.407f, 0.614f), l(0.456f, 0.627f), l(0.542f, 0.596f), l(0.648f, 0.647f), l(0.641f, 0.756f), l(0.486f, 0.817f), l(0.534f, 0.904f), l(0.593f, 0.947f), l(0.631f, 0.939f)),
            s(0.61f, 0.259f, l(0.557f, 0.19f), l(0.555f, 0.076f), l(0.613f, 0.038f), l(0.712f, 0.071f)),
            s(0.232f, 0.291f, l(0.768f, 0.291f)),
        ),
        "उ" to listOf(
            s(0.597f, 0.099f, l(0.67f, 0.138f), l(0.709f, 0.254f), l(0.692f, 0.366f), l(0.606f, 0.457f), l(0.726f, 0.634f), l(0.748f, 0.78f), l(0.722f, 0.845f), l(0.601f, 0.918f), l(0.407f, 0.884f), l(0.231f, 0.694f), l(0.179f, 0.595f), l(0.188f, 0.552f)),
            s(0.024f, 0.058f, l(0.976f, 0.058f)),
        ),
        "ऊ" to listOf(
            s(0.448f, 0.179f, l(0.505f, 0.206f), l(0.535f, 0.283f), l(0.532f, 0.37f), l(0.462f, 0.457f), l(0.555f, 0.607f), l(0.548f, 0.754f), l(0.452f, 0.814f), l(0.304f, 0.788f), l(0.207f, 0.701f), l(0.124f, 0.557f)),
            s(0.619f, 0.597f, l(0.635f, 0.537f), l(0.689f, 0.51f), l(0.799f, 0.527f), l(0.839f, 0.567f), l(0.849f, 0.717f), l(0.779f, 0.828f), l(0.729f, 0.834f)),
            s(0f, 0.148f, l(1f, 0.148f)),
        ),
        "ऋ" to listOf(
            s(0.179f, 0.408f, l(0.349f, 0.408f), l(0.45f, 0.491f), l(0.47f, 0.546f), l(0.228f, 0.69f), l(0.161f, 0.679f)),
            s(0.576f, 0.517f, l(0.741f, 0.46f), l(0.804f, 0.572f), l(0.695f, 0.661f), l(0.703f, 0.739f), l(0.741f, 0.777f), l(0.833f, 0.794f), l(0.885f, 0.777f), l(0.896f, 0.733f)),
            s(0.517f, 0.206f, l(0.517f, 0.843f)),
            s(0f, 0.196f, l(1f, 0.196f)),
        ),
        "ए" to listOf(
            s(0.316f, 0.114f, l(0.34f, 0.525f), l(0.66f, 0.771f), l(0.69f, 0.879f), l(0.65f, 0.933f)),
            s(0.675f, 0.114f, l(0.675f, 0.455f)),
            s(0.098f, 0.045f, l(0.902f, 0.045f)),
        ),
        "ऐ" to listOf(
            s(0.363f, 0.337f, l(0.381f, 0.643f), l(0.619f, 0.827f), l(0.637f, 0.935f)),
            s(0.627f, 0.337f, l(0.627f, 0.59f)),
            s(0.639f, 0.241f, l(0.524f, 0.075f), l(0.408f, 0.045f), l(0.386f, 0.083f)),
            s(0.2f, 0.286f, l(0.8f, 0.286f)),
        ),
        "ओ" to listOf(
            s(0.152f, 0.369f, l(0.27f, 0.358f), l(0.345f, 0.417f), l(0.348f, 0.5f), l(0.278f, 0.567f), l(0.337f, 0.655f), l(0.348f, 0.765f), l(0.286f, 0.818f), l(0.168f, 0.807f), l(0.094f, 0.751f), l(0.037f, 0.626f)),
            s(0.337f, 0.591f, l(0.481f, 0.634f), l(0.535f, 0.564f)),
            s(0.572f, 0.361f, l(0.572f, 0.952f)),
            s(0.826f, 0.361f, l(0.826f, 0.952f)),
            s(0.861f, 0.321f, l(0.701f, 0.123f), l(0.583f, 0.094f), l(0.556f, 0.134f)),
            s(0.404f, 0.349f, l(0.997f, 0.349f)),
        ),
        "औ" to listOf(
            s(0.158f, 0.37f, l(0.292f, 0.373f), l(0.346f, 0.426f), l(0.349f, 0.507f), l(0.279f, 0.571f), l(0.34f, 0.67f), l(0.346f, 0.78f), l(0.271f, 0.831f), l(0.164f, 0.815f), l(0.091f, 0.756f), l(0.035f, 0.643f)),
            s(0.335f, 0.598f, l(0.475f, 0.643f), l(0.534f, 0.574f)),
            s(0.573f, 0.37f, l(0.573f, 0.962f)),
            s(0.828f, 0.37f, l(0.828f, 0.962f)),
            s(0.81f, 0.316f, l(0.708f, 0.223f), l(0.619f, 0.209f), l(0.598f, 0.241f)),
            s(0.85f, 0.319f, l(0.729f, 0.105f), l(0.622f, 0.078f), l(0.603f, 0.107f)),
            s(0.405f, 0.356f, l(1f, 0.356f)),
        ),
        "अं" to listOf(
            s(0.237f, 0.307f, l(0.382f, 0.3f), l(0.464f, 0.365f), l(0.472f, 0.463f), l(0.388f, 0.547f), l(0.462f, 0.662f), l(0.468f, 0.79f), l(0.398f, 0.848f), l(0.253f, 0.835f), l(0.148f, 0.742f), l(0.099f, 0.617f)),
            s(0.458f, 0.579f, l(0.536f, 0.617f), l(0.631f, 0.627f), l(0.695f, 0.543f)),
            s(0.739f, 0.3f, l(0.739f, 1f)),
            s(0.538f, 0.286f, l(0.942f, 0.286f)),
            s(0.734f, 0.076f, l(0.734f, 0.08f)),
        ),
        "अः" to listOf(
            s(0.151f, 0.236f, l(0.271f, 0.231f), l(0.34f, 0.284f), l(0.346f, 0.366f), l(0.276f, 0.435f), l(0.337f, 0.53f), l(0.343f, 0.637f), l(0.284f, 0.685f), l(0.165f, 0.674f), l(0.077f, 0.597f), l(0.037f, 0.493f)),
            s(0.334f, 0.462f, l(0.399f, 0.493f), l(0.478f, 0.501f), l(0.531f, 0.432f)),
            s(0.568f, 0.231f, l(0.568f, 0.817f)),
            s(0.084f, 0.24f, l(1f, 0.24f)),
            s(0.822f, 0.439f, l(0.822f, 0.443f)),
            s(0.822f, 0.69f, l(0.822f, 0.694f)),
        ),
        "क" to listOf(
            s(0.442f, 0.368f, l(0.258f, 0.326f), l(0.179f, 0.363f), l(0.116f, 0.447f), l(0.142f, 0.553f), l(0.242f, 0.621f), l(0.358f, 0.632f), l(0.484f, 0.568f)),
            s(0.50725f, 0.168f, l(0.50725f, 0.863f)),
            s(0.558f, 0.437f, l(0.658f, 0.395f), l(0.784f, 0.453f), l(0.816f, 0.5f), l(0.826f, 0.621f), l(0.774f, 0.7f)),
            s(0.063f, 0.158f, l(0.011f, 0.132f), l(0.079f, 0.163f), l(0.926f, 0.163f), l(1f, 0.195f)),
        ),
        "ख" to listOf(
            s(0.3f, 0.221f, l(0.3f, 0.405f), l(0.258f, 0.532f), l(0.158f, 0.589f), l(0.1f, 0.553f), l(0.089f, 0.479f), l(0.153f, 0.432f), l(0.274f, 0.474f)),
            s(0.184f, 0.658f, l(0.158f, 0.6f), l(0.263f, 0.737f), l(0.426f, 0.816f), l(0.553f, 0.8f), l(0.684f, 0.716f)),
            s(0.705f, 0.342f, l(0.547f, 0.321f), l(0.432f, 0.416f), l(0.437f, 0.505f), l(0.511f, 0.579f), l(0.637f, 0.595f), l(0.758f, 0.547f)),
            s(0.784f, 0.168f, l(0.784f, 0.853f)),
            s(0.084f, 0.161f, l(0.968f, 0.161f)),
        ),
        "ग" to listOf(
            s(0.285f, 0.512f, l(0.2f, 0.5f), l(0.115f, 0.573f), l(0.127f, 0.645f), l(0.17f, 0.688f), l(0.303f, 0.712f), l(0.345f, 0.682f), l(0.364f, 0.124f)),
            s(0.766f, 0.197f, l(0.766f, 0.912f)),
            s(0.073f, 0.106f, l(0.012f, 0.076f), l(0.091f, 0.112f), l(0.915f, 0.112f), l(1f, 0.148f)),
        ),
        "घ" to listOf(
            s(0.15f, 0.171f, l(0.15f, 0.278f), l(0.21f, 0.344f), l(0.305f, 0.404f), l(0.395f, 0.392f), l(0.198f, 0.428f), l(0.174f, 0.458f), l(0.21f, 0.554f), l(0.359f, 0.65f), l(0.557f, 0.668f), l(0.737f, 0.584f), l(0.749f, 0.123f)),
            s(0.758f, 0.668f, l(0.758f, 0.919f)),
            s(0.072f, 0.111f, l(0.012f, 0.081f), l(0.168f, 0.129f), l(0.91f, 0.117f), l(0.976f, 0.147f)),
        ),
        "ङ" to listOf(
            s(0.62f, 0.137f, l(0.613f, 0.25f), l(0.261f, 0.264f), l(0.197f, 0.327f), l(0.197f, 0.405f), l(0.218f, 0.468f), l(0.289f, 0.539f), l(0.394f, 0.553f), l(0.556f, 0.511f), l(0.669f, 0.546f), l(0.739f, 0.658f), l(0.746f, 0.75f), l(0.718f, 0.813f), l(0.613f, 0.905f), l(0.514f, 0.94f), l(0.31f, 0.919f), l(0.148f, 0.835f)),
            s(0.092f, 0.06f, l(0.014f, 0.025f), l(0.106f, 0.067f), l(0.93f, 0.081f)),
        ),
        "च" to listOf(
            s(0.18f, 0.314f, l(0.096f, 0.278f), l(0.18f, 0.314f), l(0.425f, 0.314f), l(0.467f, 0.338f), l(0.443f, 0.332f), l(0.413f, 0.392f), l(0.269f, 0.488f), l(0.257f, 0.572f), l(0.365f, 0.668f), l(0.551f, 0.668f), l(0.725f, 0.578f)),
            s(0.7535f, 0.123f, l(0.7535f, 0.913f)),
            s(0.078f, 0.111f, l(0.012f, 0.081f), l(0.09f, 0.117f), l(0.91f, 0.117f), l(0.964f, 0.141f)),
        ),
        "छ" to listOf(
            s(0.204f, 0.174f, l(0.132f, 0.269f), l(0.156f, 0.365f), l(0.311f, 0.509f), l(0.401f, 0.503f), l(0.311f, 0.509f), l(0.21f, 0.605f), l(0.222f, 0.731f), l(0.275f, 0.796f), l(0.401f, 0.862f), l(0.575f, 0.874f), l(0.713f, 0.82f), l(0.79f, 0.743f), l(0.85f, 0.587f), l(0.832f, 0.401f), l(0.743f, 0.281f), l(0.635f, 0.275f), l(0.563f, 0.341f), l(0.581f, 0.425f), l(0.766f, 0.563f)),
            s(0.743f, 0.192f, l(0.743f, 0.275f)),
            s(0.072f, 0.115f, l(0.934f, 0.115f)),
        ),
        "ज" to listOf(
            s(0.126f, 0.494f, l(0.317f, 0.775f), l(0.467f, 0.811f), l(0.545f, 0.751f), l(0.557f, 0.668f), l(0.533f, 0.572f), l(0.413f, 0.416f), l(0.737f, 0.422f)),
            s(0.756f, 0.207f, l(0.756f, 0.901f)),
            s(0.096f, 0.109f, l(0.922f, 0.109f)),
        ),
        "झ" to listOf(
            s(0.538f, 0.898f, l(0.469f, 0.915f), l(0.341f, 0.81f), l(0.325f, 0.757f), l(0.452f, 0.721f), l(0.538f, 0.656f), l(0.534f, 0.511f), l(0.43f, 0.462f), l(0.292f, 0.498f), l(0.23f, 0.485f), l(0.17f, 0.37f), l(0.18f, 0.318f), l(0.226f, 0.279f), l(0.436f, 0.256f), l(0.452f, 0.141f)),
            s(0.754f, 0.4f, l(0.728f, 0.462f), l(0.679f, 0.489f), l(0.531f, 0.443f)),
            s(0.787f, 0.079f, l(0.787f, 0.803f)),
            s(0.003f, 0.062f, l(0.997f, 0.062f)),
        ),
        "ञ" to listOf(
            s(0.168f, 0.314f, l(0.246f, 0.278f), l(0.383f, 0.296f), l(0.473f, 0.362f), l(0.509f, 0.482f), l(0.485f, 0.59f), l(0.431f, 0.644f), l(0.335f, 0.656f), l(0.186f, 0.614f)),
            s(0.569f, 0.5f, l(0.707f, 0.524f)),
            s(0.7525f, 0.123f, l(0.7525f, 0.895f)),
            s(0.102f, 0.117f, l(0.012f, 0.081f), l(0.102f, 0.117f), l(0.91f, 0.117f), l(0.964f, 0.141f)),
        ),
        "ट" to listOf(
            s(0.62f, 0.155f, l(0.613f, 0.296f), l(0.394f, 0.366f), l(0.204f, 0.5f), l(0.155f, 0.599f), l(0.169f, 0.704f), l(0.275f, 0.831f), l(0.458f, 0.923f), l(0.669f, 0.93f), l(0.803f, 0.817f)),
            s(0.07f, 0.049f, l(0.014f, 0.021f), l(0.106f, 0.063f), l(0.894f, 0.063f), l(1f, 0.106f)),
        ),
        "ठ" to listOf(
            s(0.629f, 0.143f, l(0.629f, 0.293f), l(0.607f, 0.314f), l(0.386f, 0.379f), l(0.193f, 0.536f), l(0.157f, 0.6f), l(0.171f, 0.743f), l(0.279f, 0.864f), l(0.4f, 0.921f), l(0.636f, 0.936f), l(0.793f, 0.879f), l(0.886f, 0.771f), l(0.893f, 0.636f), l(0.843f, 0.5f), l(0.714f, 0.371f)),
            s(0.093f, 0.043f, l(0.014f, 0.014f), l(0.093f, 0.043f), l(0.914f, 0.043f), l(0.993f, 0.079f)),
        ),
        "ड" to listOf(
            s(0.629f, 0.125f, l(0.621f, 0.246f), l(0.264f, 0.261f), l(0.2f, 0.325f), l(0.2f, 0.404f), l(0.221f, 0.468f), l(0.293f, 0.539f), l(0.4f, 0.554f), l(0.529f, 0.511f), l(0.671f, 0.539f), l(0.75f, 0.654f), l(0.757f, 0.754f), l(0.729f, 0.818f), l(0.643f, 0.896f), l(0.521f, 0.946f), l(0.314f, 0.925f), l(0.136f, 0.832f)),
            s(0.093f, 0.043f, l(0.943f, 0.043f)),
        ),
        "ढ" to listOf(
            s(0.629f, 0.129f, l(0.629f, 0.286f), l(0.321f, 0.421f), l(0.186f, 0.55f), l(0.157f, 0.614f), l(0.164f, 0.736f), l(0.221f, 0.821f), l(0.364f, 0.907f), l(0.5f, 0.936f), l(0.729f, 0.921f), l(0.814f, 0.879f), l(0.85f, 0.807f), l(0.793f, 0.671f), l(0.686f, 0.643f), l(0.65f, 0.664f), l(0.586f, 0.736f), l(0.614f, 0.836f)),
            s(0.071f, 0.041f, l(0.957f, 0.041f)),
        ),
        "ण" to listOf(
            s(0.176f, 0.179f, l(0.176f, 0.439f), l(0.206f, 0.567f), l(0.285f, 0.627f), l(0.412f, 0.639f), l(0.515f, 0.567f), l(0.545f, 0.5f), l(0.545f, 0.106f)),
            s(0.768f, 0.197f, l(0.768f, 0.918f)),
            s(0.067f, 0.101f, l(0.994f, 0.101f)),
        ),
        "त" to listOf(
            s(0.613f, 0.408f, l(0.275f, 0.415f), l(0.155f, 0.507f), l(0.141f, 0.57f), l(0.176f, 0.739f), l(0.303f, 0.923f)),
            s(0.714f, 0.141f, l(0.714f, 0.979f)),
            s(0.106f, 0.049f, l(0.014f, 0.007f), l(0.106f, 0.049f), l(0.901f, 0.049f), l(0.979f, 0.085f)),
        ),
        "थ" to listOf(
            s(0.356f, 0.242f, l(0.356f, 0.168f), l(0.349f, 0.255f), l(0.141f, 0.228f), l(0.081f, 0.174f), l(0.074f, 0.094f), l(0.148f, 0.04f), l(0.268f, 0.054f), l(0.342f, 0.121f), l(0.362f, 0.201f), l(0.336f, 0.369f), l(0.235f, 0.45f), l(0.101f, 0.456f), l(0.101f, 0.517f), l(0.154f, 0.584f), l(0.302f, 0.671f), l(0.503f, 0.678f), l(0.705f, 0.577f), l(0.718f, 0.128f)),
            s(0.728f, 0.658f, l(0.728f, 0.966f)),
            s(0.584f, 0.105f, l(0.98f, 0.105f)),
        ),
        "द" to listOf(
            s(0.618f, 0.118f, l(0.604f, 0.271f), l(0.403f, 0.271f), l(0.292f, 0.306f), l(0.201f, 0.396f), l(0.167f, 0.5f), l(0.229f, 0.653f), l(0.354f, 0.722f), l(0.444f, 0.736f), l(0.562f, 0.736f), l(0.646f, 0.688f), l(0.646f, 0.604f), l(0.597f, 0.535f), l(0.5f, 0.521f), l(0.465f, 0.556f), l(0.479f, 0.639f), l(0.514f, 0.729f), l(0.576f, 0.743f), l(0.583f, 0.792f), l(0.708f, 0.972f)),
            s(0.118f, 0.042f, l(0.021f, 0f), l(0.118f, 0.042f), l(0.889f, 0.042f), l(0.993f, 0.083f)),
        ),
        "ध" to listOf(
            s(0.191f, 0.191f, l(0.242f, 0.236f), l(0.312f, 0.248f), l(0.389f, 0.166f), l(0.382f, 0.089f), l(0.306f, 0.045f), l(0.204f, 0.057f), l(0.076f, 0.191f), l(0.064f, 0.274f), l(0.096f, 0.35f), l(0.197f, 0.452f), l(0.465f, 0.471f)),
            s(0.153f, 0.465f, l(0.108f, 0.503f), l(0.127f, 0.618f), l(0.293f, 0.752f), l(0.497f, 0.771f), l(0.675f, 0.701f), l(0.707f, 0.631f), l(0.707f, 0.159f)),
            s(0.718f, 0.764f, l(0.718f, 1f)),
            s(0.611f, 0.14f, l(0.548f, 0.108f), l(0.624f, 0.146f), l(0.885f, 0.146f), l(0.975f, 0.185f)),
        ),
        "न" to listOf(
            s(0.371f, 0.524f, l(0.383f, 0.59f), l(0.323f, 0.668f), l(0.228f, 0.638f), l(0.174f, 0.488f), l(0.24f, 0.422f), l(0.737f, 0.422f)),
            s(0.758f, 0.189f, l(0.758f, 0.913f)),
            s(0.078f, 0.111f, l(0.012f, 0.081f), l(0.09f, 0.117f), l(0.91f, 0.117f), l(0.964f, 0.141f)),
        ),
        "प" to listOf(
            s(0.236f, 0.114f, l(0.257f, 0.55f), l(0.343f, 0.636f), l(0.5f, 0.657f), l(0.7f, 0.557f), l(0.714f, 0.093f)),
            s(0.724f, 0.614f, l(0.724f, 0.979f)),
            s(0.057f, 0.024f, l(0.964f, 0.024f)),
        ),
        "फ" to listOf(
            s(0.12f, 0.275f, l(0.129f, 0.502f), l(0.21f, 0.622f), l(0.283f, 0.635f), l(0.464f, 0.584f), l(0.476f, 0.373f), l(0.476f, 0.893f)),
            s(0.579f, 0.412f, l(0.494f, 0.433f), l(0.665f, 0.399f), l(0.794f, 0.459f), l(0.828f, 0.622f), l(0.79f, 0.725f)),
            s(0.129f, 0.155f, l(0.983f, 0.155f)),
        ),
        "ब" to listOf(
            s(0.699f, 0.519f, l(0.699f, 0.667f), l(0.689f, 0.41f), l(0.372f, 0.344f), l(0.273f, 0.366f), l(0.24f, 0.421f), l(0.153f, 0.47f), l(0.126f, 0.53f), l(0.158f, 0.678f), l(0.29f, 0.77f), l(0.492f, 0.765f), l(0.541f, 0.705f), l(0.689f, 0.678f)),
            s(0.699f, 0.268f, l(0.699f, 0.945f)),
            s(0.525f, 0.71f, l(0.246f, 0.421f)),
            s(0.158f, 0.06f, l(0.989f, 0.06f)),
        ),
        "भ" to listOf(
            s(0.191f, 0.311f, l(0.33f, 0.297f), l(0.187f, 0.316f), l(0.11f, 0.297f), l(0.062f, 0.22f), l(0.077f, 0.134f), l(0.124f, 0.086f), l(0.22f, 0.072f), l(0.273f, 0.081f), l(0.325f, 0.153f), l(0.34f, 0.732f), l(0.325f, 0.789f), l(0.268f, 0.842f), l(0.124f, 0.842f), l(0.072f, 0.804f), l(0.048f, 0.727f), l(0.067f, 0.646f), l(0.115f, 0.603f), l(0.703f, 0.584f)),
            s(0.737f, 0.335f, l(0.737f, 0.986f)),
            s(0.598f, 0.163f, l(0.995f, 0.163f)),
        ),
        "म" to listOf(
            s(0.317f, 0.267f, l(0.312f, 0.688f), l(0.262f, 0.756f), l(0.204f, 0.769f), l(0.104f, 0.719f), l(0.104f, 0.597f), l(0.149f, 0.552f), l(0.742f, 0.534f), l(0.751f, 0.33f), l(0.751f, 0.914f)),
            s(0.163f, 0.136f, l(0.995f, 0.136f)),
        ),
        "य" to listOf(
            s(0.329f, 0.201f, l(0.341f, 0.356f), l(0.281f, 0.428f), l(0.18f, 0.446f), l(0.18f, 0.5f), l(0.323f, 0.62f), l(0.503f, 0.644f), l(0.737f, 0.536f), l(0.749f, 0.123f)),
            s(0.756f, 0.602f, l(0.756f, 0.895f)),
            s(0.09f, 0.112f, l(0.952f, 0.112f)),
        ),
        "र" to listOf(
            s(0.545f, 0.123f, l(0.545f, 0.452f), l(0.497f, 0.568f), l(0.408f, 0.637f), l(0.339f, 0.616f), l(0.284f, 0.568f), l(0.271f, 0.479f), l(0.339f, 0.432f), l(0.469f, 0.466f)),
            s(0.414f, 0.705f, l(0.394f, 0.651f), l(0.531f, 0.842f)),
            s(0.161f, 0.022f, l(0.853f, 0.022f)),
        ),
        "ल" to listOf(
            s(0.317f, 0.865f, l(0.15f, 0.662f), l(0.126f, 0.554f), l(0.144f, 0.506f), l(0.251f, 0.422f), l(0.371f, 0.428f), l(0.449f, 0.482f), l(0.455f, 0.59f), l(0.737f, 0.44f)),
            s(0.75f, 0.183f, l(0.75f, 0.871f)),
            s(0.078f, 0.105f, l(0.946f, 0.105f)),
        ),
        "व" to listOf(
            s(0.59f, 0.388f, l(0.399f, 0.344f), l(0.273f, 0.366f), l(0.158f, 0.459f), l(0.137f, 0.607f), l(0.175f, 0.689f), l(0.29f, 0.77f), l(0.481f, 0.77f), l(0.689f, 0.699f), l(0.699f, 0.301f), l(0.699f, 1f)),
            s(0.109f, 0.06f, l(0.989f, 0.06f)),
        ),
        "श" to listOf(
            s(0.278f, 0.285f, l(0.166f, 0.272f), l(0.106f, 0.232f), l(0.079f, 0.172f), l(0.093f, 0.099f), l(0.152f, 0.053f), l(0.199f, 0.053f), l(0.318f, 0.113f), l(0.384f, 0.272f), l(0.391f, 0.47f), l(0.338f, 0.589f), l(0.238f, 0.675f), l(0.106f, 0.669f), l(0.046f, 0.609f), l(0.06f, 0.517f), l(0.146f, 0.536f), l(0.483f, 0.927f)),
            s(0.731f, 0.192f, l(0.731f, 0.974f)),
            s(0.53f, 0.099f, l(0.464f, 0.066f), l(0.55f, 0.106f), l(0.907f, 0.106f), l(0.993f, 0.146f)),
        ),
        "ष" to listOf(
            s(0.197f, 0.134f, l(0.218f, 0.556f), l(0.296f, 0.634f), l(0.458f, 0.655f), l(0.69f, 0.528f), l(0.704f, 0.056f)),
            s(0.714f, 0.57f, l(0.714f, 0.965f)),
            s(0.289f, 0.162f, l(0.577f, 0.486f)),
            s(0.07f, 0.035f, l(0.014f, 0.007f), l(0.106f, 0.049f), l(0.894f, 0.049f), l(0.937f, 0.07f)),
        ),
        "स" to listOf(
            s(0.339f, 0.233f, l(0.335f, 0.495f), l(0.271f, 0.586f), l(0.217f, 0.613f), l(0.167f, 0.595f), l(0.217f, 0.618f), l(0.235f, 0.681f), l(0.376f, 0.844f)),
            s(0.434f, 0.5f, l(0.724f, 0.5f)),
            s(0.751f, 0.265f, l(0.751f, 0.88f)),
            s(0.181f, 0.102f, l(0.982f, 0.102f)),
        ),
        "ह" to listOf(
            s(0.62f, 0.123f, l(0.613f, 0.243f), l(0.317f, 0.243f), l(0.254f, 0.264f), l(0.211f, 0.32f), l(0.232f, 0.426f), l(0.289f, 0.496f), l(0.57f, 0.454f), l(0.69f, 0.525f), l(0.718f, 0.658f), l(0.669f, 0.722f), l(0.592f, 0.75f)),
            s(0.261f, 0.532f, l(0.211f, 0.588f), l(0.225f, 0.68f), l(0.458f, 0.947f)),
            s(0.106f, 0.046f, l(0.014f, 0.004f), l(0.106f, 0.046f), l(0.93f, 0.06f)),
        ),
    )

    private val DIGITS: Map<Char, List<GuideStroke>> = mapOf(
        '0' to listOf(
            s(
                0.50f, 0.12f, q(0.24f, 0.12f, 0.24f, 0.50f), q(0.24f, 0.88f, 0.50f, 0.88f),
                q(0.76f, 0.88f, 0.76f, 0.50f), q(0.76f, 0.12f, 0.50f, 0.12f),
            ),
        ),
        '1' to listOf(s(0.34f, 0.26f, l(0.52f, 0.12f), l(0.52f, 0.88f))),
        '2' to listOf(
            s(
                0.26f, 0.28f, q(0.28f, 0.12f, 0.50f, 0.12f), q(0.74f, 0.12f, 0.74f, 0.32f),
                q(0.74f, 0.52f, 0.26f, 0.88f), l(0.78f, 0.88f),
            ),
        ),
        '3' to listOf(
            s(
                0.26f, 0.22f, q(0.32f, 0.12f, 0.52f, 0.12f), q(0.74f, 0.12f, 0.74f, 0.30f),
                q(0.74f, 0.46f, 0.50f, 0.48f), q(0.78f, 0.50f, 0.78f, 0.68f),
                q(0.78f, 0.88f, 0.50f, 0.88f), q(0.30f, 0.88f, 0.24f, 0.76f),
            ),
        ),
        '4' to listOf(
            s(0.62f, 0.12f, l(0.22f, 0.62f), l(0.80f, 0.62f)),
            s(0.62f, 0.12f, l(0.62f, 0.88f)),
        ),
        '5' to listOf(
            s(
                0.74f, 0.12f, l(0.30f, 0.12f), l(0.28f, 0.44f), q(0.50f, 0.36f, 0.62f, 0.44f),
                q(0.78f, 0.52f, 0.77f, 0.68f), q(0.76f, 0.88f, 0.48f, 0.88f),
                q(0.30f, 0.88f, 0.24f, 0.78f),
            ),
        ),
        '6' to listOf(
            s(
                0.72f, 0.14f, q(0.30f, 0.20f, 0.28f, 0.58f), q(0.26f, 0.88f, 0.52f, 0.88f),
                q(0.76f, 0.88f, 0.76f, 0.68f), q(0.76f, 0.48f, 0.52f, 0.48f),
                q(0.34f, 0.48f, 0.28f, 0.60f),
            ),
        ),
        '7' to listOf(s(0.22f, 0.12f, l(0.78f, 0.12f), l(0.42f, 0.88f))),
        '8' to listOf(
            s(
                0.50f, 0.12f, q(0.28f, 0.12f, 0.28f, 0.28f), q(0.28f, 0.44f, 0.50f, 0.50f),
                q(0.76f, 0.57f, 0.76f, 0.72f), q(0.76f, 0.88f, 0.50f, 0.88f),
                q(0.24f, 0.88f, 0.24f, 0.72f), q(0.24f, 0.57f, 0.50f, 0.50f),
                q(0.72f, 0.44f, 0.72f, 0.28f), q(0.72f, 0.12f, 0.50f, 0.12f),
            ),
        ),
        '9' to listOf(
            s(
                0.74f, 0.36f, q(0.74f, 0.12f, 0.50f, 0.12f), q(0.26f, 0.12f, 0.26f, 0.34f),
                q(0.26f, 0.56f, 0.50f, 0.56f), q(0.68f, 0.56f, 0.74f, 0.44f),
                l(0.74f, 0.36f), l(0.66f, 0.88f),
            ),
        ),
    )
}
