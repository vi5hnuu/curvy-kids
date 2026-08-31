package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TopicKind
import com.vi5hnu.curvykids.data.content.dailyPicks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The Home screen's suggested-activity selection. */
class TopicPicksTest {

    private val day = 20_000L

    @Test
    fun `returns the requested number of picks`() {
        assertEquals(6, dailyPicks(lastTopicId = null, seed = day).size)
        assertEquals(4, dailyPicks(lastTopicId = null, seed = day, count = 4).size)
    }

    @Test
    fun `never repeats the topic already offered by the continue card`() {
        val picks = dailyPicks(lastTopicId = "upper", seed = day)
        assertTrue(picks.none { it.id == "upper" })
    }

    @Test
    fun `always includes a writing topic`() {
        // Writing is the app's core skill; a purely random draw regularly produced a Home
        // screen with no tracing practice on it at all.
        (0L until 30L).forEach { d ->
            val picks = dailyPicks(lastTopicId = null, seed = d)
            assertTrue("day $d had no writing topic", picks.any { it.kind == TopicKind.TRACE })
        }
    }

    @Test
    fun `picks are stable within a day and change between days`() {
        assertEquals(
            dailyPicks(lastTopicId = null, seed = day).map { it.id },
            dailyPicks(lastTopicId = null, seed = day).map { it.id },
        )
        assertNotEquals(
            dailyPicks(lastTopicId = null, seed = day).map { it.id },
            dailyPicks(lastTopicId = null, seed = day + 1).map { it.id },
        )
    }

    @Test
    fun `picks contain no duplicates and are all real topics`() {
        val picks = dailyPicks(lastTopicId = null, seed = day)
        assertEquals(picks.size, picks.distinctBy { it.id }.size)
        assertTrue(picks.all { p -> TOPICS.any { it.id == p.id } })
    }

    @Test
    fun `handles a pool smaller than the requested count`() {
        val two = TOPICS.take(2)
        assertEquals(2, dailyPicks(all = two, lastTopicId = null, seed = day, count = 6).size)
    }
}
