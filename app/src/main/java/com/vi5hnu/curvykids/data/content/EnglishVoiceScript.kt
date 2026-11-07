package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/**
 * One English sentence as the recordings it is made of, in order.
 *
 * Sentences whose words change from round to round ("Find the" + any of 300 pictures, "3 plus 4")
 * can't each have their own recording, so they are said as parts; a fixed sentence is one part.
 */
data class Speech(val parts: List<VoiceLine>) {
    /** The whole sentence, for the device voice when a part has no recording yet. */
    val text: String get() = parts.joinToString(" ") { it.text }

    /**
     * Whether every part has a recording, by [isRecorded]: only then is the sentence played from
     * recordings; otherwise the device voice says all of it, so it never switches voice halfway.
     */
    fun fullyRecorded(isRecorded: (VoiceClip) -> Boolean): Boolean =
        parts.isNotEmpty() && parts.all { isRecorded(it.clip) }
}

/**
 * Every English line the app speaks, each with the key its recording is stored under
 * (`assets/voice/en/lines/<slug>.m4a`), the English counterpart of [HindiVoiceScript].
 *
 * A line's key comes from its text ("Find the pairs!" -> `find-the-pairs`), so a new recording
 * needs no code change, and two spellings of the same words ("Try again", "Try again!") share one.
 *
 * Screens speak English only through these builders and `PhonicsSpeaker.say`, never by building
 * strings themselves; [sections] lists every part the builders can produce, for the recording
 * list. A unit test checks that every builder's parts are listed, so the two can't drift.
 */
object EnglishVoiceScript {

    private const val LINES = "lines"

    /** "Find the pairs!" -> "find-the-pairs": lowercase letters and digits joined by hyphens. */
    fun slug(text: String): String =
        text.lowercase().replace(Regex("['’]"), "").replace(Regex("[^a-z0-9]+"), "-").trim('-').ifEmpty { "blank" }

    /** The recording for exactly [text]. */
    fun line(text: String): VoiceLine = VoiceLine(VoiceClip.english(LINES, slug(text)), text, "English")

    private fun speech(vararg parts: String) = Speech(parts.map(::line))

    // ── Fixed lines ──────────────────────────────────────────────────────────────

    const val GREETING = "Hi! Where shall we play?"
    const val MY_STICKERS = "My stickers"
    const val TRY_AGAIN = "Try again!"
    const val ALL_DONE = "All done! Great job!"
    const val GET_VOICES = "Let's get the voices! Ask a grown-up to help."
    const val GETTING_VOICES = "Getting your voices ready!"

    /** Tracing (GameViewModel). */
    val TRACE_PRAISE = listOf("Yay! Great job!", "Awesome!", "Well done!", "Perfect!", "You did it!")
    const val DRAW_FIRST = "Draw it first, then tap check."
    const val HINDI_GIVE_UP = "That's okay! Let's try the next one."
    private const val KEEP_PRACTICING = "That's OK! Keep practicing."
    private const val MOVING_ON = "Moving on!"
    private const val COULD_NOT_SEE = "Hmm, I couldn't see it. Try drawing a bit bigger."
    private const val ALMOST = "Almost! Let's try again."

    /** Quiz prompts and praise that never change. */
    val QUIZ_LINES = listOf(
        "Which letter makes this sound?", "Which is bigger?", "Which is smaller?",
        "Which one is different?", "Which is NOT an animal?", "Which is NOT food?", "Tap the small one!",
        "Yes! That's the different one!", "What comes next?", "Yes! That comes next!",
        "Find the pairs!", "Match!", "Count again", "Look at the clock. What time is it?",
        "Which fraction is shaded?",
    )

    /** Words said when a drawing tool is tapped (ToolVoice). */
    const val RUBBED_OUT = "Rubbed out!"
    const val NEW_PAGE = "New page!"
    const val SAVED = "Saved!"
    private val TOOL_LINES = listOf(RUBBED_OUT, NEW_PAGE, SAVED)

