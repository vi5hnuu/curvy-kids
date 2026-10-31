package com.vi5hnu.curvykids.ui

import com.vi5hnu.curvykids.ui.activities.buildPhonicSpec
import org.junit.Assert.assertEquals
import org.junit.Test

class PhonicsQuizTest {

    @Test
    fun `every question has exactly one letter with the target sound`() {
        repeat(500) {
            val spec = buildPhonicSpec(emptySet())
            assertEquals(3, spec.choices.size)
            assertEquals(1, spec.choices.count { it.soundGroup == spec.target.soundGroup })
        }
    }
}
