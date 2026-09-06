package com.vi5hnu.curvykids.data.content

/**
 * Stroke order for the barakhadi syllables — a consonant with a vowel sign (क का को …).
 *
 * Composed, not tabulated, and the signs are not invented. Wikimedia's stroke-order files cover
 * letters only, but several of the vowel letters *are* a letter plus a sign, so the sign can be
 * read out of the difference between two letters that are both already verified:
 *
 *     आ is अ with ा      →  the stroke आ has that अ lacks is the ा sign
 *     ओ is आ with े      →  likewise for े
 *     औ is ओ with a second mark
 *     अं and अः are अ with their dots
 *
 * That gives the sign's shape, its direction, and where it falls in the order — all from the
 * source. It corrected a guess, too: the े mark is drawn up and to the *left*, which is the
 * opposite of what it looks like it should be.
 *
 * ि ी ु and ू have no such parent letter to be read out of, so they are absent and those forms
 * fall back to showing the syllable's shape. Guessing them would teach a child the wrong hand.
 *
 * Signs are described against the headline: x as a fraction of the syllable's width, y as an
 * offset from the headline, which is how they were measured and how they compose onto any
 * consonant.
 */
internal object BarakhadiStrokes {

    /**
     * Where the headline sits when nothing has to fit above it. Signs like े and ौ reach over
     * the top of the letter, so those push the headline down to make room - see [headlineFor].
     */
    private const val BASE_HEADLINE_Y = 0.16f

    /** A point of a sign: across the syllable, and above (negative) or below the headline. */
    private class P(val x: Float, val dy: Float)

    private class Sign(
        val mark: String,
        /** The consonant's share of the width; a sign to the right leaves it less room. */
        val consonantWidth: Float,
        val strokes: List<List<P>>,
    )

    /** ा — a bar down the right, measured from आ. */
    private val AA = listOf(P(0.711f, 0.012f), P(0.711f, 0.604f))

    /** े — the mark above the headline, measured from ओ. Drawn up and to the left. */
    private val E = listOf(
        P(0.771f, -0.028f), P(0.501f, -0.226f), P(0.302f, -0.255f), P(0.256f, -0.215f),
    )

    /** The second mark of ै, measured from औ. */
    private val AI2 = listOf(
        P(0.748f, -0.037f), P(0.545f, -0.251f), P(0.365f, -0.278f), P(0.333f, -0.249f),
    )

    /** ं — the anusvara dot, measured from अं. */
    private val AM = listOf(P(0.485f, -0.210f), P(0.485f, -0.206f))

    /** ः — the two visarga dots, measured from अः. */
    private val AH_1 = listOf(P(0.806f, 0.199f), P(0.806f, 0.203f))
    private val AH_2 = listOf(P(0.806f, 0.450f), P(0.806f, 0.454f))

    private val SIGNS = listOf(
        Sign("ा", 0.70f, listOf(AA)),
        Sign("े", 0.94f, listOf(E)),
        Sign("ै", 0.94f, listOf(E, AI2)),
        Sign("ो", 0.70f, listOf(AA, E)),
        Sign("ौ", 0.70f, listOf(AA, E, AI2)),
        Sign("ं", 0.94f, listOf(AM)),
        Sign("ः", 0.78f, listOf(AH_1, AH_2)),
    )

    private val BY_MARK = SIGNS.associateBy { it.mark }

    /** Longest first, so ौ is matched before the ो inside it. */
    private val MARKS = SIGNS.map { it.mark }.sortedByDescending { it.length }

