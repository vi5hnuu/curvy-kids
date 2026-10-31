package com.vi5hnu.curvykids.data

import com.google.android.play.core.assetpacks.model.AssetPackErrorCode
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.vi5hnu.curvykids.audio.packs.FailReason
import com.vi5hnu.curvykids.audio.packs.PackState
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.allReady
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HINDI_VOWELS
import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TopicKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoicePackTest {

    @Test
    fun `every letter and barakhadi clip belongs to a pack`() {
        (HINDI_VOWELS + HINDI_CONSONANTS).forEach {
            assertEquals(VoicePack.HINDI_LETTERS, VoicePack.forClip(HindiVoiceScript.letter(it).clip))
        }
        HINDI_CONSONANTS.forEach { c ->
            BARAKHADI_MATRAS.forEach { m ->
                assertEquals(VoicePack.HINDI_BARAKHADI, VoicePack.forClip(HindiVoiceScript.syllable(c, m).clip))
            }
        }
    }

    @Test
    fun `only Hindi topics need a pack`() {
        TOPICS.forEach { topic ->
            val packs = VoicePack.requiredFor(topic)
            when {
                topic.kind == TopicKind.BARAKHADI ->
                    assertEquals(setOf(VoicePack.HINDI_LETTERS, VoicePack.HINDI_BARAKHADI), packs)
                topic.id.startsWith("hindi") -> assertEquals(setOf(VoicePack.HINDI_LETTERS), packs)
                else -> assertTrue("${topic.id} should need no pack", packs.isEmpty())
            }
        }
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
