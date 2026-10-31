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
                add(HindiVoiceScript.letter(c)); add(HindiVoiceScript.letterWithWord(c))
            }
            com.vi5hnu.curvykids.data.content.HINDI_VOWELS.forEach {
                add(HindiVoiceScript.letter(it)); add(HindiVoiceScript.letterWithWord(it))
            }
        }
        raw.groupBy { it.clip.key }.forEach { (key, lines) ->
            assertEquals("key $key has different texts", 1, lines.map { it.text }.distinct().size)
        }
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
