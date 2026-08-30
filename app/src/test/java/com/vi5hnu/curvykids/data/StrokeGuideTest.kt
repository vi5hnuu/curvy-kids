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
    fun `devanagari deliberately has no guide rather than a guessed one`() {
        val hindi = Level.entries.first { it.script == Script.DEVANAGARI }
        hindi.characters.forEach { assertNull(StrokeGuide.forCharacter(it)) }
    }

    @Test
    fun `multi character strings and symbols resolve to nothing`() {
        assertNull(StrokeGuide.forCharacter("AB"))
        assertNull(StrokeGuide.forCharacter(""))
        assertNull(StrokeGuide.forCharacter("?"))
        assertNotNull(StrokeGuide.forCharacter("A"))
    }
}
