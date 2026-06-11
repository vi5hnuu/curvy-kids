package com.vi5hnu.curvykids.data.content

/**
 * Maps each character to a child-friendly example word + emoji, used to build the phonics phrase
 * spoken via Text-To-Speech (e.g. "A for Apple", "1 is One"). Kept as a simple static
 * dictionary so no audio assets are required.
 */
object Phonics {

    private val letterWords = mapOf(
        'a' to ("Apple" to "🍎"), 'b' to ("Ball" to "⚽"), 'c' to ("Cat" to "🐱"),
        'd' to ("Dog" to "🐶"), 'e' to ("Egg" to "🥚"), 'f' to ("Fish" to "🐟"),
        'g' to ("Goat" to "🐐"), 'h' to ("Hat" to "🎩"), 'i' to ("Ice" to "🧊"),
        'j' to ("Jug" to "🫙"), 'k' to ("Kite" to "🪁"), 'l' to ("Lion" to "🦁"),
        'm' to ("Moon" to "🌙"), 'n' to ("Nest" to "🪺"), 'o' to ("Orange" to "🍊"),
        'p' to ("Parrot" to "🦜"), 'q' to ("Queen" to "👑"), 'r' to ("Rabbit" to "🐰"),
        's' to ("Sun" to "☀️"), 't' to ("Tiger" to "🐯"), 'u' to ("Umbrella" to "☂️"),
        'v' to ("Van" to "🚐"), 'w' to ("Watch" to "⌚"), 'x' to ("Xylophone" to "🎹"),
        'y' to ("Yak" to "🐂"), 'z' to ("Zebra" to "🦓"),
    )

    private val numberWords = mapOf(
        '0' to ("Zero" to "⭕"), '1' to ("One" to "☝️"), '2' to ("Two" to "✌️"),
        '3' to ("Three" to "🐦"), '4' to ("Four" to "🍀"), '5' to ("Five" to "🖐️"),
        '6' to ("Six" to "🎲"), '7' to ("Seven" to "🌈"), '8' to ("Eight" to "🐙"),
        '9' to ("Nine" to "🎈"),
    )

    /** Builds the spoken phrase for [character], or just the character if unmapped. */
    fun phraseFor(character: String): String {
        val c = character.singleOrNull() ?: return character
        return when {
            c.isLetter() -> letterWords[c.lowercaseChar()]?.first?.let { "$character for $it" } ?: character
            c.isDigit() -> numberWords[c]?.first?.let { "$character is $it" } ?: character
            else -> character
        }
    }

    /** The example/spoken word alone (e.g. "Apple", "Five"), or null if unmapped. */
    fun wordFor(character: String): String? {
        val c = character.singleOrNull() ?: return null
        return when {
            c.isLetter() -> letterWords[c.lowercaseChar()]?.first
            c.isDigit() -> numberWords[c]?.first
            else -> null
        }
    }

    /** The emoji for [character]'s example word (e.g. "🍎" for A), or null if unmapped. */
    fun emojiFor(character: String): String? {
        val c = character.singleOrNull() ?: return null
        return when {
            c.isLetter() -> letterWords[c.lowercaseChar()]?.second
            c.isDigit() -> numberWords[c]?.second
            else -> null
        }
    }
}
