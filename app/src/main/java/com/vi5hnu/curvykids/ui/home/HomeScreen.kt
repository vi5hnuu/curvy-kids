package com.vi5hnu.curvykids.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.billing.ProOffer
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.pro.OfferBanner
import com.vi5hnu.curvykids.data.content.World
import com.vi5hnu.curvykids.data.content.art
import com.vi5hnu.curvykids.data.content.dailyPicks
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarGold
import com.vi5hnu.curvykids.ui.kit.CandyCard
import com.vi5hnu.curvykids.ui.kit.HoldToOpenLock
import com.vi5hnu.curvykids.ui.kit.Responsive
import com.vi5hnu.curvykids.ui.kit.kidContentWidth
import androidx.compose.foundation.layout.fillMaxHeight
import com.vi5hnu.curvykids.ui.kit.SceneBackground
import com.vi5hnu.curvykids.ui.kit.TopicArtView
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidDimens
import com.vi5hnu.curvykids.ui.theme.KidInk
import java.time.LocalDate
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/**
 * Home: the mascot, the six worlds, and the sticker book.
 *
 * Built for a child who can't read yet: every choice is a big coloured picture that says its
 * name when tapped, and there is nothing else to choose between. The one exception is the
 * "play again" bubble, which reopens the last activity (or suggests today's pick before there
 * is one). Grown-ups get in through the lock in the corner, which needs a press and hold.
 *
 * When Pro is on discount, a quiet line for grown-ups sits under the counters ([OfferBanner]);
 * which offer, if any, is decided outside ([offer]), so Home knows nothing of billing.
 *
 * In landscape (tablet or phone on its side) the worlds take the right side and the rest a column
 * on the left; on any other short window the screen scrolls instead of squeezing the worlds.
 */
@Composable
fun HomeScreen(
    appState: AppUiState,
    onOpenWorld: (World) -> Unit,
    onOpenTopic: (Topic) -> Unit,
    onOpenStickers: () -> Unit,
    onOpenGrownUps: () -> Unit,
    onSpeak: (String) -> Unit,
    offer: ProOffer? = null,
    onOpenOffer: () -> Unit = {},
    onHideOffer: () -> Unit = {},
) {
    val banner: @Composable () -> Unit = {
        offer?.let { OfferBanner(it, onOpenOffer, onHideOffer, Modifier.padding(top = 10.dp)) }
    }
    // Greet once per visit to the app, not on every return from an activity.
    var greeted = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!greeted.value) { greeted.value = true; onSpeak(EnglishVoiceScript.GREETING) }
    }

    val today = LocalDate.now().toEpochDay()
    val playAgain: Topic? = appState.lastTopic
        ?: remember(today) { dailyPicks(lastTopicId = null, seed = today).firstOrNull() }

    val isReturn = appState.lastTopic != null
    SceneBackground {
        BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            val twoPane = Responsive.isTwoPane(maxWidth, maxHeight)
            val compact = maxHeight < 640.dp && !twoPane
            // Tablets and phones on their side: the six worlds sit three to a row.
            val columns = if (Responsive.isWide(maxWidth)) 3 else 2
            Column(
                Modifier
                    .kidContentWidth()
                    .fillMaxHeight()
                    .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(KidDimens.ScreenPadding),
            ) {
                if (twoPane) {
                    // Landscape: counters, mascot, play-again and stickers down the left, and the
                    // six worlds over the full height on the right, so nothing hides below the fold.
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(KidDimens.Gap)) {
                        BoxWithConstraints(Modifier.weight(0.32f).fillMaxHeight()) {
                            val mascot = if (maxHeight < 380.dp) 64.dp else 96.dp
                            Column(Modifier.fillMaxSize()) {
                                TopBar(appState, onOpenGrownUps)
                                banner()
                                Spacer(Modifier.weight(1f))
                                CurvyMascot(size = mascot, mood = CurvyMood.Happy, floating = true,
                                    modifier = Modifier.align(Alignment.CenterHorizontally))
                                Spacer(Modifier.weight(1f))
                                PlayAgainCard(playAgain, isReturn, onOpenTopic, Modifier.fillMaxWidth())
                                Spacer(Modifier.height(KidDimens.Gap))
                                StickerBookButton(onOpenStickers)
                            }
                        }
                        WorldGrid(onOpenWorld, columns = 3, modifier = Modifier.weight(0.68f).fillMaxHeight())
                    }
                } else {
                    TopBar(appState, onOpenGrownUps)
                    banner()
                    Spacer(Modifier.height(8.dp))
                    Greeting(playAgain, isReturn, onOpenTopic)
                    Spacer(Modifier.height(KidDimens.Gap))
                    WorldGrid(
                        onOpenWorld = onOpenWorld,
                        columns = columns,
                        modifier = if (compact) Modifier.height(if (columns == 3) 420.dp else 560.dp) else Modifier.weight(1f),
                    )
                    Spacer(Modifier.height(KidDimens.Gap))
                    StickerBookButton(onOpenStickers)
                }
            }
        }
    }
}

