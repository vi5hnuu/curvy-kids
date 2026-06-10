package com.vi5hnu.curvykids.recognition

import com.vi5hnu.curvykids.data.content.Level
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterMatcherTest {

    @Test
    fun `letter matches when present anywhere in candidates`() {
        assertTrue(CharacterMatcher.matches("A", listOf("A", "H"), Level.UPPERCASE))
        assertTrue(CharacterMatcher.matches("A", listOf("H", "A"), Level.UPPERCASE))
    }

    @Test
    fun `letter match ignores case`() {
        // Recognizer commonly returns the other case; to a child it's the same letter.
        assertTrue(CharacterMatcher.matches("A", listOf("a"), Level.UPPERCASE))
        assertTrue(CharacterMatcher.matches("c", listOf("C"), Level.LOWERCASE))
    }

    @Test
    fun `letter does not match a different letter`() {
        assertFalse(CharacterMatcher.matches("A", listOf("H", "M"), Level.UPPERCASE))
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
