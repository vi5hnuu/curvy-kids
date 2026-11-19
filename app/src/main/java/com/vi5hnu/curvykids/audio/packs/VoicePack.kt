package com.vi5hnu.curvykids.audio.packs

import com.vi5hnu.curvykids.audio.VoiceClip
import com.vi5hnu.curvykids.billing.Lockable
import com.vi5hnu.curvykids.billing.ProCatalog
import com.vi5hnu.curvykids.data.content.STORIES
import com.vi5hnu.curvykids.data.content.Script
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.TopicKind
import com.vi5hnu.curvykids.data.content.TopicVoices
import com.vi5hnu.curvykids.data.content.World

/** When Google Play delivers a [VoicePack]. */
enum class Delivery {
    /** By itself, right after the app is installed or updated: the free part of the app needs it. */
    FAST_FOLLOW,

    /**
     * Only when the app asks, which it does once an item is open to the child (Pro owned, or
     * opened for today with a video) and is tapped, or when a grown-up asks for all of them in
     * the Parent Zone. A family that never buys Pro never downloads these.
     */
    ON_DEMAND,
}

/**
 * Recorded voice clips that Google Play delivers apart from the app, so the app download stays
 * small and Pro recordings reach only the families who can open them.
 *
 * Each entry is backed by an asset-pack Gradle module of the same [packName] (see
 * settings.gradle.kts), whose `src/main/assets/voice/` holds its clips at exactly the paths the app
 * plays them from, and whose `src/main/assets/packs/<packName>` marker tells a debug build (which
 * bundles every pack) that the pack is built in. Adding a pack = one module + one entry here.
 *
 * Which pack a clip lives in is decided by code, [homeOf], never by hand: the build checks every
 * recording is in its home (VoiceScriptTest), so new recordings get placed right.
 *
 * @param title   Shown to the parent.
 * @param folders Folders under `assets/voice/` this pack may hold clips of. Several packs can share
 *                a folder (every English line is in `en/lines`), so this narrows the search only.
 * @param world   For a Pro pack, the world whose Pro activities it voices.
 */
enum class VoicePack(
    val packName: String,
    val title: String,
    val folders: List<String>,
    val delivery: Delivery,
    val world: World? = null,
) {
    HINDI_LETTERS("voice_hi_letters", "Hindi letters", listOf("hi/hindi_vowels", "hi/hindi_consonants"), Delivery.FAST_FOLLOW),
    HINDI_BARAKHADI("voice_hi_barakhadi", "Hindi barakhadi", listOf("hi/barakhadi"), Delivery.FAST_FOLLOW),
    PRO_LETTERS("voice_pro_letters", "Letters", listOf("en/lines", "en/phonemes"), Delivery.ON_DEMAND, World.LETTERS),
    PRO_NUMBERS("voice_pro_numbers", "Numbers", listOf("en/lines"), Delivery.ON_DEMAND, World.NUMBERS),
    PRO_NATURE("voice_pro_nature", "Animals", listOf("en/lines"), Delivery.ON_DEMAND, World.NATURE),
    PRO_MY_WORLD("voice_pro_my_world", "My World", listOf("en/lines"), Delivery.ON_DEMAND, World.MY_WORLD),
    PRO_HINDI("voice_pro_hindi", "Hindi", listOf("hi/barakhadi", "hi/stories"), Delivery.ON_DEMAND, World.HINDI);

    /** Whether a clip of [clip]'s folder could be in this pack (see [folders]). */
    fun mayHold(clip: VoiceClip): Boolean = folders.any { clip.relativePath.startsWith("$it/") }

    companion object {
        fun named(packName: String): VoicePack? = entries.firstOrNull { it.packName == packName }

        val onDemand: List<VoicePack> get() = entries.filter { it.delivery == Delivery.ON_DEMAND }

        /** The pack voicing the Pro activities of [world]; null when they say nothing of their own. */
        fun proPackOf(world: World): VoicePack? = entries.firstOrNull { it.world == world }

        /**
         * The packs a topic can't be used without, Pro or not. Hindi tracing speaks the letter
         * clips; barakhadi speaks syllables and, when a consonant is tapped, the letter clip too.
         * A Pro activity also needs its world's Pro pack.
         */
        fun requiredFor(topic: Topic): Set<VoicePack> = buildSet {
            when {
                topic.kind == TopicKind.BARAKHADI -> addAll(listOf(HINDI_LETTERS, HINDI_BARAKHADI))
                topic.kind == TopicKind.TRACE && topic.set?.script == Script.DEVANAGARI -> add(HINDI_LETTERS)
            }
            if (!ProCatalog.isFree(Lockable.Activity(topic.id))) {
                World.entries.firstOrNull { topic.id in it.topicIds }?.let(::proPackOf)?.let(::add)
            }
        }

        /** The packs [item] can't be opened without. */
        fun requiredFor(item: Lockable): Set<VoicePack> = when (item) {
            is Lockable.Activity -> TOPICS.firstOrNull { it.id == item.topicId }?.let(::requiredFor).orEmpty()
            is Lockable.BarakhadiLetter, is Lockable.Story ->
                if (ProCatalog.isFree(item)) emptySet() else setOf(PRO_HINDI)
        }

        /**
         * The pack [clip] belongs in, or null when it belongs in the app itself.
         * - Hindi letters, and the free Barakhadi letters, go in the fast-follow packs.
         * - Pro Barakhadi letters and Pro stories go in the Hindi Pro pack.
         * - An English clip goes in a world's Pro pack only if Pro activities of that one world
         *   are all that say it ([TopicVoices]); anything a free activity says, or two worlds share,
         *   stays in the app.
         */
        fun homeOf(clip: VoiceClip): VoicePack? {
            val path = clip.relativePath
            return when {
                HINDI_LETTERS.mayHold(clip) -> HINDI_LETTERS
                path.startsWith("hi/barakhadi/") -> {
                    val index = path.removePrefix("hi/barakhadi/").substringBefore('-').toIntOrNull()?.minus(1)
                    if (index != null && !ProCatalog.isFree(Lockable.BarakhadiLetter(index))) PRO_HINDI else HINDI_BARAKHADI
                }
                path.startsWith("hi/stories/") -> {
                    val index = STORIES.indexOfFirst { "story-${it.id}" == clip.key }
                    if (index >= 0 && !ProCatalog.isFree(Lockable.Story(index))) PRO_HINDI else null
                }
                else -> englishHomes[path]
            }
        }

        /** English clip path → its Pro pack, for the clips that have one (see [homeOf]). */
        private val englishHomes: Map<String, VoicePack> by lazy {
            val kept = mutableSetOf<String>()
            val owners = mutableMapOf<String, MutableSet<VoicePack>>()
            TOPICS.forEach { topic ->
                val clips = TopicVoices.clipsFor(topic).map { it.relativePath }
                val pack = World.entries.firstOrNull { topic.id in it.topicIds }?.let(::proPackOf)
                if (ProCatalog.isFree(Lockable.Activity(topic.id)) || pack == null) kept += clips
                else clips.forEach { owners.getOrPut(it) { mutableSetOf() } += pack }
            }
            owners.filterKeys { it !in kept }.filterValues { it.size == 1 }.mapValues { it.value.single() }
        }
    }
}
