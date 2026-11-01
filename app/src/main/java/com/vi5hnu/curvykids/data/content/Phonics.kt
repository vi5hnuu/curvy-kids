package com.vi5hnu.curvykids.data.content

/**
 * The phrase spoken for a letter or digit on the trace screen ("A for Apple", "1 is One") and the
 * picture beside it.
 *
 * Letters come from [letterExample] (that is, [PHONICS_DATA]), the same list the Phonics game and
 * the letter pictures use, so what the child sees is always what the voice says.
 */
object Phonics {

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
            c.isDigit() -> numberWords[c]
            c.isLatin() -> letterExample(character)?.let { it.word to it.picture }
            else -> null
        }
    }

    /**
     * Builds the spoken phrase for [character]: "1 is One", "A for Apple", or "X, as in Fox" for
     * a letter taught by its ending sound. Just the character if unmapped.
     */
    fun phraseFor(character: String): String {
        val c = character.singleOrNull() ?: return character
        if (c.isDigit()) return numberWords[c]?.let { "$character is ${it.first}" } ?: character
        val ex = letterExample(character)?.takeIf { c.isLatin() } ?: return character
        return if (ex.position == SoundPosition.END) "$character, as in ${ex.word}" else "$character for ${ex.word}"
    }

    /** The example/spoken word alone (e.g. "Apple", "Five"), or null if unmapped. */
    fun wordFor(character: String): String? = entryFor(character)?.first

    /** Asset path of the picture for [character]'s example word, or null if unmapped. */
    fun assetFor(character: String): AssetPath? = entryFor(character)?.second

    /** Hindi letters have their own spoken line (HindiVoiceScript), never an English phrase. */
    private fun Char.isLatin() = this in 'a'..'z' || this in 'A'..'Z'
}
