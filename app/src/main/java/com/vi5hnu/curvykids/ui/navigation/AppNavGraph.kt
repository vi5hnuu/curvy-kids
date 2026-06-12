package com.vi5hnu.curvykids.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.TopicKind
import com.vi5hnu.curvykids.ui.activities.AnimalsScreen
import com.vi5hnu.curvykids.ui.activities.BodyScreen
import com.vi5hnu.curvykids.ui.activities.ColorsScreen
import com.vi5hnu.curvykids.ui.activities.CountScreen
import com.vi5hnu.curvykids.ui.activities.DaysScreen
import com.vi5hnu.curvykids.ui.activities.DrawScreen
import com.vi5hnu.curvykids.ui.activities.ShapesScreen
import com.vi5hnu.curvykids.ui.activities.WordsScreen
import com.vi5hnu.curvykids.ui.app.AppViewModel
import com.vi5hnu.curvykids.ui.game.GameViewModel
import com.vi5hnu.curvykids.ui.home.HomeScreen
import com.vi5hnu.curvykids.ui.parent.ParentGate
import com.vi5hnu.curvykids.ui.parent.ParentTab
import com.vi5hnu.curvykids.ui.parent.ParentZone
import com.vi5hnu.curvykids.ui.play.PlayScreen
import com.vi5hnu.curvykids.ui.rewards.RewardsScreen
import com.vi5hnu.curvykids.ui.theme.BgBottom
import com.vi5hnu.curvykids.ui.theme.BgTop
import com.vi5hnu.curvykids.ui.trace.TraceScreen

/**
 * Root navigation graph. Hosts the 4 bottom tabs and topic full-screens.
 * - AppViewModel is passed in from MainActivity (activity-scoped, lives across all screens).
 * - GameViewModel is activity-scoped too; selectLevel() is called on trace topic open.
 * - BottomTabBar floats over tab content as an overlay — hidden on topic full-screens.
 */
@Composable
fun AppNavGraph(
    appViewModel: AppViewModel,
    gameViewModel: GameViewModel,
) {
    val navController = rememberNavController()
    val appState by appViewModel.uiState.collectAsState()
    val appSettings by appViewModel.settings.collectAsState()

    // Gradient background applied to the full screen at all times
    val bgGradient = Brush.verticalGradient(listOf(BgTop, BgBottom))

    // Determine visibility of bottom tab bar
    val navBackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackEntry?.destination?.route
    val tabRoutes = AppTab.entries.map { it.route }.toSet()
    val onTabScreen = currentRoute in tabRoutes
    val currentTab = AppTab.entries.firstOrNull { it.route == currentRoute } ?: AppTab.HOME

    // Tab screens need bottom padding to avoid content hidden behind the floating tab bar
    val tabContentPadding = Modifier.padding(bottom = 92.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize(),
        ) {
            // ── Home tab ───────────────────────────────────────────────────
            composable(Screen.Home.route) {
                HomeScreen(
                    appState = appState,
                    onOpenTopic = { topic ->
                        appViewModel.setLastTopic(topic.id)
                        navController.navigate(Screen.Topic.createRoute(topic.id))
                    },
                    modifier = tabContentPadding,
                )
            }

            // ── Play tab ───────────────────────────────────────────────────
            composable(Screen.Play.route) {
                PlayScreen(
                    onOpenTopic = { topic ->
                        appViewModel.setLastTopic(topic.id)
                        navController.navigate(Screen.Topic.createRoute(topic.id))
                    },
                    modifier = tabContentPadding,
                )
            }

            // ── Rewards tab ────────────────────────────────────────────────
            composable(Screen.Rewards.route) {
                RewardsScreen(
                    appState = appState,
                    modifier = tabContentPadding,
                )
            }

            // ── Parent tab ─────────────────────────────────────────────────
            composable(Screen.Parent.route) {
                // rememberSaveable so config changes (rotation) don't dismiss an open sheet
                var showGate by rememberSaveable { mutableStateOf(false) }
                var showZone by rememberSaveable { mutableStateOf(false) }

                ParentTab(
                    onUnlock = { showGate = true },
                    modifier = tabContentPadding,
                )

                if (showGate) {
                    ParentGate(
                        onPass = {
                            showGate = false
                            showZone = true
                        },
                        onDismiss = { showGate = false },
                    )
                }

                if (showZone) {
                    ParentZone(
                        appState = appState,
                        settings = appSettings,
                        onSoundEffects = appViewModel::setSoundEffects,
                        onBackgroundMusic = appViewModel::setBackgroundMusic,
                        onPlayReminder = appViewModel::setPlayReminder,
                        onDismiss = { showZone = false },
                    )
                }
            }

            // ── Topic full-screen ──────────────────────────────────────────
            composable(
                route = Screen.Topic.route,
                arguments = listOf(navArgument("topicId") { type = NavType.StringType }),
            ) { entry ->
                val topicId = entry.arguments?.getString("topicId") ?: return@composable
                val topic = TOPICS.find { it.id == topicId } ?: return@composable

                when (topic.kind) {
                    TopicKind.TRACE -> {
                        // Schedule the level switch; GameViewModel.selectLevel is async
                        // (loads masteredSet from DataStore before calling goTo).
                        LaunchedEffect(topicId) { gameViewModel.selectLevel(topic.set!!) }

                        val gameState by gameViewModel.uiState.collectAsState()

                        // Don't render TraceScreen until the correct level is loaded —
                        // prevents one or more frames of wrong-character content being shown
                        // or recognised against the previous level's character.
                        if (gameState.level == topic.set) {
                            TraceScreen(
                                viewModel = gameViewModel,
                                onBack = { navController.popBackStack() },
                                onReward = appViewModel::reward,
                                onMarkMastered = appViewModel::markMastered,
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = topic.color)
                            }
                        }
                    }

                    TopicKind.SHAPES -> ShapesScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                    )

                    TopicKind.COLORS -> ColorsScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                        onAddBadge = appViewModel::addBadge,
                        speaker = gameViewModel.speaker,
                    )

                    TopicKind.COUNT -> CountScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                        speaker = gameViewModel.speaker,
                    )

                    TopicKind.WORDS -> WordsScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                        speaker = gameViewModel.speaker,
                    )

                    TopicKind.ANIMALS -> AnimalsScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                        speaker = gameViewModel.speaker,
                    )

                    TopicKind.BODY -> BodyScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                        speaker = gameViewModel.speaker,
                    )

                    TopicKind.DAYS -> DaysScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                        onReward = appViewModel::reward,
                        speaker = gameViewModel.speaker,
                    )

                    TopicKind.DRAW -> DrawScreen(
                        topic = topic,
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }

        // ── Floating bottom tab bar (hidden inside topic screens) ──────────
        if (onTabScreen) {
            BottomTabBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    navController.navigate(tab.route) {
                        // Avoid building up a large back stack
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
            )
        }
    }
}
