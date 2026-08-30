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
 * Devanagari is deliberately absent. Its stroke order (and the shirorekha headline convention)
 * differs enough that guessing at it would teach children the wrong thing; those characters fall
 * back to the plain reveal animation until the data is authored properly.
 */
object StrokeGuide {

    /** The strokes for [character], or null when no guide has been authored for it. */
    fun forCharacter(character: String): List<GuideStroke>? {
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
