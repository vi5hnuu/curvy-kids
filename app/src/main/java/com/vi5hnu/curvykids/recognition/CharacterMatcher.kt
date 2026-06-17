package com.vi5hnu.curvykids.recognition

import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.data.content.Script

/**
 * Decides whether the recognizer's candidate list counts as a correct match for the
 * character the child was asked to draw.
 *
 * This is intentionally lenient — it's a learning app for young children, so frustration is
 * worse than the occasional generous pass:
 *  - **Letters**: a match if the expected letter appears anywhere in the candidates,
 *    ignoring case. Handwriting recognizers routinely return the opposite case (a drawn "A"
 *    comes back as "a"), and to a child 'a' and 'A' are "the same letter".
 *  - **Numbers**: stricter — the recognizer's best guess (top candidate) must be exact, since
 *    digits are visually distinct and there's no case ambiguity.
 */
object CharacterMatcher {

    fun matches(expected: String, candidates: List<String>, level: Level): Boolean {
        if (candidates.isEmpty()) return false

        return when {
            // Devanagari: the `hi` model often returns the letter joined with a matra or as part
            // of a word (e.g. "का" or "कमल" for क). Accept it if the target letter appears in any
            // candidate — generous on purpose, matching the lenient spirit for young learners.
            level.script == Script.DEVANAGARI ->
                candidates.any { it.trim() == expected || it.contains(expected) }

            // Numbers: stricter — the recognizer's best guess must be exact (digits are distinct).
            level == Level.NUMBERS ->
                candidates.first().trim() == expected

            // Latin letters: case-insensitive presence anywhere in the candidate list.
            else ->
                candidates.any { it.trim().equals(expected, ignoreCase = true) }
        }
    }
}
