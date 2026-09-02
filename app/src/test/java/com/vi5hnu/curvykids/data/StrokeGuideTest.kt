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
        // Six consonants have only a ~60px animation to read, too coarse to recover a path
        // from, and the composites have no source. Showing a guessed stroke order would teach
        // a child the wrong hand, so those letters fall back to the letterform instead.
        val unsourced = "फ ब भ म व स अं अः क्ष त्र ज्ञ".split(" ")
        unsourced.forEach {
            assertNull("'$it' should have no guide until a real source exists",
                StrokeGuide.forCharacter(it))
        }
    }

    @Test
    fun `most of the devanagari the app teaches is covered`() {
        val all = Level.entries.filter { it.script == Script.DEVANAGARI }.flatMap { it.characters }
        assertTrue("Only ${devanagariWithGuides.size} of ${all.size} letters have a guide",
            devanagariWithGuides.size >= all.size * 3 / 4)
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
    fun `multi character strings and symbols resolve to nothing`() {
        assertNull(StrokeGuide.forCharacter("AB"))
        assertNull(StrokeGuide.forCharacter(""))
        assertNull(StrokeGuide.forCharacter("?"))
        assertNotNull(StrokeGuide.forCharacter("A"))
    }
}
