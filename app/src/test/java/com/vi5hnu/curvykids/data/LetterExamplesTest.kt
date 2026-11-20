package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HINDI_VOWELS
import com.vi5hnu.curvykids.data.content.Phonics
import com.vi5hnu.curvykids.data.content.letterExample
import com.vi5hnu.curvykids.data.content.syllableSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** The letter screens show, mark and say one example word per letter. */
class LetterExamplesTest {

    @Test
    fun `every english letter has a picture and its letter is marked`() {
        ('A'..'Z').forEach { c ->
            val ex = assertNotNull(letterExample(c.toString())).let { letterExample(c.toString())!! }
            assertTrue("$c: missing ${ex.picture.path}", File("src/main/assets/${ex.picture.path}").isFile)
            assertTrue("$c not marked in ${ex.word}", ex.word.substring(ex.mark).equals(c.toString(), ignoreCase = true))
        }
    }

    @Test
    fun `every hindi letter with a word has a picture and is marked`() {
        (HINDI_VOWELS + HINDI_CONSONANTS).filter { it.example.isNotEmpty() }.forEach { l ->
            val ex = letterExample(l.char)
            assertNotNull("${l.char} has no example", ex)
            // A vowel inside a word is written as its sign (अः in छः is ः).
            val marked = ex!!.word.substring(ex.mark)
            assertTrue("${l.char} not marked in ${ex.word}", marked.startsWith(l.char) || marked.startsWith(l.char.drop(1)))
            assertTrue(File("src/main/assets/${ex.picture.path}").isFile)
        }
    }

    @Test
    fun `speech and picture come from the same word`() {
        assertEquals("J for Jellyfish", Phonics.phraseFor("J"))
        assertEquals("x, as in Fox", Phonics.phraseFor("x"))
        assertEquals("क", Phonics.phraseFor("क")) // Hindi has its own recorded line
    }

    @Test
    fun `marks never split a syllable`() {
        assertEquals("गा", "गाय".substring(syllableSpan("गाय", 0, 1)))
        assertEquals("ङ्गा", "गङ्गा".substring(syllableSpan("गङ्गा", 1, 1)))
        assertEquals("त्रि", "त्रिशूल".substring(syllableSpan("त्रिशूल", 0, 3)))
        assertEquals("ण", "बाण".substring(syllableSpan("बाण", 2, 1)))
    }
}
