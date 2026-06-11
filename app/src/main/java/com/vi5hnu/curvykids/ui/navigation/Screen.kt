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

/** Items shown in the bottom tab bar. */
enum class AppTab(val route: String, val label: String, val icon: TabIcon) {
    HOME("home", "Home", TabIcon.Home),
    PLAY("play", "Play", TabIcon.Play),
    REWARDS("rewards", "Rewards", TabIcon.Star),
    PARENT("parent", "Grown-ups", TabIcon.Kid),
}

/** Lightweight icon descriptor (avoids importing the icon in the enum). */
enum class TabIcon { Home, Play, Star, Kid }
