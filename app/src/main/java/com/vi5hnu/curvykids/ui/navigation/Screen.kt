package com.vi5hnu.curvykids.ui.navigation

/** Navigation destinations within the app: Home → World → Topic, plus the sticker book and Pro. */
sealed class Screen(val route: String) {
    data object Home    : Screen("home")
    data object Rewards : Screen("rewards")

    /** Buying CurvyKids Pro; only ever navigated to after the parent gate. */
    data object Pro     : Screen("pro")

    /** One world's activities (see [com.vi5hnu.curvykids.data.content.World]). */
    data object World   : Screen("world/{worldId}") {
        fun createRoute(worldId: String) = "world/$worldId"
    }

    /** An activity, full-screen. */
    data object Topic   : Screen("topic/{topicId}") {
        fun createRoute(topicId: String) = "topic/$topicId"
    }
}
