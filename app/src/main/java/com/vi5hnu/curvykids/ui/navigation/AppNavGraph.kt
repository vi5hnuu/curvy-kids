package com.vi5hnu.curvykids.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.vi5hnu.curvykids.ui.theme.SceneHillFront
import com.vi5hnu.curvykids.ui.theme.SceneSkyBottom
import com.vi5hnu.curvykids.ui.theme.SceneSkyTop
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import com.vi5hnu.curvykids.audio.packs.allReady
import com.vi5hnu.curvykids.ui.voice.VoicePackGate
import com.vi5hnu.curvykids.ui.world.WorldScreen
import com.vi5hnu.curvykids.data.content.World
import com.vi5hnu.curvykids.data.content.Topic
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import com.vi5hnu.curvykids.ui.activities.AdditionScreen
import com.vi5hnu.curvykids.ui.activities.AnimalsScreen
import com.vi5hnu.curvykids.ui.activities.BigSmallScreen
import com.vi5hnu.curvykids.ui.activities.barakhadi.BarakhadiScreen
import com.vi5hnu.curvykids.ui.activities.BodyScreen
import com.vi5hnu.curvykids.ui.activities.ColorsScreen
import com.vi5hnu.curvykids.ui.activities.CountCatchScreen
import com.vi5hnu.curvykids.ui.activities.CountScreen
import com.vi5hnu.curvykids.ui.activities.DaysScreen
import com.vi5hnu.curvykids.ui.activities.DrawScreen
import com.vi5hnu.curvykids.ui.activities.EmotionsScreen
import com.vi5hnu.curvykids.ui.activities.FruitsVeggiesScreen
import com.vi5hnu.curvykids.ui.activities.MemoryMatchScreen
import com.vi5hnu.curvykids.ui.activities.MonthsScreen
import com.vi5hnu.curvykids.ui.activities.OddOneOutScreen
import com.vi5hnu.curvykids.ui.activities.OppositeScreen
import com.vi5hnu.curvykids.ui.activities.PatternsScreen
import com.vi5hnu.curvykids.ui.activities.FractionsScreen
import com.vi5hnu.curvykids.ui.activities.PhonicScreen
import com.vi5hnu.curvykids.ui.activities.PictureDiscoverScreen
import com.vi5hnu.curvykids.ui.activities.RhymingScreen
import com.vi5hnu.curvykids.ui.activities.TimeScreen
import com.vi5hnu.curvykids.ui.activities.SeasonsScreen
import com.vi5hnu.curvykids.ui.activities.ShapesScreen
import com.vi5hnu.curvykids.ui.activities.StoriesScreen
import com.vi5hnu.curvykids.ui.activities.SubtractionScreen
import com.vi5hnu.curvykids.ui.activities.VehiclesScreen
import com.vi5hnu.curvykids.ui.activities.WordsScreen
import com.vi5hnu.curvykids.ui.app.AppViewModel
import com.vi5hnu.curvykids.ui.app.LocalDifficulty
import com.vi5hnu.curvykids.ui.game.GameViewModel
import com.vi5hnu.curvykids.ui.home.HomeScreen
import com.vi5hnu.curvykids.ui.parent.ParentGate
import com.vi5hnu.curvykids.ui.parent.ParentZone
import com.vi5hnu.curvykids.ui.rewards.RewardsScreen
import com.vi5hnu.curvykids.ui.theme.BgBottom
import com.vi5hnu.curvykids.ui.theme.BgTop
import com.vi5hnu.curvykids.ui.trace.TraceScreen
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript
import androidx.activity.compose.LocalActivity
import com.vi5hnu.curvykids.billing.ProRepository
import com.vi5hnu.curvykids.billing.Access
import com.vi5hnu.curvykids.billing.ContentLocks
import com.vi5hnu.curvykids.billing.Lockable
import com.vi5hnu.curvykids.ui.pro.LockedSheet
import com.vi5hnu.curvykids.ads.AdUnlocks
import com.vi5hnu.curvykids.ads.RewardedAds
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.vi5hnu.curvykids.ui.pro.ProScreen

/**
 * Root navigation graph. Hosts the 4 bottom tabs and topic full-screens.
 * - AppViewModel is passed in from MainActivity (activity-scoped, lives across all screens).
 * - GameViewModel is activity-scoped too; selectLevel() is called on trace topic open.
 * - Home → world → topic. Rewards is reached from Home; grown-ups from the lock on Home.
 * - A topic whose voice packs aren't downloaded shows [VoicePackGate] until they are.
 * - Pro's purchase screen is reached only from behind the parent gate (Parent Zone, locked items).
 * - Anything locked ([Access]) opens [LockedSheet] instead, wherever it was tapped from. A free
 *   user may watch a video there ([RewardedAds]) to open that one item for today ([AdUnlocks]).
 */
