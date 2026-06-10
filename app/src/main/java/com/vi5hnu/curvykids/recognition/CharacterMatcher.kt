package com.vi5hnu.curvykids.recognition

import com.vi5hnu.curvykids.data.content.Level

/**
 * Decides whether the recognizer's candidate list counts as a correct match for the
 * character the child was asked to draw.
 *
 * This ports (and generalises) the rule the old web app used: some letters have nearly
 * identical upper- and lower-case glyphs (c, k, o, p, s, u, v, w, x, z). For those the
 * recognizer may legitimately return the other case, so we accept a match if the expected
 * letter appears anywhere in the candidates (case-insensitive). For every other letter we
 * require an exact, case-sensitive top-1 match so 'a' is not accepted for 'A'. Numbers
 * always require an exact top-1 match.
 */
object CharacterMatcher {

    /** Letters whose upper- and lower-case forms are visually ambiguous. */
    private val AMBIGUOUS_LETTERS = setOf('c', 'k', 'o', 'p', 's', 'u', 'v', 'w', 'x', 'z')

    fun matches(expected: String, candidates: List<String>, level: Level): Boolean {
        if (candidates.isEmpty()) return false

        if (level == Level.NUMBERS) {
            return candidates.first() == expected
        }

        val isAmbiguous = expected.singleOrNull()?.lowercaseChar() in AMBIGUOUS_LETTERS
        return if (isAmbiguous) {
            candidates.any { it.equals(expected, ignoreCase = true) }
        } else {
            candidates.first() == expected
        }
    }
}
