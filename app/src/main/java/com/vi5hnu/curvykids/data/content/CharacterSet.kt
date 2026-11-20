package com.vi5hnu.curvykids.data.content

/** Writing system a [Level] belongs to — drives recognizer model + match leniency. */
enum class Script { LATIN, DEVANAGARI }

/** The learning tracks the child can switch between. */
enum class Level(
    val label: String,
    /**
     * Stable prefix under which this track's mastered characters are persisted, e.g. "upper:A".
     * Matches the corresponding [Topic] id — kept on the level itself so game code never has to
     * reverse-look-up a topic to find out where to save progress, and so the strings that are
     * already on real devices can never drift.
     */
    val progressKey: String,
    /** BCP-47 tag of the ML Kit Digital Ink model used to recognise this track. */
    val languageTag: String = "en-US",
    val script: Script = Script.LATIN,
) {
    UPPERCASE("ABC", progressKey = "upper"),
    LOWERCASE("abc", progressKey = "lower"),
    NUMBERS("123", progressKey = "numbers"),
    HINDI_VOWELS("स्वर", progressKey = "hindi_vowels", languageTag = "hi", script = Script.DEVANAGARI),
    HINDI_CONSONANTS("व्यंजन", progressKey = "hindi_consonants", languageTag = "hi", script = Script.DEVANAGARI);

    /** Ordered characters that make up this level. Computed once per enum instance. */
    val characters: List<String> by lazy {
        when (this) {
            UPPERCASE -> ('A'..'Z').map(Char::toString)
            LOWERCASE -> ('a'..'z').map(Char::toString)
            NUMBERS -> ('0'..'9').map(Char::toString)
            // FQN disambiguates the top-level letter lists from the same-named enum entries.
            HINDI_VOWELS -> com.vi5hnu.curvykids.data.content.HINDI_VOWELS.map { it.char }
            HINDI_CONSONANTS -> com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS.map { it.char }
        }
    }
}
