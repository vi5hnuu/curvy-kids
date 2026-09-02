package com.vi5hnu.curvykids.ui.navigation

/** Navigation destinations within the app. */
sealed class Screen(val route: String) {
    // Bottom tabs
    data object Home    : Screen("home")
    data object Play    : Screen("play")
    data object Rewards : Screen("rewards")
    data object Parent  : Screen("parent")

    // Topic full-screens (overlaid on top of tab content)
    data object Topic   : Screen("topic/{topicId}") {
        fun createRoute(topicId: String) = "topic/$topicId"
    }
}

/**
 * Items shown in the bottom tab bar.
 *
 * @param holdToOpen Opens on a press-and-hold rather than a tap. The grown-ups tab is a quarter
 *   of the bar and a child hits it by accident, which drops them out of play onto a screen with
 *   nothing for them on it. A hold is beyond a toddler's aim but nothing to an adult.
 */
enum class AppTab(
    val route: String,
    val label: String,
    val icon: TabIcon,
    val holdToOpen: Boolean = false,
) {
    HOME("home", "Home", TabIcon.Home),
    PLAY("play", "Play", TabIcon.Play),
    REWARDS("rewards", "Rewards", TabIcon.Star),
    PARENT("parent", "Grown-ups", TabIcon.Kid, holdToOpen = true),
}

/** Lightweight icon descriptor (avoids importing the icon in the enum). */
enum class TabIcon { Home, Play, Star, Kid }
