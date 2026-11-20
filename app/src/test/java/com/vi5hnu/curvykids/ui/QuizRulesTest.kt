package com.vi5hnu.curvykids.ui

import com.vi5hnu.curvykids.ui.activities.components.QuizOutcome
import com.vi5hnu.curvykids.ui.activities.components.QuizProgress
import com.vi5hnu.curvykids.ui.activities.components.QuizRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizRulesTest {

    private val rules = QuizRules(length = 10, missesBeforeReveal = 2, celebrateEvery = 5)

    @Test
    fun `first miss asks to try again, second reveals the answer`() {
        val p = QuizProgress()
        assertEquals(QuizOutcome.TRY_AGAIN, p.outcomeOf(false, rules))
        val p2 = p.after(QuizOutcome.TRY_AGAIN)
        assertEquals(QuizOutcome.REVEAL, p2.outcomeOf(false, rules))
        val p3 = p2.after(QuizOutcome.REVEAL)
        assertEquals(1, p3.asked)
        assertEquals(0, p3.correct)
        assertEquals(0, p3.misses)
    }

    @Test
    fun `session ends after its length and does not celebrate on the last answer`() {
        var p = QuizProgress()
        repeat(9) { p = p.after(QuizOutcome.CORRECT) }
        assertFalse(p.isFinished(rules))
        p = p.after(QuizOutcome.CORRECT)
        assertTrue(p.isFinished(rules))
        assertFalse(p.celebrates(QuizOutcome.CORRECT, rules))
    }

    @Test
    fun `celebrates every fifth correct answer`() {
        var p = QuizProgress()
        repeat(5) { p = p.after(QuizOutcome.CORRECT) }
        assertTrue(p.celebrates(QuizOutcome.CORRECT, rules))
    }

    @Test
    fun `stars reflect how many were right`() {
        assertEquals(3, QuizProgress(asked = 10, correct = 8).stars(rules))
        assertEquals(2, QuizProgress(asked = 10, correct = 5).stars(rules))
        assertEquals(1, QuizProgress(asked = 10, correct = 2).stars(rules))
    }
}