@Composable
private fun TopBar(appState: AppUiState, onOpenGrownUps: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Counter(KidIcon.Star, StarGold, "${appState.stars}", "${appState.stars} stars")
        Spacer(Modifier.width(10.dp))
        Counter(KidIcon.Flame, Coral, "${appState.streak}", "${appState.streak} days in a row")
        Spacer(Modifier.weight(1f))
        HoldToOpenLock(onOpen = onOpenGrownUps)
    }
}

/** A chunky number with its picture. The words are for TalkBack only. */
@Composable
private fun Counter(icon: KidIcon, tint: Color, value: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clearAndSetSemantics { contentDescription = label },
    ) {
        KidIconImage(icon = icon, modifier = Modifier.size(26.dp), color = tint)
        Spacer(Modifier.width(6.dp))
        Text(value, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = tint)
    }
}

/** The mascot, and next to it a bubble that jumps straight back into an activity. */
@Composable
private fun Greeting(playAgain: Topic?, isReturn: Boolean, onOpenTopic: (Topic) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        CurvyMascot(size = 104.dp, mood = CurvyMood.Happy, floating = true)
        Spacer(Modifier.width(12.dp))
        PlayAgainCard(playAgain, isReturn, onOpenTopic, Modifier.weight(1f))
    }
}

/** The "play again" bubble: the last activity, or today's pick before there is one. */
@Composable
private fun PlayAgainCard(playAgain: Topic?, isReturn: Boolean, onOpenTopic: (Topic) -> Unit, modifier: Modifier) {
    if (playAgain == null) return
    val world = World.of(playAgain)
    CandyCard(
        color = Color.White,
        onClick = { onOpenTopic(playAgain) },
        label = (if (isReturn) "Play again: " else "Try this: ") + playAgain.title,
        radius = 26.dp,
        modifier = modifier.height(92.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp)) {
            TopicArtView(playAgain.art(), world.color, Modifier.size(62.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                playAgain.title,
                fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp,
                color = KidInk, maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            KidIconImage(KidIcon.ArrowForward, Modifier.size(30.dp), color = world.color)
        }
    }
}

/** The six worlds, [columns] per row, sharing whatever height is left. */
@Composable
private fun WorldGrid(onOpenWorld: (World) -> Unit, columns: Int, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(KidDimens.Gap)) {
        World.entries.chunked(columns).forEach { row ->
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(KidDimens.Gap)) {
                row.forEach { world -> WorldTile(world, { onOpenWorld(world) }, Modifier.weight(1f).fillMaxSize()) }
            }
        }
    }
}

/** One world: its picture on a lighter panel, and its name underneath. */
@Composable
private fun WorldTile(world: World, onClick: () -> Unit, modifier: Modifier) {
    CandyCard(color = world.color, onClick = onClick, label = world.title, modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(12.dp),
        ) {
            Box(
                Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = 0.25f)).padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                TopicArtView(world.art, Color.White, Modifier.fillMaxSize())
            }
            Spacer(Modifier.height(6.dp))
            Text(world.title, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = KidDimens.TileTitle, color = Color.White, maxLines = 1)
        }
    }
}

@Composable
private fun StickerBookButton(onClick: () -> Unit, height: Dp = 76.dp) {
    CandyCard(color = Color.White, onClick = onClick, label = "My stickers",
        modifier = Modifier.fillMaxWidth().height(height)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KidIconImage(KidIcon.Star, Modifier.size(38.dp), color = StarGold)
            Spacer(Modifier.width(10.dp))
            Text("My stickers", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp, color = KidInk)
        }
    }
}
