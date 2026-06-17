package com.vi5hnu.curvykids.data.content

import androidx.compose.ui.graphics.Color
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.Grape
import com.vi5hnu.curvykids.ui.theme.Blue
import com.vi5hnu.curvykids.ui.theme.Pink
import com.vi5hnu.curvykids.ui.theme.Aqua
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Sun
import com.vi5hnu.curvykids.ui.theme.TintCoral
import com.vi5hnu.curvykids.ui.theme.TintTeal
import com.vi5hnu.curvykids.ui.theme.TintGrape
import com.vi5hnu.curvykids.ui.theme.TintBlue
import com.vi5hnu.curvykids.ui.theme.TintPink
import com.vi5hnu.curvykids.ui.theme.TintAqua
import com.vi5hnu.curvykids.ui.theme.TintGreen
import com.vi5hnu.curvykids.ui.theme.TintSun

/** What kind of activity a topic launches. */
enum class TopicKind {
    TRACE, SHAPES, COLORS, COUNT, WORDS, ANIMALS, BODY, DAYS, DRAW,
    MEMORY, BIGSMALL, ODDONE, CATCH,
    // World group
    MONTHS, SEASONS, EMOTIONS, VEHICLES, FRUITSVEGGIES,
    // Math group
    ADDITION, SUBTRACTION, PATTERNS, TIME, FRACTIONS,
    // Language group
    PHONICS, OPPOSITES, RHYMING,
    // Generic picture topics (Food, Clothes, Jobs, …) — content keyed by Topic.id in PICTURE_SPECS
    PICTURE,
}

/**
 * A single entry in the "Pick an adventure" grid on the Home and Play screens.
 *
 * @param id     Stable identifier persisted as lastTopicId.
 * @param title  Display name shown on the card.
 * @param sub    Short subtitle line on the card.
 * @param glyph  Badge content (1–2 chars or single emoji).
 * @param color  Accent color for the badge and header.
 * @param tint   Very-light tint used as the card background.
 * @param kind   Which activity screen to launch.
 * @param set    For TRACE topics, which character set to use.
 */
data class Topic(
    val id: String,
    val title: String,
    val sub: String,
    val glyph: String,
    val color: Color,
    val tint: Color,
    val kind: TopicKind,
    val set: Level? = null,
)

