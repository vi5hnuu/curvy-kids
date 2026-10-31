package com.vi5hnu.curvykids.data

import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TOPIC_ART
import com.vi5hnu.curvykids.data.content.TopicArt
import com.vi5hnu.curvykids.data.content.World
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class WorldsTest {

    @Test
    fun `every topic is in exactly one world`() {
        val listed = World.entries.flatMap { it.topicIds }
        assertEquals("a topic is listed twice", listed.size, listed.toSet().size)
        assertEquals("topics missing from every world", emptySet<String>(), TOPICS.map { it.id }.toSet() - listed.toSet())
        assertEquals("worlds list unknown topics", emptySet<String>(), listed.toSet() - TOPICS.map { it.id }.toSet())
    }

    @Test
    fun `every topic has tile art and every picture exists`() {
        TOPICS.forEach { assertTrue("${it.id} has no tile art", it.id in TOPIC_ART) }
        val pictures = TOPIC_ART.values.filterIsInstance<TopicArt.Picture>() +
            World.entries.map { it.art }.filterIsInstance<TopicArt.Picture>()
        pictures.forEach { assertTrue("missing ${it.asset.path}", File("src/main/assets", it.asset.path).isFile) }
    }
}
