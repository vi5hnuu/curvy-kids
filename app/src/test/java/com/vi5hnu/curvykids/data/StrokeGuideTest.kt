package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.GuideSeg
import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.data.content.Script
import com.vi5hnu.curvykids.data.content.StrokeGuide
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the hand-authored stroke-order tables. They are a few hundred coordinates typed by
 * hand, and a slip there produces a subtly malformed letter that only shows up by eye on one
 * character — exactly the kind of thing a test should catch instead.
 */
class StrokeGuideTest {

    private val latinLevels = Level.entries.filter { it.script == Script.LATIN }

    /** The Devanagari letters that have a stroke guide; the rest fall back to the letterform. */
    private val devanagariWithGuides = Level.entries
        .filter { it.script == Script.DEVANAGARI }
        .flatMap { it.characters }
        .filter { StrokeGuide.forCharacter(it) != null }

    /** Every point a stroke visits, start and segment endpoints alike. */
    private fun pointsOf(character: String): List<Pair<Float, Float>> =
        StrokeGuide.forCharacter(character).orEmpty().flatMap { stroke ->
            listOf(stroke.startX to stroke.startY) + stroke.segments.flatMap { seg ->
                when (seg) {
                    is GuideSeg.Line -> listOf(seg.x to seg.y)
                    is GuideSeg.Quad -> listOf(seg.cx to seg.cy, seg.x to seg.y)
                }
            }
        }

    @Test
    fun `every latin character the app teaches has a stroke guide`() {
        val missing = latinLevels
            .flatMap { it.characters }
            .filter { StrokeGuide.forCharacter(it) == null }
        assertTrue("No stroke guide for: $missing", missing.isEmpty())
    }

    @Test
    fun `every guide has at least one stroke and every stroke moves somewhere`() {
        latinLevels.flatMap { it.characters }.forEach { ch ->
            val strokes = StrokeGuide.forCharacter(ch)!!
            assertTrue("'$ch' has no strokes", strokes.isNotEmpty())
            strokes.forEachIndexed { i, stroke ->
                assertTrue("'$ch' stroke ${i + 1} has no segments", stroke.segments.isNotEmpty())
            }
        }
    }

    @Test
    fun `all coordinates stay inside the unit box`() {
        latinLevels.flatMap { it.characters }.forEach { ch ->
            pointsOf(ch).forEach { (x, y) ->
                assertTrue("'$ch' has x=$x outside 0..1", x in 0f..1f)
                assertTrue("'$ch' has y=$y outside 0..1", y in 0f..1f)
            }
        }
    }

    @Test
    fun `characters use a sane number of strokes`() {
        // A manuscript letter is between one and four strokes; more than that means a typo
        // duplicated an entry rather than a genuinely complex letterform.
        latinLevels.flatMap { it.characters }.forEach { ch ->
            val count = StrokeGuide.forCharacter(ch)!!.size
            assertTrue("'$ch' has $count strokes", count in 1..4)
        }
    }

    @Test
    fun `stroke order is taught for the letters that need it`() {
        // X is two crossing diagonals: top-left down, then top-right down. This is the case that
        // motivated replacing the left-to-right reveal, so it is pinned explicitly.
        val x = StrokeGuide.forCharacter("X")!!
        assertTrue("X should be drawn as two strokes", x.size == 2)
        assertTrue("X stroke 1 should start at the top left", x[0].startX < 0.5f && x[0].startY < 0.5f)
        assertTrue("X stroke 2 should start at the top right", x[1].startX > 0.5f && x[1].startY < 0.5f)
    }

    @Test
    fun `devanagari guides are well formed and stay inside the unit box`() {
        // These are generated from Wikimedia's stroke-order files rather than typed, so the
        // risk is not a typo but a bad extraction producing an empty or runaway path.
        devanagariWithGuides.forEach { ch ->
            val strokes = StrokeGuide.forCharacter(ch)!!
            assertTrue("'$ch' has no strokes", strokes.isNotEmpty())
            strokes.forEachIndexed { i, stroke ->
                assertTrue("'$ch' stroke ${i + 1} has no segments", stroke.segments.isNotEmpty())
            }
            pointsOf(ch).forEach { (x, y) ->
                assertTrue("'$ch' has x=$x outside 0..1", x in 0f..1f)
                assertTrue("'$ch' has y=$y outside 0..1", y in 0f..1f)
            }
        }
    }

    @Test
    fun `devanagari letters without a trustworthy source have no guide at all`() {
        // The conjuncts have no stroke-order source, and are ligatures rather than their parts
        // written in turn, so composing them would be guesswork. Showing a guessed stroke order
        // would teach a child the wrong hand, so these fall back to the letterform instead.
        val unsourced = "क्ष त्र ज्ञ".split(" ")
        unsourced.forEach {
            assertNull("'$it' should have no guide until a real source exists",
                StrokeGuide.forCharacter(it))
        }
    }

    @Test
    fun `most of the devanagari the app teaches is covered`() {
        val all = Level.entries.filter { it.script == Script.DEVANAGARI }.flatMap { it.characters }
        assertTrue("Only ${devanagariWithGuides.size} of ${all.size} letters have a guide",
            devanagariWithGuides.size == all.size - 3)
    }

