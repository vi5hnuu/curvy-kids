package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/** A line of Hindi narration: the recording's [clip], the [text] to read, and where it's heard. */
data class VoiceLine(val clip: VoiceClip, val text: String, val usedIn: String)

/**
 * Every Hindi line the app speaks, each with the key its recording is stored under
 * (`assets/voice/hi/<key>.m4a`).
 *
 * Screens ask this object for a line's clip instead of building keys themselves, so the recording
 * sheet ([all]) and what the app actually plays can't drift apart. Anything not recorded yet
 * falls back to the device's Hindi voice.
 */
object HindiVoiceScript {

    // ── Phrases spoken while tracing Hindi letters ─────────────────────────────

    val PRAISE: List<VoiceLine> = listOf("शाबाश!", "बहुत बढ़िया!", "वाह! कमाल कर दिया!", "बिलकुल सही!")
        .mapIndexed { i, text -> VoiceLine(VoiceClip.hindi("praise-${i + 1}"), text, "Trace: correct") }

    val TRY_AGAIN = VoiceLine(VoiceClip.hindi("try-again"), "फिर से कोशिश करो।", "Trace: wrong")
    val GIVE_UP = VoiceLine(VoiceClip.hindi("give-up"), "कोई बात नहीं! फिर कोशिश करते हैं।", "Trace: out of tries")
    val WRITE_FIRST = VoiceLine(VoiceClip.hindi("write-first"), "पहले लिखो!", "Trace: Check with nothing drawn")

    // ── Letters and barakhadi ──────────────────────────────────────────────────

    /** The letter on its own ("क"). Also the first barakhadi form, which is the same sound. */
    fun letter(l: HindiLetter) = VoiceLine(VoiceClip.hindi("letter-${l.romanized}"), l.char, "Letter name")

    /** The letter with its example word ("क, क से कबूतर"); the plain letter when there's no word. */
    fun letterWithWord(l: HindiLetter): VoiceLine =
        if (l.example.isEmpty()) letter(l)
        else VoiceLine(
            VoiceClip.hindi("letter-${l.romanized}-word"),
            "${l.char}, ${l.char} से ${l.example}",
            "Trace prompt",
        )

    /** One barakhadi form, e.g. की for क + ी. */
    fun syllable(consonant: HindiLetter, matra: Matra): VoiceLine {
        if (matra.sign.isEmpty()) return letter(consonant)
        val key = consonant.romanized.dropLast(1) + matra.key
        return VoiceLine(
            VoiceClip.hindi("syllable-$key"),
            barakhadiSyllable(consonant.char, matra),
            "Barakhadi",
        )
    }

    // ── Stories ────────────────────────────────────────────────────────────────

    /** Line [index] (0-based) of [story]; recorded one sentence per file so reading can highlight it. */
    fun storyLine(story: Story, index: Int) = VoiceLine(
        VoiceClip.hindi("story-${story.id}-line-${index + 1}"),
        story.lines[index],
        "Story: ${story.subtitle}",
    )

    fun storyMoral(story: Story) = VoiceLine(
        VoiceClip.hindi("story-${story.id}-moral"),
        story.moral,
        "Story: ${story.subtitle} (moral)",
    )

    /** The complete recording sheet, in a sensible order to record in. Keys are unique. */
    val all: List<VoiceLine> by lazy {
        buildList {
            HINDI_VOWELS.forEach { add(letter(it)); add(letterWithWord(it)) }
            HINDI_CONSONANTS.forEach { add(letter(it)); add(letterWithWord(it)) }
            HINDI_CONSONANTS.forEach { c -> BARAKHADI_MATRAS.forEach { m -> add(syllable(c, m)) } }
            addAll(PRAISE); add(TRY_AGAIN); add(GIVE_UP); add(WRITE_FIRST)
            STORIES.forEach { s -> s.lines.indices.forEach { add(storyLine(s, it)) }; add(storyMoral(s)) }
        }.distinctBy { it.clip.key }
    }
}
