package com.vi5hnu.curvykids.data.content

/**
 * The single source of truth for illustrated-asset paths.
 *
 * Every picture in the app comes from the SVG library under `assets/art/`, laid out
 * as `art/<category>/<prefix>-<name>.svg`. Content lists used to embed those strings literally,
 * which meant a renamed or missing file surfaced only as a blank badge at runtime, in one screen,
 * on one topic.
 *
 * Routing every reference through these builders means the naming convention is stated once, a
 * typo is a compile error rather than a blank tile, and [ALL_CATEGORIES] gives tests something
 * concrete to check the library against.
 *
 * Names are the file's own suffix, lowercase and hyphenated — `animal("polar-bear")` resolves to
 * `art/animals/animal-polar-bear.svg`.
 */
/**
 * The location of an illustration inside `assets/`.
 *
 * A distinct type rather than a bare String on purpose: an asset path is not display text, and
 * when it was a String nothing stopped one being handed to a `Text` composable. That went wrong
 * three separate times — a child was shown AssetPath("kids/fruits/fruit-apple.svg") where the picture of an
 * apple belonged. Wrapping it means the compiler rejects that instead of the screen rendering it.
 */
@JvmInline
value class AssetPath(val path: String)

object KidAssets {

    private const val ROOT = "art"

    /** Builds a path from a category directory and its filename prefix. */
    private fun path(category: String, prefix: String, name: String) =
        AssetPath("$ROOT/$category/$prefix-$name.svg")

    // ── Living things ────────────────────────────────────────────────────────────
    fun animal(name: String) = path("animals", "animal", name)
    fun bird(name: String) = path("birds", "bird", name)
    fun insect(name: String) = path("insects", "insect", name)
    fun seaAnimal(name: String) = path("sea-animals", "sea", name)

    // ── People & places ──────────────────────────────────────────────────────────
    fun body(name: String) = path("body", "body", name)
    fun feeling(name: String) = path("feelings", "feeling", name)
    fun job(name: String) = path("jobs", "job", name)
    fun place(name: String) = path("places", "place", name)
    fun sport(name: String) = path("sports", "sport", name)

    // ── Things ───────────────────────────────────────────────────────────────────
    fun clothes(name: String) = path("clothes", "clothes", name)
    fun home(name: String) = path("around-home", "home", name)
    fun instrument(name: String) = path("instruments", "instrument", name)
    fun toy(name: String) = path("toys", "toy", name)
    fun transport(name: String) = path("transport", "transport", name)

    // ── Food ─────────────────────────────────────────────────────────────────────
    fun food(name: String) = path("food", "food", name)
    fun fruit(name: String) = path("fruits", "fruit", name)
    fun vegetable(name: String) = path("vegetables", "veg", name)

    // ── World ────────────────────────────────────────────────────────────────────
    fun nature(name: String) = path("nature", "nature", name)
    fun weather(name: String) = path("weather", "weather", name)
    fun day(name: String) = path("days-months", "day", name)
    fun month(name: String) = path("days-months", "month", name)

    // ── Abstract / academic ──────────────────────────────────────────────────────
    fun shape(name: String) = path("shapes", "shape", name)
    fun color(name: String) = path("colors", "color", name)
    fun pattern(name: String) = path("patterns", "pattern", name)
    fun mathSymbol(name: String) = path("math-symbols", "math", name)
    fun fraction(name: String) = path("fractions", "fraction", name)

    /** Drawing tools (eraser, new page, tick, frame, crayon-<colour>) as the real objects. */
    fun tool(name: String) = path("drawing-tools", "tool", name)

    /** Counting sets illustrate 1–10 as grouped objects. */
    fun counting(n: Int) = path("counting", "count", n.toString())

    /** Apples for the number tiles: "apples-3" (123), "apples-add" (1+2), "apples-take" (3−1). */
    fun countingPicture(name: String) = path("counting", "count", name)

    /** Clock faces, one per o'clock hour (1–12). */
    fun time(hour: Int) = path("telling-time", "time", hour.toString())

    /** Numerals 0–99 plus a few beyond, drawn as standalone digits. */
    fun number(n: Int) = path("numbers", "number", n.toString())

    // ── Letters ──────────────────────────────────────────────────────────────────
    /** Uppercase Latin letters keep their case in the filename (`uppercase-A.svg`). */
    fun uppercase(letter: Char) = path("uppercase", "uppercase", letter.uppercaseChar().toString())

    /** Lowercase Latin letters carry a `-low` suffix (`lowercase-a-low.svg`). */
    fun lowercase(letter: Char) = path("lowercase", "lowercase", "${letter.lowercaseChar()}-low")

    fun hindiVowel(romanized: String) = path("hindi-vowels", "hindi-swar", romanized)
    fun hindiConsonant(romanized: String) = path("hindi-consonants", "hindi-vyanjan", romanized)
    fun barakhadi(romanized: String) = path("hindi-barakhadi", "barakhadi", romanized)

    /** Pictures drawn for barakhadi example words no other category covers (कंघी, किताब...). */
    fun hindiWord(name: String) = path("hindi-words", "hindi-word", name)

    /**
     * Every category directory the library defines. Used by tests to assert that the shipped
     * asset folders and this catalog have not drifted apart.
     */
    val ALL_CATEGORIES = listOf(
        "animals", "around-home", "birds", "body", "clothes", "colors", "counting", "drawing-tools",
        "days-months", "feelings", "food", "fractions", "fruits", "hindi-barakhadi",
        "hindi-consonants", "hindi-vowels", "hindi-words", "insects", "instruments", "jobs", "lowercase",
        "math-symbols", "nature", "numbers", "patterns", "places", "sea-animals", "shapes",
        "sports", "telling-time", "toys", "transport", "uppercase", "vegetables", "weather",
    )
}