/** Ordered list of all topics — grouped by section for display on Home and Play screens. */
val TOPICS: List<Topic> = listOf(
    // Writing
    Topic("upper",        "Big Letters",    "A B C",        "Aa", Coral, TintCoral, TopicKind.TRACE,       Level.UPPERCASE),
    Topic("lower",        "Small Letters",  "a b c",        "bd", Teal,  TintTeal,  TopicKind.TRACE,       Level.LOWERCASE),
    Topic("numbers",      "Numbers",        "1 2 3",        "12", Grape, TintGrape, TopicKind.TRACE,       Level.NUMBERS),
    // Discover
    Topic("shapes",       "Shapes",         "Tap & learn",  "◆",  Blue,  TintBlue,  TopicKind.SHAPES),
    Topic("colors",       "Colors",         "Match it",     "🎨", Pink,  TintPink,  TopicKind.COLORS),
    Topic("animals",      "Animals",        "Who am I?",    "🦁", Sun,   TintSun,   TopicKind.ANIMALS),
    Topic("body",         "My Body",        "Point & say",  "👋", Coral, TintCoral, TopicKind.BODY),
    Topic("days",         "Days & Time",    "Week & sky",   "🗓️", Grape, TintGrape, TopicKind.DAYS),
    // Games
    Topic("count",        "Counting",       "1 to 10",      "🍎", Aqua,  TintAqua,  TopicKind.COUNT),
    Topic("words",        "First Words",    "C-A-T",        "🐱", Green, TintGreen, TopicKind.WORDS),
    Topic("draw",         "Free Draw",      "Make art!",    "✏️", Teal,  TintTeal,  TopicKind.DRAW),
    // Brain Games
    Topic("memory",       "Memory Match",   "Find pairs",   "🧠", Grape, TintGrape, TopicKind.MEMORY),
    Topic("bigsmall",     "Big or Small",   "Which is big?","📏", Blue,  TintBlue,  TopicKind.BIGSMALL),
    Topic("oddone",       "Odd One Out",    "Spot it!",     "🔍", Coral, TintCoral, TopicKind.ODDONE),
    Topic("catch",        "Count & Tap",    "How many?",    "🎯", Aqua,  TintAqua,  TopicKind.CATCH),
    // World
    Topic("months",       "Months",         "Jan – Dec",    "🗓️", Blue,  TintBlue,  TopicKind.MONTHS),
    Topic("seasons",      "Seasons",        "Spring…",      "🌸", Green, TintGreen, TopicKind.SEASONS),
    Topic("emotions",     "Feelings",       "How I feel",   "😊", Pink,  TintPink,  TopicKind.EMOTIONS),
    Topic("vehicles",     "Vehicles",       "Vroom!",       "🚗", Coral, TintCoral, TopicKind.VEHICLES),
    Topic("fruitsveggies","Fruits & Vegs",  "Sort it!",     "🍎", Green, TintGreen, TopicKind.FRUITSVEGGIES),
    // Math
    Topic("addition",     "Addition",       "1 + 2 = ?",    "➕", Teal,  TintTeal,  TopicKind.ADDITION),
    Topic("subtraction",  "Subtraction",    "5 - 2 = ?",    "➖", Grape, TintGrape, TopicKind.SUBTRACTION),
    Topic("patterns",     "Patterns",       "What's next?", "🔷", Blue,  TintBlue,  TopicKind.PATTERNS),
    Topic("time",         "Telling Time",   "O'clock",      "🕐", Aqua,  TintAqua,  TopicKind.TIME),
    Topic("fractions",    "Fractions",      "Half & more",  "🍕", Coral, TintCoral, TopicKind.FRACTIONS),
    // Language
    Topic("phonics",      "Letter Sounds",  "A says ahh",   "🔤", Coral, TintCoral, TopicKind.PHONICS),
    Topic("opposites",    "Opposites",      "Big ↔ Small",  "↔️", Sun,   TintSun,   TopicKind.OPPOSITES),
    Topic("rhyming",      "Rhyming",        "Cat – Hat!",   "🎵", Pink,  TintPink,  TopicKind.RHYMING),
    // Hindi — trace + recognise Devanagari (reuses the TRACE scaffold via the level's script)
    Topic("hindi_vowels",     "Hindi Vowels",  "स्वर",   "अ", Coral, TintCoral, TopicKind.TRACE, Level.HINDI_VOWELS),
    Topic("hindi_consonants", "Hindi Letters", "व्यंजन", "क", Teal,  TintTeal,  TopicKind.TRACE, Level.HINDI_CONSONANTS),
    // Nature & Animals (picture topics)
    Topic("birds",        "Birds",          "Tweet!",       "🐦", Blue,  TintBlue,  TopicKind.PICTURE),
    Topic("sea",          "Sea Animals",    "Splash!",      "🐬", Aqua,  TintAqua,  TopicKind.PICTURE),
    Topic("insects",      "Bugs",           "Creepy!",      "🐞", Green, TintGreen, TopicKind.PICTURE),
    Topic("nature",       "Nature",         "Outdoors",     "🌳", Green, TintGreen, TopicKind.PICTURE),
    Topic("weather",      "Weather",        "In the sky",   "🌦️", Aqua,  TintAqua,  TopicKind.PICTURE),
    // Things
    Topic("food",         "Food",           "Yummy!",       "🍕", Coral, TintCoral, TopicKind.PICTURE),
    Topic("clothes",      "Clothes",        "Wear it",      "👕", Pink,  TintPink,  TopicKind.PICTURE),
    Topic("toys",         "Toys & Fun",     "Playtime",     "🧸", Sun,   TintSun,   TopicKind.PICTURE),
    Topic("instruments",  "Music",          "La la la!",    "🎸", Grape, TintGrape, TopicKind.PICTURE),
    Topic("home",         "My Home",        "Indoors",      "🛋️", Teal,  TintTeal,  TopicKind.PICTURE),
    // People & Places
    Topic("jobs",         "Jobs",           "Who am I?",    "👷", Blue,  TintBlue,  TopicKind.PICTURE),
    Topic("places",       "Places",         "Where?",       "🏠", Coral, TintCoral, TopicKind.PICTURE),
    Topic("sports",       "Sports",         "Let's play!",  "⚽", Green, TintGreen, TopicKind.PICTURE),
)