    // ── Words used inside sentences ──────────────────────────────────────────────

    private const val YES = "Yes!"
    private const val THIS_ONE_IS = "This one is"
    private const val FIND_THE = "Find the"
    private const val FIND = "Find"
    private const val TAP = "Tap"
    private const val PLUS = "plus"
    private const val HOW_MANY_IN_ALL = "How many in all?"
    private const val TAKE_AWAY = "take away"
    private const val HOW_MANY_LEFT = "How many are left?"
    private const val LEFT = "left!"
    private const val RHYMES_WITH = "Which word rhymes with"
    private const val AND = "and"
    private const val RHYME = "rhyme!"
    private const val IS_A_FRUIT = "is a fruit!"
    private const val IS_A_VEGETABLE = "is a vegetable!"

    /** Numbers are said from their digits ("7"), up to [MAX_NUMBER]. */
    const val MAX_NUMBER = 20

    // ── Sentences ────────────────────────────────────────────────────────────────

    /** Any single name or fixed line on its own. */
    fun say(text: String) = speech(text)

    fun yes(name: String) = speech(YES, name)
    fun yes() = speech(YES)
    fun thisOneIs(name: String) = speech(THIS_ONE_IS, name)
    fun findThe(name: String) = speech(FIND_THE, name)
    fun find(name: String) = speech(FIND, name)
    fun tap(n: Int) = speech(TAP, n.toString())
    fun addition(a: Int, b: Int) = speech(a.toString(), PLUS, b.toString(), HOW_MANY_IN_ALL)
    fun subtraction(total: Int, remove: Int) = speech(total.toString(), TAKE_AWAY, remove.toString(), HOW_MANY_LEFT)
    fun yesLeft(n: Int) = speech(YES, n.toString(), LEFT)
    fun rhymesWith(word: String, options: List<String>) = speech(RHYMES_WITH, word, *options.toTypedArray())
    fun yesRhyme(word: String, other: String) = speech(YES, word, AND, other, RHYME)
    fun yesFruit(name: String, isFruit: Boolean) = speech(YES, name, if (isFruit) IS_A_FRUIT else IS_A_VEGETABLE)

    /** Phonics quiz: the sound's example ("like Apple", "like the end of Fox"). */
    fun soundHint(p: LetterPhonic) =
        if (p.position == SoundPosition.END) "like the end of ${p.example}" else "like ${p.example}"
    fun yesSound(p: LetterPhonic) = speech(YES, p.letter, soundHint(p))

    fun whoSays(a: Animal) = "Who says ${a.sound}"
    fun animalCall(a: Animal) = speech(a.name, a.sound)
    fun vehicleCall(v: Vehicle) = speech(v.name, v.sound)
    fun whatComesAfter(name: String) = "What comes after $name?"
    fun opposite(word: String) = "What is the opposite of $word?"
    fun seasonCall(s: SeasonItem) = "${s.name}! ${s.weather}"
    fun whichSeason(s: SeasonItem) = "Which season is ${s.weather}?"
    fun fruitOrVegetable(name: String) = "Is $name a fruit or a vegetable?"
    fun crayon(name: String) = "$name!"

    /** Tracing: "Draw the letter A." / "Draw the number 5." then its phrase ("A for Apple."). */
    fun drawThe(character: String, isNumber: Boolean) = speech(
        "Draw the ${if (isNumber) "number" else "letter"} $character.",
        *listOfNotNull(Phonics.wordFor(character)?.let { "${Phonics.phraseFor(character)}." }).toTypedArray(),
    )
    fun letterPhrase(character: String) = speech(Phonics.phraseFor(character))
    fun traceTryAgain(character: String, sawNothing: Boolean) = speech(
        if (sawNothing) COULD_NOT_SEE else ALMOST,
        "This is $character.",
        *listOfNotNull(Phonics.wordFor(character)?.let { "${Phonics.phraseFor(character)}." }).toTypedArray(),
    )
    fun traceGiveUp(character: String) = speech(KEEP_PRACTICING, "${Phonics.phraseFor(character)}.", MOVING_ON)

