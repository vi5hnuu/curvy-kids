package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/**
 * The English recordings each activity speaks that are its own content: the names, sounds and
 * sentences built from its content list (months, vehicles, opposites…), from the same builders
 * its screen calls. Used to decide which recordings can be left out of the app and downloaded
 * only when a Pro activity is first opened (see [com.vi5hnu.curvykids.audio.packs.VoicePack.homeOf]).
 *
 * Lines every part of the app shares ([shared]: names of worlds and activities, tracing,
 * numbers and letters, "Yes!", "Find the"…) are never counted as one activity's own, so they
 * always stay in the app. Listing too little here is harmless (the clip stays in the app); a
 * line a free activity says must be listed under that activity, which keeps it in the app too.
 */
object TopicVoices {

    private fun lines(texts: Iterable<String>) = texts.map { EnglishVoiceScript.line(it).clip }
    private fun parts(speech: Iterable<Speech>) = speech.flatMap { s -> s.parts.map { it.clip } }

    /** Recordings shared by the whole app, never one activity's own. */
    val shared: Set<VoiceClip> by lazy {
        val generalSections = setOf("App and worlds", "Tracing", "Numbers and letters")
        val sections = EnglishVoiceScript.sections.filter { it.first in generalSections }.flatMap { it.second }
        (sections.map { it.clip } +
            lines(EnglishVoiceScript.QUIZ_LINES) +
            lines(listOf(EnglishVoiceScript.TRY_AGAIN, EnglishVoiceScript.ALL_DONE, EnglishVoiceScript.RUBBED_OUT,
                EnglishVoiceScript.NEW_PAGE, EnglishVoiceScript.SAVED)) +
            // The sentence words every quiz uses: "Yes!", "This one is", "Find the", "Find", "Tap".
            parts(listOf(EnglishVoiceScript.yes())) +
            listOf(
                EnglishVoiceScript.thisOneIs("x"), EnglishVoiceScript.findThe("x"),
                EnglishVoiceScript.find("x"), EnglishVoiceScript.tap(1),
            ).map { it.parts.first().clip }).toSet()
    }

    /** The recordings that are [topic]'s own content (before [shared] is taken out). */
    fun clipsFor(topic: Topic): List<VoiceClip> = with(EnglishVoiceScript) {
        when (topic.kind) {
            // Free activities: listed so that what they say always stays in the app.
            TopicKind.SHAPES -> lines(SHAPES.map { it.name })
            TopicKind.COLORS -> lines(COLOR_ITEMS.map { it.name })
            TopicKind.ANIMALS -> lines(ANIMALS.flatMap { listOf(it.name, it.sound, whoSays(it)) })
            TopicKind.BODY -> lines(BODY_PARTS.map { it.name })
            TopicKind.DRAW -> lines(CRAYON_BOX.map { crayon(it.name) })
            TopicKind.MEMORY, TopicKind.PATTERNS, TopicKind.ODDONE, TopicKind.BIGSMALL ->
                lines(GAME_PICTURES.map(::pictureName) + listOf("the big one", "the small one"))
            // Pro activities.
            TopicKind.PHONICS -> PHONICS_DATA.map { it.clip } + lines(PHONICS_DATA.map(::soundHint))
            TopicKind.WORDS -> lines(WORD_LIST.map { it.word.lowercase().replaceFirstChar(Char::uppercase) })
            TopicKind.RHYMING -> parts(RHYME_GROUPS.flatMap { g ->
                val options = g.rhymes.map { it.first.lowercase() }
                listOf(rhymesWith(g.word.lowercase(), options)) + options.map { yesRhyme(g.word.lowercase(), it) }
            })
            TopicKind.OPPOSITES -> lines(OPPOSITES.flatMap {
                listOf(it.word1, it.word2, opposite(it.word1), opposite(it.word2))
            })
            TopicKind.ADDITION -> parts(listOf(addition(1, 2)))
            TopicKind.SUBTRACTION -> parts(listOf(subtraction(3, 1), yesLeft(2)))
            TopicKind.FRACTIONS -> lines(FRACTIONS.map { it.spoken })
            TopicKind.TIME -> lines(TIMES.map { it.label })
            TopicKind.SEASONS -> lines(SEASONS_LIST.flatMap { listOf(it.name, seasonCall(it), whichSeason(it)) })
            TopicKind.FRUITSVEGGIES -> lines(FRUITS_VEGGIES.flatMap { listOf(it.name, fruitOrVegetable(it.name)) }) +
                parts(listOf(yesFruit("x", true), yesFruit("x", false))) + lines(listOf("a fruit", "a vegetable"))
            TopicKind.EMOTIONS -> lines(EMOTIONS.map { it.name })
            TopicKind.VEHICLES -> lines(VEHICLES.flatMap { listOf(it.name, it.sound) })
            TopicKind.DAYS -> lines(DAYS + DAYS.map(::whatComesAfter))
            TopicKind.MONTHS -> lines(MONTHS.flatMap { listOf(it.name, whatComesAfter(it.name)) })
            TopicKind.PICTURE -> lines(PICTURE_SPECS[topic.id]?.items.orEmpty().map { it.name })
            // Numbers, letters and Hindi: everything they say in English is shared.
            TopicKind.TRACE, TopicKind.COUNT, TopicKind.CATCH, TopicKind.BARAKHADI, TopicKind.STORIES -> emptyList()
        }.filterNot { it in shared }
    }
}