    /**
     * The strokes for a barakhadi syllable, or null when it is not one, when its consonant has
     * no guide, or when its sign is one of the four that cannot be sourced.
     */
    fun forSyllable(text: String, guideOf: (String) -> List<GuideStroke>?): List<GuideStroke>? {
        val mark = MARKS.firstOrNull { text.length > it.length && text.endsWith(it) } ?: return null
        val guide = guideOf(text.dropLast(mark.length)) ?: return null
        val sign = BY_MARK[mark] ?: return null

        val headlineY = headlineFor(sign)
        val ownHeadline = guide.firstOrNull { it.isHeadline() }?.startY ?: BASE_HEADLINE_Y
        val depth = (guide.flatMap { it.ys() }.maxOrNull() ?: 1f) - ownHeadline

        // The consonant is scaled to fit both the room the sign leaves it across, and the room
        // left below the headline once the sign has taken what it needs above. Then it is
        // dropped so its own headline lands on the syllable's - scaling alone would lift the
        // letter away from the bar it is supposed to hang from.
        val k = minOf(sign.consonantWidth, if (depth > 0f) (0.99f - headlineY) / depth else 1f)
        val drop = headlineY - ownHeadline * k
        val body = guide
            .filterNot { it.isHeadline() }
            .map { it.scaledBy(k, drop) }

        val marks = sign.strokes.map { pts ->
            GuideStroke(
                pts.first().x, headlineY + pts.first().dy,
                pts.drop(1).map { GuideSeg.Line(it.x, headlineY + it.dy) },
            )
        }
        // The headline runs across the whole syllable and is written last.
        val headline = GuideStroke(0.04f, headlineY, listOf(GuideSeg.Line(0.98f, headlineY)))
        return fitToBox(body + marks + headline)
    }

    /**
     * Re-centres a composed syllable to fill the unit box, keeping its proportions.
     *
     * Composition leaves the strokes wherever the parts happened to land - a sign that needs
     * room above pushes everything down, a narrow sign leaves the right-hand side empty - so
     * without this every syllable is drawn at a different size and sits off-centre. The letters
     * are framed this way too, so the two look like one alphabet.
     */
    private fun fitToBox(strokes: List<GuideStroke>): List<GuideStroke> {
        val xs = strokes.flatMap { it.xs() }
        val ys = strokes.flatMap { it.ys() }
        val w = xs.max() - xs.min()
        val h = ys.max() - ys.min()
        val span = maxOf(w, h)
        if (span <= 0f) return strokes
        val k = 1f / span
        val ox = (1f - w * k) / 2f - xs.min() * k
        val oy = (1f - h * k) / 2f - ys.min() * k
        // Clamped: the arithmetic can land a hair outside the box, and the guide's contract is
        // that every point is inside it.
        fun fx(v: Float) = (v * k + ox).coerceIn(0f, 1f)
        fun fy(v: Float) = (v * k + oy).coerceIn(0f, 1f)
        return strokes.map { st ->
            GuideStroke(
                fx(st.startX), fy(st.startY),
                st.segments.map { seg ->
                    when (seg) {
                        is GuideSeg.Line -> GuideSeg.Line(fx(seg.x), fy(seg.y))
                        is GuideSeg.Quad ->
                            GuideSeg.Quad(fx(seg.cx), fy(seg.cy), fx(seg.x), fy(seg.y))
                    }
                },
            )
        }
    }

    /** Every x this stroke visits. */
    private fun GuideStroke.xs(): List<Float> =
        listOf(startX) + segments.map { if (it is GuideSeg.Line) it.x else (it as GuideSeg.Quad).x }

    /** How far down the headline has to sit for this sign's marks to fit above it. */
    private fun headlineFor(sign: Sign): Float {
        val above = sign.strokes.flatten().minOfOrNull { it.dy } ?: 0f
        return maxOf(BASE_HEADLINE_Y, -above + 0.03f)
    }

    /** Every y this stroke visits. */
    private fun GuideStroke.ys(): List<Float> =
        listOf(startY) + segments.map { if (it is GuideSeg.Line) it.y else (it as GuideSeg.Quad).y }

    /** Shrinks a stroke about the top-left corner and drops it by [dy], keeping its proportions. */
    private fun GuideStroke.scaledBy(k: Float, dy: Float): GuideStroke =
        GuideStroke(
            startX * k, startY * k + dy,
            segments.map { seg ->
                when (seg) {
                    is GuideSeg.Line -> GuideSeg.Line(seg.x * k, seg.y * k + dy)
                    is GuideSeg.Quad ->
                        GuideSeg.Quad(seg.cx * k, seg.cy * k + dy, seg.x * k, seg.y * k + dy)
                }
            },
        )

    /** The consonant's own headline, which the syllable's replaces. */
    private fun GuideStroke.isHeadline(): Boolean {
        val pts = listOf(startX to startY) + segments.map {
            if (it is GuideSeg.Line) it.x to it.y else (it as GuideSeg.Quad).x to it.y
        }
        val xs = pts.map { it.first }
        val ys = pts.map { it.second }
        return xs.max() - xs.min() > 0.30f && ys.average() < BASE_HEADLINE_Y + 0.14f
    }
}
