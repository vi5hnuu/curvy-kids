package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.data.content.PICTURE_SPECS
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TopicKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Structural rules the topic catalogue has to keep.
 *
 * These exist because the failure mode is silent: a topic with no illustration falls back to a
 * placeholder badge, and a topic with no content opens an empty screen — neither breaks the
 * build, and both are easy to miss among 45 entries.
 */
class TopicIntegrityTest {

    private val glyphSource =
        File("src/main/java/com/vi5hnu/curvykids/ui/components/TopicGlyph.kt").readText()

    /** Ids listed in TopicGlyph's two coverage sets. */
    private fun idsIn(setName: String): Set<String> {
        val start = glyphSource.indexOf("private val $setName")
        require(start >= 0) { "$setName not found in TopicGlyph.kt" }
        val body = glyphSource.substring(start, glyphSource.indexOf(")", start))
        return Regex("\"([a-z_]+)\"").findAll(body).map { it.groupValues[1] }.toSet()
    }

    @Test
    fun `topic ids are unique`() {
        val ids = TOPICS.map { it.id }
        assertEquals("Duplicate topic ids: ${ids.groupBy { it }.filter { it.value.size > 1 }.keys}",
            ids.size, ids.distinct().size)
    }

    @Test
    fun `every topic has a badge that is not a placeholder`() {
        val covered = idsIn("LETTER_TOPICS") + idsIn("GLYPH_TOPICS")
        val uncovered = TOPICS.map { it.id }.filterNot { it in covered }
        assertTrue(
            "These topics would fall back to the placeholder badge: $uncovered",
            uncovered.isEmpty(),
        )
    }

    @Test
    fun `writing topics carry their letters and other topics do not`() {
        val letterTopics = idsIn("LETTER_TOPICS")
        TOPICS.forEach { topic ->
            if (topic.id in letterTopics) {
                assertTrue("Writing topic '${topic.id}' has no glyph text", topic.glyph.isNotBlank())
            } else {
                assertTrue(
                    "Topic '${topic.id}' still carries glyph text '${topic.glyph}'; " +
                        "non-writing topics draw an illustration instead",
                    topic.glyph.isBlank(),
                )
            }
        }
    }

    @Test
    fun `every trace topic names a character set`() {
        TOPICS.filter { it.kind == TopicKind.TRACE }.forEach {
            assertTrue("Trace topic '${it.id}' has no Level", it.set != null)
        }
    }

    @Test
    fun `every trace level is reachable from exactly one topic`() {
        val used = TOPICS.mapNotNull { it.set }
        assertEquals("A Level is wired to more than one topic", used.size, used.distinct().size)
        val missing = Level.entries.filterNot { it in used }
        assertTrue("These levels have no topic to open them: $missing", missing.isEmpty())
    }

    @Test
    fun `every picture topic has content and every content entry has a topic`() {
        val pictureIds = TOPICS.filter { it.kind == TopicKind.PICTURE }.map { it.id }.toSet()
        val withoutContent = pictureIds.filterNot { PICTURE_SPECS.containsKey(it) }
        assertTrue("Picture topics that would open empty: $withoutContent", withoutContent.isEmpty())

        val orphaned = PICTURE_SPECS.keys.filterNot { it in pictureIds }
        assertTrue("Picture content no topic can reach: $orphaned", orphaned.isEmpty())
    }

    @Test
    fun `no picture topic ships an empty item list`() {
        PICTURE_SPECS.forEach { (id, spec) ->
            assertTrue("Picture topic '$id' has no items", spec.items.isNotEmpty())
        }
    }
}
