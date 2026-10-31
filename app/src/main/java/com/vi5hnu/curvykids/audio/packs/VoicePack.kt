package com.vi5hnu.curvykids.audio.packs

import com.vi5hnu.curvykids.audio.VoiceClip
import com.vi5hnu.curvykids.data.content.Script
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.TopicKind

/**
 * A category of recorded voice clips that Google Play delivers on demand, so the app download
 * stays small and a family only fetches the languages and topics they use.
 *
 * Each entry is backed by an asset-pack Gradle module of the same [packName] (see
 * settings.gradle.kts), whose `src/main/assets/voice/<folder>` holds the clips for [folders].
 * Adding a category = one module + one entry here (+ a line in [requiredFor]).
 *
 * @param title   Shown to the parent on the download screen.
 * @param folders Folders under `assets/voice/` this pack owns, e.g. "hi/barakhadi".
 */
enum class VoicePack(val packName: String, val title: String, val folders: List<String>) {
    HINDI_LETTERS("voice_hi_letters", "Hindi letters", listOf("hi/hindi_vowels", "hi/hindi_consonants")),
    HINDI_BARAKHADI("voice_hi_barakhadi", "Hindi barakhadi", listOf("hi/barakhadi"));

    fun owns(clip: VoiceClip): Boolean = folders.any { clip.relativePath.startsWith("$it/") }

    companion object {
        fun forClip(clip: VoiceClip): VoicePack? = entries.firstOrNull { it.owns(clip) }
        fun named(packName: String): VoicePack? = entries.firstOrNull { it.packName == packName }

        /**
         * The packs a topic can't be used without. The only place topics are tied to packs:
         * Hindi tracing speaks the letter clips; barakhadi speaks syllables and, when a consonant
         * is tapped, the letter clip too.
         */
        fun requiredFor(topic: Topic): Set<VoicePack> = when {
            topic.kind == TopicKind.BARAKHADI -> setOf(HINDI_LETTERS, HINDI_BARAKHADI)
            topic.kind == TopicKind.TRACE && topic.set?.script == Script.DEVANAGARI -> setOf(HINDI_LETTERS)
            else -> emptySet()
        }
    }
}