    /** A picture's spoken name from its file ("art/animals/animal-teddy-bear.svg" -> "teddy bear"). */
    fun pictureName(asset: AssetPath): String =
        asset.path.substringAfterLast('/').substringAfter('-').substringBefore('.').replace('-', ' ')

    // ── Everything, for the recording list ──────────────────────────────────────

    private val letters = ('A'..'Z').map { it.toString() }
    private val digits = ('0'..'9').map { it.toString() }

    /** The recording sheet, grouped as a voice artist would record it. Each line once. */
    val sections: List<Pair<String, List<VoiceLine>>> by lazy {
        val seen = mutableSetOf<String>()
        fun List<String>.fresh() = map(::line).filter { seen.add(it.clip.relativePath) }
        buildList {
            add("App and worlds" to (listOf(GREETING, MY_STICKERS, GET_VOICES, GETTING_VOICES) +
                World.entries.map { it.title } + TOPICS.map { it.title }).fresh())
            add("Tracing" to (TRACE_PRAISE + DRAW_FIRST + HINDI_GIVE_UP + KEEP_PRACTICING + MOVING_ON + COULD_NOT_SEE +
                ALMOST + letters.map { "Draw the letter $it." } + digits.map { "Draw the number $it." } +
                (letters + digits).map { "This is $it." } +
                (letters + digits).filter { Phonics.wordFor(it) != null }.map { "${Phonics.phraseFor(it)}." }).fresh())
            add("Quiz sentences" to (QUIZ_LINES + TOOL_LINES + listOf(YES, TRY_AGAIN, ALL_DONE, THIS_ONE_IS, FIND_THE,
                FIND, TAP, PLUS, HOW_MANY_IN_ALL, TAKE_AWAY, HOW_MANY_LEFT, LEFT, RHYMES_WITH, AND, RHYME,
                IS_A_FRUIT, IS_A_VEGETABLE, "this one", "the big one", "the small one", "a fruit", "a vegetable") +
                ANIMALS.map(::whoSays) + DAYS.map(::whatComesAfter) + MONTHS.map { whatComesAfter(it.name) } +
                OPPOSITES.flatMap { listOf(opposite(it.word1), opposite(it.word2)) } +
                SEASONS_LIST.flatMap { listOf(seasonCall(it), whichSeason(it)) } +
                FRUITS_VEGGIES.map { fruitOrVegetable(it.name) } + PHONICS_DATA.map(::soundHint) +
                CRAYON_BOX.map { crayon(it.name) }).fresh())
            add("Numbers and letters" to ((0..MAX_NUMBER).map { it.toString() } + letters + TIMES.map { it.label } +
                FRACTIONS.map { it.spoken }).fresh())
            add("Names" to (SHAPES.map { it.name } + COLOR_ITEMS.map { it.name } + ANIMALS.map { it.name } +
                ANIMALS.map { it.sound } + VEHICLES.map { it.name } + VEHICLES.map { it.sound } +
                BODY_PARTS.map { it.name } + DAYS + MONTHS.map { it.name } + SEASONS_LIST.map { it.name } +
                EMOTIONS.map { it.name } + FRUITS_VEGGIES.map { it.name } +
                OPPOSITES.flatMap { listOf(it.word1, it.word2) } +
                // Word tiles are spelled in capitals ("APE"); listed as a word so it is read as one.
                WORD_LIST.map { it.word.lowercase().replaceFirstChar(Char::uppercase) } +
                RHYME_GROUPS.flatMap { g -> listOf(g.word) + g.rhymes.map { it.first } }.map { it.lowercase() } +
                PICTURE_SPECS.values.flatMap { spec -> spec.items.map { it.name } } +
                GAME_PICTURES.map(::pictureName)).fresh())
        }.filter { it.second.isNotEmpty() }
    }

    /** Every line, once each. */
    val all: List<VoiceLine> by lazy { sections.flatMap { it.second } }
}
