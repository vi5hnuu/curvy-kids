package com.vi5hnu.curvykids.data

import com.google.android.play.core.assetpacks.model.AssetPackErrorCode
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.vi5hnu.curvykids.audio.packs.Delivery
import com.vi5hnu.curvykids.audio.packs.FailReason
import com.vi5hnu.curvykids.audio.packs.PackState
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.allReady
import com.vi5hnu.curvykids.billing.Lockable
import com.vi5hnu.curvykids.billing.ProCatalog
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HINDI_VOWELS
import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.STORIES
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TopicKind
import com.vi5hnu.curvykids.data.content.TopicVoices
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoicePackTest {

    @Test
    fun `letters, free barakhadi and pro barakhadi go to their packs`() {
        (HINDI_VOWELS + HINDI_CONSONANTS).forEach {
            assertEquals(VoicePack.HINDI_LETTERS, VoicePack.homeOf(HindiVoiceScript.letter(it).clip))
        }
        HINDI_CONSONANTS.forEachIndexed { i, c ->
            val expected = if (ProCatalog.isFree(Lockable.BarakhadiLetter(i))) VoicePack.HINDI_BARAKHADI else VoicePack.PRO_HINDI
            BARAKHADI_MATRAS.forEach { m -> assertEquals(expected, VoicePack.homeOf(HindiVoiceScript.syllable(c, m).clip)) }
        }
        STORIES.forEachIndexed { i, story ->
            val expected = if (ProCatalog.isFree(Lockable.Story(i))) null else VoicePack.PRO_HINDI
            assertEquals(expected, VoicePack.homeOf(HindiVoiceScript.story(story).clip))
        }
    }

    @Test
    fun `free activities need only the free packs, pro ones their world's pack`() {
        TOPICS.forEach { topic ->
            val packs = VoicePack.requiredFor(topic)
            if (ProCatalog.isFree(Lockable.Activity(topic.id))) {
                assertTrue("${topic.id} must not need a Pro pack", packs.none { it.delivery == Delivery.ON_DEMAND })
            }
            if (topic.kind == TopicKind.BARAKHADI) {
                assertTrue(packs.containsAll(setOf(VoicePack.HINDI_LETTERS, VoicePack.HINDI_BARAKHADI)))
            }
        }
    }

    @Test
    fun `nothing a free activity says leaves the app`() {
        TOPICS.filter { ProCatalog.isFree(Lockable.Activity(it.id)) }.forEach { topic ->
            TopicVoices.clipsFor(topic).forEach { clip ->
                assertEquals("${topic.id}: ${clip.relativePath}", null, VoicePack.homeOf(clip))
            }
        }
        TopicVoices.shared.forEach { assertEquals(it.relativePath, null, VoicePack.homeOf(it)) }
    }

    @Test
    fun `play statuses map to states`() {
        assertEquals(PackState.Ready, PackState.from(AssetPackStatus.COMPLETED, 10, 10, 0))
        assertEquals(PackState.Downloading(5, 10), PackState.from(AssetPackStatus.DOWNLOADING, 5, 10, 0))
        assertEquals(PackState.NotDownloaded(10), PackState.from(AssetPackStatus.NOT_INSTALLED, 0, 10, 0))
        assertEquals(PackState.NeedsConfirmation, PackState.from(AssetPackStatus.WAITING_FOR_WIFI, 0, 10, 0))
        assertEquals(
            PackState.Failed(FailReason.OFFLINE),
            PackState.from(AssetPackStatus.FAILED, 0, 10, AssetPackErrorCode.NETWORK_ERROR),
        )
        assertEquals(0.5f, PackState.Downloading(5, 10).progress)
    }

    @Test
    fun `all packs must be ready`() {
        val states = mapOf(VoicePack.HINDI_LETTERS to PackState.Ready, VoicePack.HINDI_BARAKHADI to PackState.Checking)
        assertTrue(states.allReady(setOf(VoicePack.HINDI_LETTERS)))
        assertFalse(states.allReady(VoicePack.entries))
    }
}
