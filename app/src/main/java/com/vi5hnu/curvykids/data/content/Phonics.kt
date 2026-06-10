package com.vi5hnu.curvykids.data.content

/**
 * Maps each character to a child-friendly example word, used to build the phonics phrase
 * spoken via Text-To-Speech (e.g. "A for Apple", "1 is One"). Kept as a simple static
 * dictionary so no audio assets are required.
 */
object Phonics {

    private val letterWords = mapOf(
        'a' to "Apple", 'b' to "Ball", 'c' to "Cat", 'd' to "Dog", 'e' to "Elephant",
        'f' to "Fish", 'g' to "Goat", 'h' to "Hat", 'i' to "Igloo", 'j' to "Jug",
        'k' to "Kite", 'l' to "Lion", 'm' to "Monkey", 'n' to "Nest", 'o' to "Orange",
        'p' to "Parrot", 'q' to "Queen", 'r' to "Rabbit", 's' to "Sun", 't' to "Tiger",
        'u' to "Umbrella", 'v' to "Van", 'w' to "Watch", 'x' to "Xylophone", 'y' to "Yak",
        'z' to "Zebra",
    )

    private val numberWords = mapOf(
        '0' to "Zero", '1' to "One", '2' to "Two", '3' to "Three", '4' to "Four",
        '5' to "Five", '6' to "Six", '7' to "Seven", '8' to "Eight", '9' to "Nine",
    )

    /** Builds the spoken phrase for [character], or just the character if unmapped. */
    fun phraseFor(character: String): String {
        val c = character.singleOrNull() ?: return character
        return when {
            c.isLetter() -> letterWords[c.lowercaseChar()]?.let { "$character for $it" } ?: character
            c.isDigit() -> numberWords[c]?.let { "$character is $it" } ?: character
            else -> character
        }
    }
}
