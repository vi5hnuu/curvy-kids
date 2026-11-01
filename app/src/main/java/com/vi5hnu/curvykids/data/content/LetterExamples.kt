package com.vi5hnu.curvykids.data.content

/**
 * The example word for a letter, as the child hears it: "A for Apple", "क से कबूतर".
 *
 * One place for every letter screen, built from [PHONICS_DATA] (English) and [HINDI_VOWELS] /
 * [HINDI_CONSONANTS] (Hindi), so the picture shown, the word marked and the line spoken can never
 * disagree.
 *
 * @property mark the part of [word] to highlight: the letter where it sits in the word, widened
 *           to whole syllables (see [syllableSpan]) so Devanagari is never split mid-cluster.
 */
data class LetterExample(
    val letter: String,
    val word: String,
    val picture: AssetPath,
    val mark: IntRange,
    val position: SoundPosition = SoundPosition.START,
)

private val ENGLISH: Map<Char, LetterPhonic> = PHONICS_DATA.associateBy { it.letter.single().lowercaseChar() }

/** The example for [letter] (any case, or a Hindi letter), or null for one without a word. */
fun letterExample(letter: String): LetterExample? {
    letter.singleOrNull()?.takeIf { it in 'a'..'z' || it in 'A'..'Z' }?.let { c ->
        val p = ENGLISH[c.lowercaseChar()] ?: return null
        val at = if (p.position == SoundPosition.END) p.example.lastIndexOf(c.lowercaseChar(), ignoreCase = true) else 0
        return LetterExample(letter, p.example, p.svg, at..at, p.position)
    }
    val h = HINDI_BY_CHAR[letter]?.takeIf { it.example.isNotEmpty() } ?: return null
    val at = h.example.indexOf(h.char)
    if (at < 0) return null
    return LetterExample(letter, h.example, h.svg, syllableSpan(h.example, at, h.char.length))
}

private const val VIRAMA = '्'

/**
 * Widens [start] until [length] chars of [word] to whole written syllables: following vowel signs
 * and nasal marks are pulled in, and a virama pulls in the consonant it joins. So marking ग in
 * गाय marks "गा", and ङ in गङ्गा marks "ङ्गा"; a highlight never cuts a cluster in half.
 */
fun syllableSpan(word: String, start: Int, length: Int): IntRange {
    var end = start + length // exclusive
    while (end < word.length) {
        val c = word[end]
        val prev = word[end - 1]
        end += when {
            prev == VIRAMA -> 1                          // the consonant after a virama
            Character.getType(c) == Character.NON_SPACING_MARK.toInt() ||
                Character.getType(c) == Character.COMBINING_SPACING_MARK.toInt() -> 1
            else -> break
        }
    }
    return start until end
}
