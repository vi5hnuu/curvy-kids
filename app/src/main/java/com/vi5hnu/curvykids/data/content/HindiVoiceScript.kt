package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/** A line of Hindi narration: the recording's [clip], the [text] to read, and where it's heard. */
data class VoiceLine(val clip: VoiceClip, val text: String, val usedIn: String)

/**
 * Every Hindi line the app speaks, each with the key its recording is stored under
 * (`assets/voice/hi/<group>/<key>.m4a`).
 *
 * Screens ask this object for a line's clip instead of building keys themselves, so the recording
 * sheet ([all]) and what the app actually plays can't drift apart. Anything not recorded yet
 * falls back to the device's Hindi voice.
 */
object HindiVoiceScript {

    // Sub-folders of assets/voice/hi/.
    private const val LETTERS = "letters"
    private const val BARAKHADI = "barakhadi"
    private const val PHRASES = "phrases"
    private const val STORY_FOLDER = "stories"

    // ── Phrases spoken while tracing Hindi letters ─────────────────────────────

    val PRAISE: List<VoiceLine> = listOf("शाबाश!", "बहुत बढ़िया!", "वाह! कमाल कर दिया!", "बिलकुल सही!")
        .mapIndexed { i, text -> VoiceLine(VoiceClip.hindi(PHRASES, "praise-${i + 1}"), text, "Trace: correct") }

    val TRY_AGAIN = VoiceLine(VoiceClip.hindi(PHRASES, "try-again"), "फिर से कोशिश करो।", "Trace: wrong")
    val GIVE_UP = VoiceLine(VoiceClip.hindi(PHRASES, "give-up"), "कोई बात नहीं! फिर कोशिश करते हैं।", "Trace: out of tries")
    val WRITE_FIRST = VoiceLine(VoiceClip.hindi(PHRASES, "write-first"), "पहले लिखो!", "Trace: Check with nothing drawn")

    // ── Letters and barakhadi ──────────────────────────────────────────────────

    /** The letter on its own ("क"). Also the first barakhadi form, which is the same sound. */
    fun letter(l: HindiLetter) = VoiceLine(VoiceClip.hindi(LETTERS, "letter-${l.romanized}"), l.char, "Letter name")

    /** The letter with its example word ("क, क से कबूतर"); the plain letter when there's no word. */
    fun letterWithWord(l: HindiLetter): VoiceLine =
        if (l.example.isEmpty()) letter(l)
        else VoiceLine(
            VoiceClip.hindi(LETTERS, "letter-${l.romanized}-word"),
            "${l.char}, ${l.char} से ${l.example}",
            "Trace prompt",
        )

    /** One barakhadi form, e.g. की for क + ी. */
    fun syllable(consonant: HindiLetter, matra: Matra): VoiceLine {
        if (matra.sign.isEmpty()) return letter(consonant)
        val key = consonant.romanized.dropLast(1) + matra.key
        return VoiceLine(
            VoiceClip.hindi(BARAKHADI, "syllable-$key"),
            barakhadiSyllable(consonant.char, matra),
            "Barakhadi",
        )
    }

    // ── Stories ────────────────────────────────────────────────────────────────

    /** Line [index] (0-based) of [story]; recorded one sentence per file so reading can highlight it. */
    fun storyLine(story: Story, index: Int) = VoiceLine(
        VoiceClip.hindi(STORY_FOLDER, "story-${story.id}-line-${index + 1}"),
        story.lines[index],
        "Story: ${story.subtitle}",
    )

    fun storyMoral(story: Story) = VoiceLine(
        VoiceClip.hindi(STORY_FOLDER, "story-${story.id}-moral"),
        story.moral,
        "Story: ${story.subtitle} (moral)",
    )

    /**
     * The recording sheet, grouped by category in the order a voice artist would record it.
     * A line that appears in two categories (a letter with no example word) is listed once.
     */
    val sections: List<Pair<String, List<VoiceLine>>> by lazy {
        val seen = mutableSetOf<String>()
        fun List<VoiceLine>.fresh() = filter { seen.add(it.clip.key) }
        buildList {
            add("Hindi vowels (स्वर)" to HINDI_VOWELS.map { letter(it) }.fresh())
            add("Hindi consonants (व्यंजन)" to HINDI_CONSONANTS.map { letter(it) }.fresh())
            add("Hindi vowels with example word" to HINDI_VOWELS.map { letterWithWord(it) }.fresh())
            add("Hindi consonants with example word" to HINDI_CONSONANTS.map { letterWithWord(it) }.fresh())
            HINDI_CONSONANTS.forEach { c ->
                add("Barakhadi - ${c.char}" to BARAKHADI_MATRAS.map { syllable(c, it) }.fresh())
            }
            add("Praise and encouragement" to (PRAISE + TRY_AGAIN + GIVE_UP + WRITE_FIRST).fresh())
            STORIES.forEach { st ->
                add("Story - ${st.title} (${st.subtitle})" to
                    (st.lines.indices.map { storyLine(st, it) } + storyMoral(st)).fresh())
            }
        }.filter { it.second.isNotEmpty() }
    }

    /** Every line, once each. */
    val all: List<VoiceLine> by lazy { sections.flatMap { it.second } }
}
