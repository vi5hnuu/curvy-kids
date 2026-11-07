package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.ANIMALS
import com.vi5hnu.curvykids.data.content.COLOR_ITEMS
import com.vi5hnu.curvykids.data.content.CRAYON_BOX
import com.vi5hnu.curvykids.data.content.DAYS
import com.vi5hnu.curvykids.data.content.EMOTIONS
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript
import com.vi5hnu.curvykids.data.content.FRUITS_VEGGIES
import com.vi5hnu.curvykids.data.content.GAME_PICTURES
import com.vi5hnu.curvykids.data.content.MONTHS
import com.vi5hnu.curvykids.data.content.OPPOSITES
import com.vi5hnu.curvykids.data.content.PHONICS_DATA
import com.vi5hnu.curvykids.data.content.PICTURE_SPECS
import com.vi5hnu.curvykids.data.content.RHYME_GROUPS
import com.vi5hnu.curvykids.data.content.SEASONS_LIST
import com.vi5hnu.curvykids.data.content.SHAPES
import com.vi5hnu.curvykids.data.content.Speech
import com.vi5hnu.curvykids.data.content.VEHICLES
import com.vi5hnu.curvykids.data.content.BODY_PARTS
import com.vi5hnu.curvykids.data.content.FRACTIONS
import com.vi5hnu.curvykids.data.content.TIMES
import com.vi5hnu.curvykids.data.content.WORD_LIST
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Keeps [EnglishVoiceScript] honest: every line the app can say is on the recording list, and
 * no screen speaks English around it.
 */
class EnglishVoiceScriptTest {

    private val keyFormat = Regex("^[a-z0-9]+(-[a-z0-9]+)*$")
    private val listed = EnglishVoiceScript.all.map { it.clip.relativePath }.toSet()

    private fun assertListed(speech: Speech) = speech.parts.forEach {
        assertTrue("'${it.text}' (${it.clip.relativePath}) is said but not on the recording list", it.clip.relativePath in listed)
    }

    @Test
    fun `every key is a safe file name`() {
        EnglishVoiceScript.all.forEach { assertTrue("bad key '${it.clip.key}'", keyFormat.matches(it.clip.key)) }
    }

    @Test
    fun `each line is listed once`() {
        assertEquals(EnglishVoiceScript.all.size, listed.size)
    }

    @Test
    fun `spellings of the same words share one recording`() {
        assertEquals(EnglishVoiceScript.line("Try again").clip, EnglishVoiceScript.line("Try again!").clip)
        assertEquals(EnglishVoiceScript.line("a for Apple").clip, EnglishVoiceScript.line("A for Apple.").clip)
        assertEquals("en/lines/find-the-pairs", EnglishVoiceScript.line("Find the pairs!").clip.relativePath)
    }

