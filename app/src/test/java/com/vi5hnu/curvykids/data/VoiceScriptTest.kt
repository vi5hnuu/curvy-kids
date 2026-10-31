package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.PHONICS_DATA
import com.vi5hnu.curvykids.data.content.RecordingSheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class VoiceScriptTest {

    private val keyFormat = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")

    @Test
    fun `every recording key is a safe file name`() {
        val keys = HindiVoiceScript.all.map { it.clip.key } + PHONICS_DATA.map { it.clip.key }
        keys.forEach { assertTrue("bad key '$it'", keyFormat.matches(it)) }
    }

    @Test
    fun `one key never stands for two different lines`() {
        // all is de-duplicated by key, so check the raw sources for a key reused with other text.
        val raw = buildList {
            com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS.forEach { c ->
                com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS.forEach { m -> add(HindiVoiceScript.syllable(c, m)) }
                add(HindiVoiceScript.letter(c))
            }
            com.vi5hnu.curvykids.data.content.HINDI_VOWELS.forEach { add(HindiVoiceScript.letter(it)) }
        }
        raw.groupBy { it.clip.relativePath }.forEach { (key, lines) ->
            assertEquals("key $key has different texts", 1, lines.map { it.text }.distinct().size)
        }
    }

    @Test
    fun `letter and barakhadi clips follow the numbered folder layout`() {
        val vowels = com.vi5hnu.curvykids.data.content.HINDI_VOWELS
        val consonants = com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
        val matras = com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
        assertEquals("hi/hindi_vowels/01-a", HindiVoiceScript.letter(vowels[0]).clip.relativePath)
        assertEquals("hi/hindi_vowels/13-ah", HindiVoiceScript.letter(vowels[12]).clip.relativePath)
        assertEquals("hi/hindi_consonants/01-ka", HindiVoiceScript.letter(consonants[0]).clip.relativePath)
        assertEquals("hi/hindi_consonants/36-gya", HindiVoiceScript.letter(consonants[35]).clip.relativePath)
        assertEquals("hi/barakhadi/01-ka/01-ka", HindiVoiceScript.syllable(consonants[0], matras[0]).clip.relativePath)
        assertEquals("hi/barakhadi/01-ka/04-kii", HindiVoiceScript.syllable(consonants[0], matras[3]).clip.relativePath)
        assertEquals("hi/barakhadi/02-kha/12-khah", HindiVoiceScript.syllable(consonants[1], matras[11]).clip.relativePath)
        val barakhadi = consonants.flatMap { c -> matras.map { HindiVoiceScript.syllable(c, it).clip } }
        assertEquals(36 * 12, barakhadi.distinct().size)
    }

    @Test
    fun `no two letters in the same sound group are confused`() {
        val k = PHONICS_DATA.filter { it.soundGroup == "k" }.map { it.letter }.toSet()
        assertEquals(setOf("C", "K"), k)
    }

    /**
     * The recording list handed to the voice artist is committed under tools/voice. It's written
     * when missing; when content changes and it no longer matches, delete it and rerun the tests.
     */
    @Test
    fun `committed recording list matches the content`() {
        val file = File("../tools/voice/recording_list.txt")
        val expected = RecordingSheet.render()
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.writeText(expected)
        }
        assertEquals(
            "tools/voice/recording_list.txt is stale: delete it and rerun the unit tests",
            expected,
            file.readText(),
        )
    }
}
