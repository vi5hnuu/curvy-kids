package com.vi5hnu.curvykids.recognition

import com.vi5hnu.curvykids.data.content.Level
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterMatcherTest {

    @Test
    fun `non-ambiguous letter requires exact top-1 match`() {
        assertTrue(CharacterMatcher.matches("A", listOf("A", "H"), Level.UPPERCASE))
        // 'A' only present as a lower-ranked candidate -> not a match (A is not ambiguous).
        assertFalse(CharacterMatcher.matches("A", listOf("H", "A"), Level.UPPERCASE))
    }

    @Test
    fun `ambiguous letter accepted anywhere in candidates ignoring case`() {
        // 'C' has near-identical upper/lower glyphs, so accept it even if not top-1...
        assertTrue(CharacterMatcher.matches("C", listOf("O", "c"), Level.UPPERCASE))
        // ...and a lowercase 'c' target accepts an uppercase 'C' candidate.
        assertTrue(CharacterMatcher.matches("c", listOf("C"), Level.LOWERCASE))
    }

    @Test
    fun `lowercase non-ambiguous is case sensitive`() {
        // 'a' and 'A' are distinct shapes -> uppercase candidate must not satisfy 'a'.
        assertFalse(CharacterMatcher.matches("a", listOf("A"), Level.LOWERCASE))
        assertTrue(CharacterMatcher.matches("a", listOf("a"), Level.LOWERCASE))
    }

    @Test
    fun `numbers require exact top-1 match`() {
        assertTrue(CharacterMatcher.matches("5", listOf("5"), Level.NUMBERS))
        assertFalse(CharacterMatcher.matches("5", listOf("8", "5"), Level.NUMBERS))
    }

    @Test
    fun `empty candidates never match`() {
        assertFalse(CharacterMatcher.matches("A", emptyList(), Level.UPPERCASE))
    }
}
