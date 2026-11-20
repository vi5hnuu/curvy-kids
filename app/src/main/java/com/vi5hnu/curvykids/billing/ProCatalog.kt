package com.vi5hnu.curvykids.billing

/**
 * What CurvyKids Pro unlocks, in one place: the Play product, and the part of the app that stays
 * free. Screens ask this object whether something is locked; they never hold a list of their own,
 * so changing the split is a change here only.
 *
 * Every world keeps a few free activities, so a child gets a real taste of each before a parent
 * is asked to pay. Hindi Barakhadi and Stories are free in part: the first few letters and the
 * first story.
 */
object ProCatalog {

    /** The one-time product in the Play Console (Monetization > Products > One-time products). */
    const val PRODUCT_ID = "curvykids_pro"

    /** Activities open to everyone, by Topic.id. Barakhadi and Stories open, but only in part. */
    private val FREE_TOPIC_IDS = setOf(
        // Letters
        "upper", "lower",
        // Numbers
        "numbers", "count", "shapes",
        // Hindi (barakhadi and stories are free in part, see below)
        "hindi_vowels", "barakhadi", "stories",
        // Animals
        "animals", "birds",
        // My World
        "colors", "body",
        // Games
        "draw", "memory",
    )

    /** Barakhadi consonants open without Pro: क ख ग घ ङ. */
    const val FREE_BARAKHADI_CONSONANTS = 5

    /** Stories open without Pro, from the top of the list. */
    const val FREE_STORIES = 1

    /** Whether [item] is open to everyone, Pro or not. */
    fun isFree(item: Lockable): Boolean = when (item) {
        is Lockable.Activity -> item.topicId in FREE_TOPIC_IDS
        is Lockable.BarakhadiLetter -> item.index < FREE_BARAKHADI_CONSONANTS
        is Lockable.Story -> item.index < FREE_STORIES
    }
}
