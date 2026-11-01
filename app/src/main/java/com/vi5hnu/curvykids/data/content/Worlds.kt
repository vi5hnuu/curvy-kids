package com.vi5hnu.curvykids.data.content

import androidx.compose.ui.graphics.Color
import com.vi5hnu.curvykids.ui.theme.KidBerry
import com.vi5hnu.curvykids.ui.theme.KidGrape
import com.vi5hnu.curvykids.ui.theme.KidGrass
import com.vi5hnu.curvykids.ui.theme.KidOrange
import com.vi5hnu.curvykids.ui.theme.KidSky
import com.vi5hnu.curvykids.ui.theme.KidSun

/**
 * What a tile shows: a picture; big characters with a picture of a word they start
 * ([LetterPicture], "A" with an apple), for topics about characters; or bare characters
 * ([Glyph]) where no picture fits.
 *
 * Characters on their own don't hold a small child's interest, and a letter learned with a
 * picture of its word is remembered better, so character topics use [LetterPicture].
 */
sealed interface TopicArt {
    data class Picture(val asset: AssetPath) : TopicArt
    data class Glyph(val text: String) : TopicArt
    data class LetterPicture(val text: String, val asset: AssetPath) : TopicArt
}

/** [text] with the picture of [letter]'s example word ("A" → apple, "क" → pigeon). */
private fun lettered(text: String, letter: String): TopicArt =
    letterExample(letter)?.let { TopicArt.LetterPicture(text, it.picture) } ?: TopicArt.Glyph(text)

/**
 * The six places a child chooses between on Home. Every topic belongs to exactly one, which is
 * the only grouping the child sees (a unit test checks nothing is missing or listed twice).
 *
 * Six big, differently coloured pictures is a choice a pre-reader can make; 45 activities under
 * written headings wasn't.
 */
enum class World(
    val id: String,
    val title: String,
    val color: Color,
    val art: TopicArt,
    val topicIds: List<String>,
) {
    LETTERS("letters", "Letters", KidBerry, lettered("ABC", "A"),
        listOf("upper", "lower", "phonics", "words", "rhyming", "opposites")),
    NUMBERS("numbers", "Numbers", KidSky, TopicArt.LetterPicture("123", KidAssets.counting(3)),
        listOf("numbers", "count", "addition", "subtraction", "shapes", "patterns", "bigsmall", "fractions", "time")),
    HINDI("hindi", "Hindi", KidOrange, lettered("अ आ", "अ"),
        listOf("hindi_vowels", "hindi_consonants", "barakhadi", "stories")),
    NATURE("nature", "Animals", KidGrass, TopicArt.Picture(KidAssets.animal("lion")),
        listOf("animals", "birds", "sea", "insects", "nature", "weather", "seasons", "fruitsveggies")),
    MY_WORLD("my_world", "My World", KidGrape, TopicArt.Picture(KidAssets.place("house")),
        listOf("body", "emotions", "colors", "food", "clothes", "toys", "instruments", "home", "jobs",
            "places", "sports", "vehicles", "days", "months")),
    GAMES("games", "Games", KidSun, TopicArt.Picture(KidAssets.toy("kite")),
        listOf("memory", "oddone", "catch", "draw"));

    val topics: List<Topic> get() = topicIds.map { id -> TOPICS.first { it.id == id } }

    companion object {
        fun byId(id: String): World? = entries.firstOrNull { it.id == id }
        fun of(topic: Topic): World = entries.first { topic.id in it.topicIds }
    }
}

/** The picture on each activity's tile: characters with a word picture for character topics, pictures for the rest. */
val TOPIC_ART: Map<String, TopicArt> = mapOf(
    // Letters
    "upper" to lettered("ABC", "A"),
    "lower" to lettered("abc", "b"),
    "phonics" to TopicArt.Picture(KidAssets.instrument("microphone")),
    "words" to TopicArt.Picture(KidAssets.fruit("apple")),
    "rhyming" to TopicArt.Picture(KidAssets.animal("cat")),
    "opposites" to TopicArt.Picture(KidAssets.weather("fire")),
    // Numbers
    "numbers" to TopicArt.LetterPicture("123", KidAssets.counting(3)),
    "count" to TopicArt.Picture(KidAssets.counting(3)),
    "addition" to TopicArt.LetterPicture("1+2", KidAssets.counting(3)),
    "subtraction" to TopicArt.LetterPicture("3−1", KidAssets.counting(2)),
    "shapes" to TopicArt.Picture(KidAssets.shape("star")),
    "patterns" to TopicArt.Picture(KidAssets.pattern("dots")),
    "bigsmall" to TopicArt.Picture(KidAssets.animal("elephant")),
    "fractions" to TopicArt.Picture(KidAssets.fraction("1-2")),
    "time" to TopicArt.Picture(KidAssets.time(3)),
    // Hindi
    "hindi_vowels" to lettered("अ", "अ"),
    "hindi_consonants" to lettered("क", "क"),
    "barakhadi" to (hindiWordPicture("कान")?.let { TopicArt.LetterPicture("का", it) } ?: TopicArt.Glyph("का")),
    "stories" to TopicArt.Picture(KidAssets.bird("blackbird")),
    // Animals & nature
    "animals" to TopicArt.Picture(KidAssets.animal("lion")),
    "birds" to TopicArt.Picture(KidAssets.bird("parrot")),
    "sea" to TopicArt.Picture(KidAssets.seaAnimal("dolphin")),
    "insects" to TopicArt.Picture(KidAssets.insect("butterfly")),
    "nature" to TopicArt.Picture(KidAssets.nature("blossom")),
    "weather" to TopicArt.Picture(KidAssets.weather("partly-cloudy")),
    "seasons" to TopicArt.Picture(KidAssets.nature("maple-leaf")),
    "fruitsveggies" to TopicArt.Picture(KidAssets.fruit("banana")),
    // My world
    "body" to TopicArt.Picture(KidAssets.body("hand")),
    "emotions" to TopicArt.Picture(KidAssets.feeling("happy")),
    "colors" to TopicArt.Picture(KidAssets.weather("rainbow")),
    "food" to TopicArt.Picture(KidAssets.food("burger")),
    "clothes" to TopicArt.Picture(KidAssets.clothes("cap")),
    "toys" to TopicArt.Picture(KidAssets.toy("teddy-bear")),
    "instruments" to TopicArt.Picture(KidAssets.instrument("guitar")),
    "home" to TopicArt.Picture(KidAssets.place("house")),
    "jobs" to TopicArt.Picture(KidAssets.job("firefighter")),
    "places" to TopicArt.Picture(KidAssets.place("castle")),
    "sports" to TopicArt.Picture(KidAssets.sport("football")),
    "vehicles" to TopicArt.Picture(KidAssets.transport("bus")),
    "days" to TopicArt.Picture(KidAssets.home("clock")),
    "months" to TopicArt.Picture(KidAssets.toy("gift")),
    // Games
    "memory" to TopicArt.Picture(KidAssets.toy("dice")),
    "oddone" to TopicArt.Picture(KidAssets.fruit("green-apple")),
    "catch" to TopicArt.Picture(KidAssets.bird("owl")),
    "draw" to TopicArt.Picture(KidAssets.job("artist")),
)

fun Topic.art(): TopicArt = TOPIC_ART[id] ?: TopicArt.Glyph(glyph)
