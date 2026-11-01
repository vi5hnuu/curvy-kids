package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/** A line of Hindi narration: the recording's [clip], the [text] to read, and where it's heard. */
data class VoiceLine(val clip: VoiceClip, val text: String, val usedIn: String)

/**
 * Every Hindi line the app speaks, each with the key its recording is stored under
 * (`assets/voice/hi/<group>/<key>.m4a`).
 *
 * Letter and barakhadi recordings are numbered in teaching order so the folders read in order:
 * - `hindi_vowels/01-a`, `hindi_consonants/01-ka`: the letter with its word ("क, क से कबूतर")
 * - `barakhadi/01-ka/04-kii`: consonant 1, matra 4 (की)
 * Numbers come from the positions in [HINDI_VOWELS], [HINDI_CONSONANTS] and [BARAKHADI_MATRAS],
 * so those lists' order must not change without renaming the recordings.
 *
 * Screens ask this object for a line's clip instead of building keys themselves, so the recording
 * sheet ([all]) and what the app actually plays can't drift apart. Anything not recorded yet
 * falls back to the device's Hindi voice.
 */
object HindiVoiceScript {

    // Sub-folders of assets/voice/hi/.
    private const val VOWELS = "hindi_vowels"
    private const val CONSONANTS = "hindi_consonants"
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

    /**
     * A letter as it's recorded: the letter with its example word ("क, क से कबूतर"), or the plain
     * letter when it has no word. There is one recording per letter, used both when a letter is
     * named and as the tracing prompt.
     */
    fun letter(l: HindiLetter): VoiceLine {
        val vowel = HINDI_VOWELS.indexOf(l)
        val (group, index) = if (vowel >= 0) VOWELS to vowel else CONSONANTS to HINDI_CONSONANTS.indexOf(l)
        require(index >= 0) { "${l.char} is not a Hindi letter in the course" }
        val text = if (l.example.isEmpty()) l.char else "${l.char}, ${l.char} से ${l.example}"
        return VoiceLine(VoiceClip.hindi(group, numbered(index, l.romanized)), text, "Letter and trace prompt")
    }

    /**
     * One barakhadi form, e.g. की for क + ी, at `barakhadi/01-ka/04-kii`. Its text is the whole
     * recorded line ("की, की से कीड़ा"), so the device voice says the same when a clip is missing.
     */
    fun syllable(consonant: HindiLetter, matra: Matra): VoiceLine {
        val c = HINDI_CONSONANTS.indexOf(consonant)
        val m = BARAKHADI_MATRAS.indexOf(matra)
        require(c >= 0 && m >= 0) { "no barakhadi form for ${consonant.char} + ${matra.key}" }
        val folder = "$BARAKHADI/${numbered(c, consonant.romanized)}"
        return VoiceLine(
            VoiceClip.hindi(folder, numbered(m, consonant.romanized.dropLast(1) + matra.key)),
            barakhadiLine(barakhadiSyllable(consonant.char, matra)),
            "Barakhadi",
        )
    }

    /** "01-ka" for the first item: 1-based, two digits, so files sort in teaching order. */
    private fun numbered(index: Int, name: String) = "%02d-%s".format(index + 1, name)

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
     * A line that would appear in two categories is listed once.
     */
    val sections: List<Pair<String, List<VoiceLine>>> by lazy {
        val seen = mutableSetOf<String>()
        fun List<VoiceLine>.fresh() = filter { seen.add(it.clip.relativePath) }
        buildList {
            add("Hindi vowels (स्वर)" to HINDI_VOWELS.map { letter(it) }.fresh())
            add("Hindi consonants (व्यंजन)" to HINDI_CONSONANTS.map { letter(it) }.fresh())
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
