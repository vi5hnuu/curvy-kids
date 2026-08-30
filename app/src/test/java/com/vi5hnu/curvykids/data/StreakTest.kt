package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.app.AppRepository
import org.junit.Assert.assertEquals
import org.junit.Test

/** The daily-streak calendar rules. Pure, so no DataStore or clock is involved. */
class StreakTest {

    private val today = 20_000L // an arbitrary epoch day

    @Test
    fun `first ever session starts the streak at one`() {
        assertEquals(1, AppRepository.nextStreak(current = 1, lastDay = null, today = today))
    }

    @Test
    fun `playing on a consecutive day grows the streak`() {
        assertEquals(5, AppRepository.nextStreak(current = 4, lastDay = today - 1, today = today))
    }

    @Test
    fun `playing twice on the same day does not double count`() {
        assertEquals(4, AppRepository.nextStreak(current = 4, lastDay = today, today = today))
    }

    @Test
    fun `skipping a day resets the streak`() {
        assertEquals(1, AppRepository.nextStreak(current = 9, lastDay = today - 2, today = today))
    }

    @Test
    fun `a clock moved backwards restarts rather than freezing or going negative`() {
        assertEquals(1, AppRepository.nextStreak(current = 9, lastDay = today + 3, today = today))
    }
}
