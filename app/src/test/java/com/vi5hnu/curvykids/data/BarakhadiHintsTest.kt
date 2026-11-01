package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HINDI_WORD_PICTURES
import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.SyllableHint
import com.vi5hnu.curvykids.data.content.barakhadiHint
import com.vi5hnu.curvykids.data.content.barakhadiLine
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/**
 * The barakhadi screen explains each syllable with what its recording says. These keep the
 * explanations complete and in step with the recordings.
 */
class BarakhadiHintsTest {

    private val syllables = HINDI_CONSONANTS.flatMap { c -> BARAKHADI_MATRAS.map { c to it } }

    @Test
    fun `every consonant and matra has a hint`() {
        val missing = syllables.map { (c, m) -> barakhadiSyllable(c.char, m) }.filter { barakhadiHint(it) == null }
        assertTrue("No hint for: $missing", missing.isEmpty())
    }

    @Test
    fun `every example word contains its syllable`() {
        syllables.forEach { (c, m) ->
            val s = barakhadiSyllable(c.char, m)
            when (val h = barakhadiHint(s)) {
                is SyllableHint.Word -> assertTrue("$s is not the start of ${h.word}", h.word.startsWith(s))
                is SyllableHint.AsIn -> assertTrue("$s is not in ${h.word}", s in h.word)
                is SyllableHint.Joined -> assertEquals(c.char, h.consonant)
                null -> Unit
            }
        }
    }

    @Test
    fun `the device voice reads the recorded line`() {
        syllables.forEach { (c, m) ->
            assertEquals(barakhadiLine(barakhadiSyllable(c.char, m)), HindiVoiceScript.syllable(c, m).text)
        }
    }

    /** Only where the narration list is checked out (it is kept out of the repository). */
    @Test
    fun `lines match the narration list`() {
        val list = File("../generate.txt")
        assumeTrue(list.exists())
        val expected = list.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        val actual = syllables.map { (c, m) -> barakhadiLine(barakhadiSyllable(c.char, m)) }
        assertEquals(expected, actual)
    }

    @Test
    fun `word pictures exist and belong to example words`() {
        val words = syllables.mapNotNull { (c, m) ->
            when (val h = barakhadiHint(barakhadiSyllable(c.char, m))) {
                is SyllableHint.Word -> h.word
                is SyllableHint.AsIn -> h.word
                else -> null
            }
        }.toSet()
        HINDI_WORD_PICTURES.forEach { (word, asset) ->
            assertTrue("$word is not a barakhadi example word", word in words)
            assertTrue("$word: missing ${asset.path}", File("src/main/assets/${asset.path}").isFile)
        }
    }
}