    @Test
    fun `every sentence the screens build is on the recording list`() {
        val e = EnglishVoiceScript
        val numbers = 0..e.MAX_NUMBER
        listOf(e.GREETING, e.MY_STICKERS, e.TRY_AGAIN, e.ALL_DONE, e.GET_VOICES, e.GETTING_VOICES, e.DRAW_FIRST,
            e.HINDI_GIVE_UP, e.RUBBED_OUT, e.NEW_PAGE, e.SAVED).forEach { assertListed(e.say(it)) }
        (e.TRACE_PRAISE + e.QUIZ_LINES).forEach { assertListed(e.say(it)) }
        assertListed(e.yes())
        numbers.forEach { n ->
            assertListed(e.tap(n)); assertListed(e.yesLeft(n)); assertListed(e.yes(n.toString()))
            assertListed(e.addition(n, n)); assertListed(e.subtraction(n, n))
        }
        (('A'..'Z') + ('a'..'z') + ('0'..'9')).map { it.toString() }.forEach { c ->
            assertListed(e.drawThe(c, isNumber = c[0].isDigit()))
            assertListed(e.letterPhrase(c))
            assertListed(e.traceTryAgain(c, sawNothing = true)); assertListed(e.traceTryAgain(c, sawNothing = false))
            assertListed(e.traceGiveUp(c))
        }
        SHAPES.forEach { assertListed(e.findThe(it.name)); assertListed(e.yes(it.name)); assertListed(e.thisOneIs(it.name)) }
        COLOR_ITEMS.forEach { assertListed(e.find(it.name)); assertListed(e.yes(it.name)) }
        ANIMALS.forEach { assertListed(e.animalCall(it)); assertListed(e.say(e.whoSays(it))); assertListed(e.yes(it.name)) }
        VEHICLES.forEach { assertListed(e.vehicleCall(it)); assertListed(e.findThe(it.name)) }
        BODY_PARTS.forEach { assertListed(e.findThe(it.name)) }
        EMOTIONS.forEach { assertListed(e.find(it.name)) }
        DAYS.forEach { assertListed(e.say(e.whatComesAfter(it))); assertListed(e.yes(it)) }
        MONTHS.forEach { assertListed(e.say(e.whatComesAfter(it.name))); assertListed(e.yes(it.name)) }
        SEASONS_LIST.forEach { assertListed(e.say(e.seasonCall(it))); assertListed(e.say(e.whichSeason(it))) }
        OPPOSITES.forEach { assertListed(e.say(e.opposite(it.word1))); assertListed(e.say(e.opposite(it.word2))); assertListed(e.yes(it.word1)) }
        FRUITS_VEGGIES.forEach {
            assertListed(e.say(e.fruitOrVegetable(it.name)))
            assertListed(e.yesFruit(it.name, true)); assertListed(e.yesFruit(it.name, false))
        }
        listOf("a fruit", "a vegetable", "this one", "the big one", "the small one").forEach { assertListed(e.thisOneIs(it)) }
        PHONICS_DATA.forEach { assertListed(e.yesSound(it)); assertListed(e.say(e.soundHint(it))) }
        RHYME_GROUPS.forEach { g ->
            val words = (listOf(g.word) + g.rhymes.map { it.first }).map { it.lowercase() }
            assertListed(e.rhymesWith(words[0], words))
            words.forEach { assertListed(e.yesRhyme(words[0], it)) }
        }
        WORD_LIST.forEach { assertListed(e.say(it.word)) }
        PICTURE_SPECS.values.flatMap { it.items }.forEach { assertListed(e.findThe(it.name)); assertListed(e.say(it.name)) }
        GAME_PICTURES.forEach { assertListed(e.thisOneIs(e.pictureName(it))) }
        CRAYON_BOX.forEach { assertListed(e.say(e.crayon(it.name))) }
        TIMES.forEach { assertListed(e.yes(it.label)) }
        FRACTIONS.forEach { assertListed(e.yes(it.spoken)) }
    }

    @Test
    fun `a sentence plays from recordings only when every part is recorded`() {
        val speech = EnglishVoiceScript.findThe("Circle")
        assertTrue(speech.fullyRecorded { true })
        assertFalse(speech.fullyRecorded { it.key != "circle" })
        assertFalse(Speech(emptyList()).fullyRecorded { true })
        assertEquals("Find the Circle", speech.text)
    }

    @Test
    fun `picture names come from the file name`() {
        assertEquals("teddy bear", EnglishVoiceScript.pictureName(com.vi5hnu.curvykids.data.content.AssetPath("art/toys/toy-teddy-bear.svg")))
    }

    /**
     * English goes through EnglishVoiceScript and `say`, so it is on the recording list. A bare
     * `speak(` in the screens is allowed only for Hindi (a Hindi voice tag) or a line with its
     * own recording (`clip =`).
     */
    @Test
    fun `no screen speaks English around the catalogue`() {
        val calls = File("src/main/java/com/vi5hnu/curvykids/ui").walkTopDown().filter { it.extension == "kt" }.flatMap { f ->
            val text = f.readText()
            Regex("""\.speak\(""").findAll(text).map { m -> f.name to text.substring(m.range.first, minOf(text.length, m.range.first + 260)).substringBefore(")\n") }
        }.toList()
        val bare = calls.filterNot { (_, call) -> "hi-IN" in call || "HINDI_TTS_TAG" in call || "clip =" in call }
        assertTrue("English spoken outside EnglishVoiceScript: $bare", bare.isEmpty())
    }
}
