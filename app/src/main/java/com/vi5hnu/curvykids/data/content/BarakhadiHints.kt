package com.vi5hnu.curvykids.data.content

/**
 * How a barakhadi syllable is explained to the child: exactly what its recording says, so the
 * screen shows the word the child hears. The data lives in [BARAKHADI_HINT_DATA], generated from
 * the narration list by tools/voice/barakhadi_hints.py.
 */
sealed interface SyllableHint {
    /** "का, का से कान": the syllable starts [word]. */
    data class Word(val word: String) : SyllableHint

    /** "खै, ख और ऐ मिलकर बना खै": no everyday word, so the consonant and vowel that make it. */
    data class Joined(val consonant: String, val vowel: String) : SyllableHint

    /** "ङ, ङ जैसे पङ्खा में": the letter only appears inside words, as in [word]. */
    data class AsIn(val word: String) : SyllableHint
}

/** The hint for [syllable], or null for a syllable the narration list does not cover. */
fun barakhadiHint(syllable: String): SyllableHint? = BARAKHADI_HINT_DATA[syllable]

/**
 * The spoken line for [syllable], word for word as recorded ("का, का से कान"), so the device
 * voice says the same thing when a recording is missing. Falls back to the bare syllable.
 */
fun barakhadiLine(syllable: String): String = when (val h = barakhadiHint(syllable)) {
    is SyllableHint.Word -> "$syllable, $syllable से ${h.word}"
    is SyllableHint.Joined -> "$syllable, ${h.consonant} और ${h.vowel} मिलकर बना $syllable"
    is SyllableHint.AsIn -> "$syllable, $syllable जैसे ${h.word} में"
    null -> syllable
}
