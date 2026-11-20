package com.vi5hnu.curvykids.ui

import com.vi5hnu.curvykids.ui.parent.GateQuestion
import com.vi5hnu.curvykids.ui.parent.numberInWords
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GateQuestionTest {
    @Test fun numbersAreWrittenInWords() {
        assertEquals("seven", numberInWords(7))
        assertEquals("thirteen", numberInWords(13))
        assertEquals("forty", numberInWords(40))
        assertEquals("forty-eight", numberInWords(48))
        assertEquals("ninety-nine", numberInWords(99))
    }

    @Test fun questionHasNoDigitsAndAddsUp() {
        val q = GateQuestion(23, 48)
        assertEquals("twenty-three plus forty-eight", q.text)
        assertEquals(71, q.answer)
    }

    @Test fun randomQuestionsUseTwoDigitNumbers() {
        val random = Random(1)
        repeat(500) {
            val q = GateQuestion.random(random)
            assertTrue(q.a in 21..79 && q.b in 12..79)
            assertFalse(q.text.any { it.isDigit() })
        }
    }
}
