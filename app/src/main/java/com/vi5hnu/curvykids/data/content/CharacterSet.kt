package com.vi5hnu.curvykids.data.content

/** Writing system a [Level] belongs to — drives recognizer model + match leniency. */
enum class Script { LATIN, DEVANAGARI }

/** The learning tracks the child can switch between. */
enum class Level(
    val label: String,
    /** BCP-47 tag of the ML Kit Digital Ink model used to recognise this track. */
    val languageTag: String = "en-US",
    val script: Script = Script.LATIN,
) {
    UPPERCASE("ABC"),
    LOWERCASE("abc"),
    NUMBERS("123"),
    HINDI_VOWELS("स्वर", languageTag = "hi", script = Script.DEVANAGARI),
    HINDI_CONSONANTS("व्यंजन", languageTag = "hi", script = Script.DEVANAGARI);

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