@Composable
fun AppNavGraph(
    appViewModel: AppViewModel,
    gameViewModel: GameViewModel,
    voicePacks: VoicePackRepository,
    pro: ProRepository,
    access: Access,
    ads: RewardedAds,
    adUnlocks: AdUnlocks,
) {
    val navController = rememberNavController()
    val proState by pro.state.collectAsState()
    val activity = LocalActivity.current
    val accessState by access.state.collectAsState()
    val adReady by ads.ready.collectAsState()
    val scope = rememberCoroutineScope()
    val appState by appViewModel.uiState.collectAsState()
    val appSettings by appViewModel.settings.collectAsState()

    // Apply the parent "Sound effects & voice" toggle to the shared audio engines.
    LaunchedEffect(appSettings.soundEffects) {
        gameViewModel.setAudioEnabled(appSettings.soundEffects)
    }

    // Tracing gets more attempts before it moves the child on when Easy is selected.
    LaunchedEffect(appSettings.difficulty) {
        gameViewModel.setDifficulty(appSettings.difficulty)
    }

    // Behind the system bars: sky above, grass below, so it continues the child screens' scene.
    val bgGradient = Brush.verticalGradient(listOf(SceneSkyTop, SceneSkyBottom, SceneHillFront))

    // Grown-ups area: opened from the lock on Home, behind the parent gate. Saveable so a
    // rotation doesn't close an open sheet.
    var showGate by rememberSaveable { mutableStateOf(false) }
    var showZone by rememberSaveable { mutableStateOf(false) }

    // A tap on something locked: "This one is for Pro!" and the sheet with the ways in.
    var lockedItem by remember { mutableStateOf<Lockable?>(null) }
    var showProGate by rememberSaveable { mutableStateOf(false) }
    val onLocked: (Lockable) -> Unit = remember {
        { item ->
            val line = if (adReady) EnglishVoiceScript.FOR_PRO_OR_VIDEO else EnglishVoiceScript.FOR_PRO
            gameViewModel.speaker.say(EnglishVoiceScript.say(line))
            lockedItem = item
        }
    }
    // Remembered, so the screens it is passed to only redraw when what is locked changes.
    val locks = remember(accessState) { ContentLocks(accessState, onLocked) }

    // Tracing moves on to the next level by itself; it must not walk into a locked one.
    LaunchedEffect(accessState) {
        gameViewModel.isLevelOpen = { level -> !accessState.isLocked(Lockable.Activity(level.progressKey)) }
    }

    // Say the name aloud as an activity opens: the child this app is for can't read the label.
    // Every way into an activity comes through here (tiles, "play again"), so the lock is too.
    // Reads the live state, as it also runs after a video, before this screen has recomposed.
    val openTopic: (Topic) -> Unit = { topic ->
        ContentLocks(access.state.value, onLocked).open(Lockable.Activity(topic.id)) {
            gameViewModel.speaker.say(EnglishVoiceScript.say(topic.title))
            appViewModel.setLastTopic(topic.id)
            navController.navigate(Screen.Topic.createRoute(topic.id))
        }
    }

    CompositionLocalProvider(LocalDifficulty provides appSettings.difficulty) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient),
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                // Keep the background full-bleed behind the bars, but inset content below the
                // status bar (top) and above the gesture/navigation bar (bottom).
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
            ) {
                // ── Home: the six worlds ───────────────────────────────────────
                composable(Screen.Home.route) {
                    HomeScreen(
                        appState = appState,
                        onOpenWorld = { world ->
                            gameViewModel.speaker.say(EnglishVoiceScript.say(world.title))
                            navController.navigate(Screen.World.createRoute(world.id))
                        },
                        onOpenTopic = openTopic,
                        onOpenStickers = {
                            gameViewModel.speaker.say(EnglishVoiceScript.say(EnglishVoiceScript.MY_STICKERS))
                            navController.navigate(Screen.Rewards.route)
                        },
                        onOpenGrownUps = { showGate = true },
                        onSpeak = { gameViewModel.speaker.say(EnglishVoiceScript.say(it), flush = false) },
                    )
                }

                // ── One world's activities ─────────────────────────────────────
                composable(
                    route = Screen.World.route,
                    arguments = listOf(navArgument("worldId") { type = NavType.StringType }),
                ) { entry ->
                    val world = World.byId(entry.arguments?.getString("worldId").orEmpty()) ?: return@composable
                    WorldScreen(
                        world = world,
                        onBack = { navController.popBackStack() },
                        onOpenTopic = openTopic,
                        // Queued, so it follows the name said as the world was tapped.
                        onSpeak = { },
                        locks = locks,
                    )
                }

                // ── CurvyKids Pro (grown-ups only) ─────────────────────────────
                composable(Screen.Pro.route) {
                    // Play may have changed its mind (new offer, a purchase on another device).
                    LaunchedEffect(Unit) { pro.refresh() }
                    ProScreen(
                        state = proState,
                        onBuy = { offer -> activity?.let { pro.buy(it, offer) } },
                        onRetry = pro::refresh,
                        onDismissFailure = pro::clearFailure,
                        onBack = { navController.popBackStack() },
                    )
                }

                // ── Sticker book ───────────────────────────────────────────────
                composable(Screen.Rewards.route) {
                    RewardsScreen(
                        appState = appState,
                        onBack = { navController.popBackStack() },
                    )
                }

                // ── Topic full-screen ──────────────────────────────────────────
                composable(
                    route = Screen.Topic.route,
                    arguments = listOf(navArgument("topicId") { type = NavType.StringType }),
                ) { entry ->
                    val topicId = entry.arguments?.getString("topicId") ?: return@composable
                    val topic = TOPICS.find { it.id == topicId } ?: return@composable

                    // Silence the shared voice when leaving ANY topic screen, so a prompt that was
                    // mid-sentence ("Find the red…") doesn't keep talking on the tab screens.
                    DisposableEffect(topicId) {
                        onDispose { gameViewModel.speaker.stop() }
                    }

                    // Topics that speak recorded voices wait for their packs, which download by
                    // themselves (a grown-up is asked only if Play needs them). When they arrive
                    // this recomposes straight into the topic.
                    val requiredPacks = remember(topic) { VoicePack.requiredFor(topic) }
                    val packStates by voicePacks.states.collectAsState()
                    if (!packStates.allReady(requiredPacks)) {
                        VoicePackGate(
                            topic = topic,
                            packs = requiredPacks,
                            repository = voicePacks,
                            onBack = { navController.popBackStack() },
                            speak = { gameViewModel.speaker.say(EnglishVoiceScript.say(it)) },
                        )
                        return@composable
                    }

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
                            feedback = gameViewModel.feedback,
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
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.BODY -> BodyScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.DAYS -> DaysScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.DRAW -> DrawScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onAddBadge = appViewModel::addBadge,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.MEMORY -> MemoryMatchScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.BIGSMALL -> BigSmallScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.ODDONE -> OddOneOutScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.CATCH -> CountCatchScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        // ── World ──────────────────────────────────────────────────
                        TopicKind.MONTHS -> MonthsScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.SEASONS -> SeasonsScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.EMOTIONS -> EmotionsScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.VEHICLES -> VehiclesScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.FRUITSVEGGIES -> FruitsVeggiesScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        // ── Math ───────────────────────────────────────────────────
                        TopicKind.ADDITION -> AdditionScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.SUBTRACTION -> SubtractionScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.PATTERNS -> PatternsScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.TIME -> TimeScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.FRACTIONS -> FractionsScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        // ── Language ───────────────────────────────────────────────
                        TopicKind.PHONICS -> PhonicScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.OPPOSITES -> OppositeScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )
                        TopicKind.RHYMING -> RhymingScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        // ── Generic picture topics (Food, Clothes, Jobs, Birds, …) ──
                        TopicKind.PICTURE -> PictureDiscoverScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                        )

                        TopicKind.BARAKHADI -> BarakhadiScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                            readsAs = gameViewModel::readsAs,
                            locks = locks,
                        )

                        TopicKind.STORIES -> StoriesScreen(
                            topic = topic,
                            onBack = { navController.popBackStack() },
                            onReward = appViewModel::reward,
                            feedback = gameViewModel.feedback,
                            locks = locks,
                        )
                    }
                }
            }

            lockedItem?.let { item ->
                LockedSheet(
                    // Only offered when a video is already loaded: the child never waits on one.
                    onWatchAd = activity?.takeIf { adReady }?.let { host ->
                        {
                            lockedItem = null
                            gameViewModel.speaker.stop()
                            ads.show(host) {
                                scope.launch {
                                    adUnlocks.open(item)
                                    // An activity opens straight away; a letter or story is now
                                    // open where the child is, marked as open for today.
                                    if (item is Lockable.Activity) {
                                        access.state.first { !it.isLocked(item) }
                                        TOPICS.find { it.id == item.topicId }?.let(openTopic)
                                    }
                                }
                            }
                        }
                    },
                    onAskGrownUp = {
                        lockedItem = null
                        showProGate = true
                    },
                    onDismiss = { lockedItem = null },
                )
            }

            // The purchase screen sits behind the parent gate (Play Families policy).
            if (showProGate) {
                ParentGate(
                    onPass = {
                        showProGate = false
                        navController.navigate(Screen.Pro.route)
                    },
                    onDismiss = { showProGate = false },
                )
            }

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
                    onDifficulty = appViewModel::setDifficulty,
                    voicePacks = voicePacks,
                    proState = proState,
                    onOpenPro = {
                        showZone = false
                        navController.navigate(Screen.Pro.route)
                    },
                    onResetProgress = {
                        // Stars/badges and tracing progress live in separate stores.
                        appViewModel.resetProgress()
                        gameViewModel.resetProgress()
                    },
                    onDismiss = { showZone = false },
                )
            }
        }
    }
}
