package com.vi5hnu.curvykids.ui

import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.ui.activities.buildPatternSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PatternsTest {

    private val pics = (0..6).map { AssetPath("art/test/p-$it.svg") }

    private fun check(unit: List<Int>) {
        val spec = buildPatternSpec(unit, pics)
        val full = spec.shown + spec.answer
        // The whole row, answer included, is the unit repeated.
        full.forEachIndexed { i, p -> assertEquals(pics[unit[i % unit.size]], p) }
        // At least one and a half repeats are visible.
        assertTrue(spec.shown.size >= unit.size + (unit.size + 1) / 2)
        // Exactly one choice is right, and the choices are distinct.
        assertEquals(1, spec.choices.count { it == spec.answer })
        assertEquals(spec.choices.size, spec.choices.distinct().size)
    }

    @Test fun ab() = check(listOf(0, 1))
    @Test fun aabb() = check(listOf(0, 0, 1, 1))
    @Test fun abb() = check(listOf(0, 1, 1))
    @Test fun abc() = check(listOf(0, 1, 2))
}
