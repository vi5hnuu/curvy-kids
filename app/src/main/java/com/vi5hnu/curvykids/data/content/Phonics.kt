package com.vi5hnu.curvykids.data.content

/**
 * Maps each character to a child-friendly example word and its illustration, used to build the
 * phonics phrase spoken via Text-To-Speech ("A for Apple", "1 is One") and the picture shown
 * beside the letter on the trace screen.
 *
 * Example words are chosen so every one has artwork in the asset library; a few differ from the
 * usual primer choices (Nest, Xylophone, Yak) because those have no illustration, and a letter
 * with no picture is no help to a child who cannot read the word yet.
 */
object Phonics {

    private val letterWords = mapOf(
        'a' to ("Apple" to KidAssets.fruit("apple")),
        'b' to ("Ball" to KidAssets.sport("soccer")),
        'c' to ("Cat" to KidAssets.animal("cat")),
        'd' to ("Dog" to KidAssets.animal("dog")),
        'e' to ("Egg" to KidAssets.food("egg")),
        'f' to ("Fish" to KidAssets.seaAnimal("fish")),
        'g' to ("Goat" to KidAssets.animal("goat")),
        'h' to ("Hat" to KidAssets.clothes("top-hat")),
        'i' to ("Insect" to KidAssets.insect("ladybug")),
        'j' to ("Jet" to KidAssets.transport("airplane")),
        'k' to ("Kite" to KidAssets.toy("kite")),
        'l' to ("Lion" to KidAssets.animal("lion")),
        'm' to ("Moon" to KidAssets.weather("moon")),
        'n' to ("Nose" to KidAssets.body("nose")),
        'o' to ("Orange" to KidAssets.fruit("orange")),
        'p' to ("Parrot" to KidAssets.bird("parrot")),
        'q' to ("Queen" to KidAssets.clothes("crown")),
        'r' to ("Rabbit" to KidAssets.animal("rabbit")),
        's' to ("Sun" to KidAssets.weather("sun")),
        't' to ("Tiger" to KidAssets.animal("tiger")),
        'u' to ("Umbrella" to KidAssets.weather("umbrella")),
        'v' to ("Violin" to KidAssets.instrument("violin")),
        'w' to ("Wolf" to KidAssets.animal("wolf")),
        'x' to ("Box" to KidAssets.toy("gift")),
        'y' to ("Yo-yo" to KidAssets.toy("yo-yo")),
        'z' to ("Zebra" to KidAssets.animal("zebra")),
    )

    private val numberWords = mapOf(
        '0' to ("Zero" to KidAssets.number(0)),
        '1' to ("One" to KidAssets.number(1)),
        '2' to ("Two" to KidAssets.number(2)),
        '3' to ("Three" to KidAssets.number(3)),
        '4' to ("Four" to KidAssets.number(4)),
        '5' to ("Five" to KidAssets.number(5)),
        '6' to ("Six" to KidAssets.number(6)),
        '7' to ("Seven" to KidAssets.number(7)),
        '8' to ("Eight" to KidAssets.number(8)),
        '9' to ("Nine" to KidAssets.number(9)),
    )

    private fun entryFor(character: String): Pair<String, AssetPath>? {
        val c = character.singleOrNull() ?: return null
        return when {
            c.isLetter() -> letterWords[c.lowercaseChar()]
            c.isDigit() -> numberWords[c]
            else -> null
        }
    }

    /** Builds the spoken phrase for [character], or just the character if unmapped. */
    fun phraseFor(character: String): String {
        val c = character.singleOrNull() ?: return character
        val word = entryFor(character)?.first ?: return character
        return if (c.isDigit()) "$character is $word" else "$character for $word"
    }

    /** The example/spoken word alone (e.g. "Apple", "Five"), or null if unmapped. */
    fun wordFor(character: String): String? = entryFor(character)?.first

    /** Asset path of the picture for [character]'s example word, or null if unmapped. */
    fun assetFor(character: String): AssetPath? = entryFor(character)?.second
}
