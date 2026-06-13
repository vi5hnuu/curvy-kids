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

/** Ordered list of all 11 topics — matches the design's TOPICS array. */
val TOPICS: List<Topic> = listOf(
    Topic("upper",   "Big Letters",   "A B C",       "Aa", Coral, TintCoral, TopicKind.TRACE,   Level.UPPERCASE),
    Topic("lower",   "Small Letters", "a b c",       "bd", Teal,  TintTeal,  TopicKind.TRACE,   Level.LOWERCASE),
    Topic("numbers", "Numbers",       "1 2 3",       "12", Grape, TintGrape, TopicKind.TRACE,   Level.NUMBERS),
    Topic("shapes",  "Shapes",        "Tap & learn", "◆",  Blue,  TintBlue,  TopicKind.SHAPES),
    Topic("colors",  "Colors",        "Match it",    "🎨", Pink,  TintPink,  TopicKind.COLORS),
    Topic("count",   "Counting",      "1 to 10",     "🍎", Aqua,  TintAqua,  TopicKind.COUNT),
    Topic("words",   "First Words",   "C-A-T",       "🐱", Green, TintGreen, TopicKind.WORDS),
    Topic("animals", "Animals",       "Who am I?",   "🦁", Sun,   TintSun,   TopicKind.ANIMALS),
    Topic("body",    "My Body",       "Point & say", "👋", Coral, TintCoral, TopicKind.BODY),
    Topic("days",    "Days & Time",   "Week & sky",  "🗓️", Grape, TintGrape, TopicKind.DAYS),
    Topic("draw",    "Free Draw",     "Make art!",   "✏️", Teal,  TintTeal,  TopicKind.DRAW),
    Topic("memory",  "Memory Match",  "Find pairs",  "🧠", Grape, TintGrape, TopicKind.MEMORY),
    Topic("bigsmall","Big or Small",  "Which is big?","📏", Blue,  TintBlue,  TopicKind.BIGSMALL),
    Topic("oddone",  "Odd One Out",   "Spot it!",    "🔍", Coral, TintCoral, TopicKind.ODDONE),
    Topic("catch",   "Count & Tap",   "How many?",   "🎯", Aqua,  TintAqua,  TopicKind.CATCH),
)