    @Test
    fun `devanagari writes its headline last`() {
        // The shirorekha - the bar the letters hang from - is always the final stroke, and it
        // runs left to right. Getting this backwards was the clearest sign, when the order was
        // being guessed, that the data did not come from real handwriting.
        listOf("क", "ग", "न", "प").forEach { ch ->
            val last = StrokeGuide.forCharacter(ch)!!.last()
            val end = last.segments.last() as GuideSeg.Line
            assertTrue("'$ch' should finish on the headline", last.startY < 0.3f && end.y < 0.3f)
            assertTrue("'$ch' should draw the headline left to right", end.x > last.startX)
        }
    }

    @Test
    fun `barakhadi syllables are built from their consonant and their sign`() {
        // The vowel signs are read out of the vowel letters (आ is अ with ा, and so on), so a
        // syllable's guide is its consonant's strokes, then the sign, then one headline.
        listOf("का", "के", "कै", "को", "कौ", "कं", "कः").forEach { syllable ->
            val strokes = StrokeGuide.forCharacter(syllable)
            assertNotNull("'$syllable' should have a guide", strokes)
            val consonant = StrokeGuide.forCharacter("क")!!
            assertTrue(
                "'$syllable' should add strokes to क, not replace them",
                strokes!!.size > consonant.size - 1,
            )
            pointsOf(syllable).forEach { (x, y) ->
                assertTrue("'$syllable' has x=$x outside 0..1", x in 0f..1f)
                assertTrue("'$syllable' has y=$y outside 0..1", y in 0f..1f)
            }
            // The headline is written last and runs across the whole syllable.
            val last = strokes.last()
            val end = last.segments.last() as GuideSeg.Line
            assertTrue("'$syllable' should finish on the headline", end.x - last.startX > 0.7f)
        }
    }

    @Test
    fun `every barakhadi form of a guided consonant can be demonstrated`() {
        val forms = listOf("", "ा", "ि", "ी", "ु", "ू", "े", "ै", "ो", "ौ", "ं", "ः")
        "क ख ग घ च छ ज ट ठ ड त थ द ध न प फ ब भ म य र ल व श ष स ह".split(" ").forEach { c ->
            forms.forEach { f ->
                assertNotNull("'$c$f' should have a guide", StrokeGuide.forCharacter(c + f))
            }
        }
    }

    /**
     * Every Devanagari character the app can demonstrate, letters and barakhadi forms alike.
     *
     * Only Devanagari: the Latin guides are drawn against manuscript rules, where 'a' is meant
     * to be shorter than 'A' and 'g' is meant to hang below the line. Devanagari has no such
     * variation - every letter hangs from the headline and fills the body - so there they should
     * all come out the same size, and it is a defect when they do not.
     */
    private fun everyDevanagariCharacter(): List<String> {
        val letters = Level.entries
            .filter { it.script == Script.DEVANAGARI }
            .flatMap { it.characters }
        val syllables = "क ख ग घ च छ ज ट ठ ड त थ द ध न प म य र ल व श ष स ह".split(" ")
            .flatMap { c ->
                listOf("ा", "ि", "ी", "ु", "ू", "े", "ै", "ो", "ौ", "ं", "ः").map { c + it }
            }
        return (letters + syllables).filter { StrokeGuide.forCharacter(it) != null }
    }

    @Test
    fun `every devanagari guide fills its box, so they are all shown at one size`() {
        // The guide is drawn into a square, so a character that only occupies a corner of the
        // unit box is drawn smaller than its neighbours. The composed syllables were doing
        // exactly that - a sign needing room above pushed everything down and left the card
        // half empty - and next to a letter it looked like a different alphabet.
        everyDevanagariCharacter().forEach { ch ->
            val pts = pointsOf(ch)
            val spread = maxOf(
                pts.maxOf { it.first } - pts.minOf { it.first },
                pts.maxOf { it.second } - pts.minOf { it.second },
            )
            assertTrue(
                "'$ch' only fills ${"%.2f".format(spread)} of its box; it will look undersized",
                spread > 0.80f,
            )
        }
    }

    @Test
    fun `every devanagari guide is roughly centred in its box`() {
        everyDevanagariCharacter().forEach { ch ->
            val pts = pointsOf(ch)
            val cx = (pts.maxOf { it.first } + pts.minOf { it.first }) / 2f
            val cy = (pts.maxOf { it.second } + pts.minOf { it.second }) / 2f
            assertTrue("'$ch' sits off-centre horizontally at $cx", cx in 0.35f..0.65f)
            assertTrue("'$ch' sits off-centre vertically at $cy", cy in 0.35f..0.65f)
        }
    }

    @Test
    fun `multi character strings and symbols resolve to nothing`() {
        assertNull(StrokeGuide.forCharacter("AB"))
        assertNull(StrokeGuide.forCharacter(""))
        assertNull(StrokeGuide.forCharacter("?"))
        assertNotNull(StrokeGuide.forCharacter("A"))
    }
}
