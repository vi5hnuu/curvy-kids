package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.progress.traceProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The mastery bars. These exist because a hardcoded Latin-only total was being compared against
 * a mastered list that also accumulated Hindi characters, producing impossible values like 80/62.
 */
class MasteryProgressTest {

    @Test
    fun `latin bar is always present and totals the latin trace sets`() {
        val bars = traceProgress(emptyList())
        assertEquals(1, bars.size)
        assertEquals("Letters & Numbers", bars[0].label)
        assertEquals(62, bars[0].total) // 26 upper + 26 lower + 10 numbers
        assertEquals(0, bars[0].done)
    }

    @Test
    fun `hindi bar appears only once hindi has been practised`() {
        assertTrue(traceProgress(listOf("upper:A")).none { it.label == "Hindi Letters" })

        val bars = traceProgress(listOf("hindi_vowels:अ"))
        val hindi = bars.single { it.label == "Hindi Letters" }
        assertEquals(1, hindi.done)
        assertEquals(49, hindi.total) // 13 vowels + 36 consonants
    }

    @Test
    fun `hindi characters are not counted against the latin total`() {
        val bars = traceProgress(listOf("upper:A", "upper:B", "hindi_vowels:अ", "hindi_consonants:क"))
        val latin = bars.single { it.label == "Letters & Numbers" }
        assertEquals(2, latin.done)
        assertEquals(62, latin.total)
        assertEquals(2, bars.single { it.label == "Hindi Letters" }.done)
    }

    @Test
    fun `done never exceeds total even with unexpected stored keys`() {
        val everything = List(200) { "upper:X$it" }
        val latin = traceProgress(everything).single { it.label == "Letters & Numbers" }
        assertTrue(latin.done <= latin.total)
        assertEquals(1f, latin.fraction, 0.0001f)
    }

    @Test
    fun `unknown topic ids are ignored rather than inflating progress`() {
        val latin = traceProgress(listOf("nonsense:Z", "upper:A")).single { it.label == "Letters & Numbers" }
        assertEquals(1, latin.done)
    }
}
