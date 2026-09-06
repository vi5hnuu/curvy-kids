package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.StrokeGuide
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import org.junit.Test

/** Reports exactly which barakhadi syllables can be demonstrated and which cannot. */
class BarakhadiCoverageTest {

    @Test
    fun `report coverage`() {
        var have = 0
        var total = 0
        val missingByMatra = linkedMapOf<String, MutableList<String>>()
        HINDI_CONSONANTS.forEach { c ->
            BARAKHADI_MATRAS.forEach { m ->
                val syllable = barakhadiSyllable(c.char, m)
                total++
                if (StrokeGuide.forCharacter(syllable) != null) {
                    have++
                } else {
                    missingByMatra.getOrPut(m.key) { mutableListOf() }.add(c.char)
                }
            }
        }
        println("BARAKHADI: $have of $total syllables have a stroke guide")
        missingByMatra.forEach { (key, cons) ->
            println("  missing for matra '$key': ${cons.size} consonants -> ${cons.take(40).joinToString(" ")}")
        }
    }
}
