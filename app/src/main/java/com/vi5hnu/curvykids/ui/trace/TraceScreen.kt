package com.vi5hnu.curvykids.ui.trace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.components.LottieViewer
import com.vi5hnu.curvykids.data.content.Phonics
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.game.AnswerResult
import com.vi5hnu.curvykids.ui.game.GameViewModel
import com.vi5hnu.curvykids.ui.game.components.DrawingCanvas
import com.vi5hnu.curvykids.ui.game.components.rememberDrawingController
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.data.content.CRAYON_COLORS
import com.vi5hnu.curvykids.ui.trace.components.CharStrip
import com.vi5hnu.curvykids.ui.trace.components.DemoWipe
import com.vi5hnu.curvykids.ui.trace.components.StrokeOrderDemo
import com.vi5hnu.curvykids.ui.trace.components.hasStrokeGuide
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.KidIcon

/**
 * Redesigned tracing screen matching trace.jsx design.
 * Keeps all ML Kit recognition logic from GameViewModel unchanged.
 *
 * @param viewModel       The game/trace ViewModel.
 * @param onBack          Called when the back button is tapped.
 * @param onReward        Called with star amount when a correct answer is given.
 * @param onMarkMastered  Called with (set, char) to sync mastery into AppRepository.
 */
@Composable
fun TraceScreen(
    viewModel: GameViewModel,
    onBack: () -> Unit,
    onReward: (Int) -> Unit = {},
    onMarkMastered: (set: String, char: String) -> Unit = { _, _ -> },
) {
    val uiState by viewModel.uiState.collectAsState()
    val recognizerState by viewModel.recognizerReady.collectAsState()

    val controller = rememberDrawingController()
    var demoKey by remember { mutableStateOf(0) }
    var inkColor by remember { mutableStateOf(CRAYON_COLORS.first()) }
    var showCharStrip by remember { mutableStateOf(false) }

    // Find the topic for this level
    val topic = TOPICS.find { it.set == uiState.level } ?: TOPICS.first()
    val chars = uiState.level.characters

    // Sized in dp-converted-to-sp so the guide glyph keeps its layout at any OS font scale.
    val ghostSize = with(LocalDensity.current) { 190.dp.toSp() }

    val ch = uiState.character
    val word = Phonics.wordFor(ch)
    val svg = Phonics.assetFor(ch)

    // Clear canvas + restart DemoWipe on every new character
    LaunchedEffect(uiState.level, uiState.index) {
        controller.clear()
        demoKey++
    }

    // Clear lastResult when leaving so re-entry doesn't double-fire reward.
    // (Voice is silenced centrally by the Topic composable's DisposableEffect.)
    DisposableEffect(Unit) {
        onDispose { viewModel.clearFeedback() }
    }

    // Give reward + sync mastery when a correct answer is accepted.
    // Driven by the one-shot event stream rather than uiState.lastResult, so a rotation (which
    // replays state to the recreated composition) cannot pay the child twice for one letter.
    LaunchedEffect(viewModel) {
        viewModel.correctAnswers.collect { answer ->
            onReward(if (answer.alreadyMastered) 1 else 5)
            onMarkMastered(answer.topicId, answer.character)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 24.dp),
        ) {
            // ── Header ────────────────────────────────────────────────────
            ScreenHeader(
                title = topic.title,
                color = topic.color,
                onBack = onBack,
                trailing = {
                    Pill(onClick = { showCharStrip = true }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            Text(
                                text = "${uiState.index + 1}/${chars.size}",
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = topic.color,
                            )
                        }
                    }
                },
            )

            Spacer(Modifier.height(16.dp))

            // ── Reference card ────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp),
                ) {
                    // Character badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = topic.tint,
                        modifier = Modifier.size(74.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ch,
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 44.sp,
                                color = topic.color,
                            )
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (word != null) "SAY IT" else "TRACE IT",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = InkSoft,
                        )
                        // Picture beside the word, not the asset path inside the string.
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (svg != null) {
                                SvgArt(svg, modifier = Modifier.size(34.dp))
                                Spacer(Modifier.width(8.dp))
                            }
                            Text(
                                text = word ?: ch,
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 23.sp,
                            )
                        }
                    }

                    // Listen button (pulsing chip)
                    Surface(
                        onClick = { viewModel.speakCurrent() },
                        shape = CircleShape,
                        color = topic.tint,
                        modifier = Modifier.size(50.dp),
                        shadowElevation = 2.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            KidIconImage(KidIcon.Speaker, Modifier.size(22.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Drawing card ──────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Guidance layer. Where stroke-order data exists (all Latin letters and
                    // digits) the child is shown how the character is actually formed — pen
                    // travelling each stroke in the taught order and direction. Devanagari has
                    // no authored stroke data yet, so it keeps the plain reveal of the glyph.
                    if (hasStrokeGuide(ch)) {
                        StrokeOrderDemo(
                            char = ch,
                            color = inkColor,
                            demoKey = demoKey,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(28.dp),
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = ch,
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = ghostSize,
                                lineHeight = ghostSize,
                                color = Color(0xFFEAF1F6),
                            )
                        }
                        DemoWipe(
                            char = ch,
                            color = inkColor,
                            demoKey = demoKey,
                        )
                    }

                    // Drawing canvas (on top) — pure ink layer, guidance handled by ghost letter + DemoWipe
                    DrawingCanvas(
                        controller = controller,
                        inkColor = inkColor,
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Erase chip (top-left)
                    Chip(
                        onClick = {
                            controller.clear()
                            viewModel.clearFeedback()
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .size(46.dp),
                        containerColor = Color(0xFFFFE9E4),
                        contentColor = Color(0xFFFF8B6B),
                    ) {
                        KidIconImage(KidIcon.Eraser, Modifier.size(20.dp))
                    }

                    // Replay "show me" chip (top-right)
                    Chip(
                        onClick = { demoKey++ },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(46.dp),
                        containerColor = Color(0xFFDFF7F2),
                        contentColor = Color(0xFF1FC2AE),
                    ) {
                        KidIconImage(KidIcon.Pointer, Modifier.size(20.dp))
                    }

                    // Crayon tray (bottom-center)
                    if (!controller.isDrawing) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .background(
                                    Color.White.copy(alpha = 0.8f),
                                    RoundedCornerShape(999.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            CRAYON_COLORS.forEach { c ->
                                Surface(
                                    onClick = { inkColor = c },
                                    shape = CircleShape,
                                    color = c,
                                    modifier = Modifier.size(if (c == inkColor) 30.dp else 26.dp),
                                    border = if (c == inkColor)
                                        androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                                    else null,
                                ) {}
                            }
                        }
                    }

                    // Wrong banner
                    if (uiState.lastResult == AnswerResult.WRONG) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 22.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = "Keep going!",
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                            )
                        }
                    }

                    // Celebrate overlay on correct answer
                    if (uiState.lastResult == AnswerResult.CORRECT) {
                        Celebrate(
                            title = "Perfect!",
                            sub = Phonics.phraseFor(ch),
                            stars = if (uiState.masteredCharacters.contains(ch)) 1 else 3,
                            // For levelComplete, dismissLevelComplete() clears lastResult and
                            // advances to the next level. For normal correct answers, GameViewModel
                            // already calls next() after 1200ms which clears lastResult via goTo().
                            onDone = { if (uiState.levelJustCompleted) viewModel.dismissLevelComplete() },
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Controls row ──────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Chip(
                    onClick = viewModel::previous,
                    modifier = Modifier.size(58.dp),
                    contentColor = InkFaint,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Previous",
                        modifier = Modifier.size(26.dp),
                    )
                }

                // Deliberately always enabled: a greyed-out button reads as "broken" to a
                // four-year-old, who has no way to know it is waiting for them to draw. It stays
                // tappable and says what to do instead.
                CandyButton(
                    onClick = {
                        if (!controller.hasDrawing) {
                            viewModel.promptToDraw()
                        } else if (!uiState.isChecking) {
                            viewModel.check(
                                strokes = controller.snapshot(),
                                writingArea = null,
                            )
                        }
                    },
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(62.dp),
                    containerColor = if (controller.hasDrawing) Green else GreenMuted,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        KidIconImage(KidIcon.Check, Modifier.size(22.dp), Color.White)
                        Text(
                            text = if (uiState.isChecking) "Checking…" else "Check",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 23.sp,
                            color = Color.White,
                        )
                    }
                }

                Chip(
                    onClick = viewModel::next,
                    modifier = Modifier.size(58.dp),
                    contentColor = InkFaint,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "Next",
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }

        // Handwriting-model overlay. The model is downloaded once per script, so this is what a
        // child sees on a first run, and what they are left with if the download cannot happen.
        if (recognizerState?.success != true) {
            val failed = recognizerState?.error != null
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // Swallow taps aimed at the canvas underneath, but never trap the child:
                    // the way out is always on screen (below) and the system back still works.
                    .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
                    .background(Color.White.copy(alpha = 0.94f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 28.dp),
                ) {
                    LottieViewer(assetName = if (failed) "something-wrong.lottie" else "baby-loading.lottie")
                    Text(
                        text = if (failed) "We need the internet to set up writing." else "Getting ready…",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Ink,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (failed) {
                            "Ask a grown-up to connect, then come back. Everything else still works!"
                        } else {
                            "This only happens once."
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = InkSoft,
                        textAlign = TextAlign.Center,
                    )
                    if (failed) {
                        Spacer(Modifier.height(20.dp))
                        CandyButton(
                            onClick = onBack,
                            modifier = Modifier.height(56.dp),
                            containerColor = topic.color,
                        ) {
                            Text(
                                text = "Pick something else",
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    // CharStrip bottom sheet
    if (showCharStrip) {
        CharStrip(
            topic = topic,
            chars = chars,
            currentIndex = uiState.index,
            mastered = uiState.masteredCharacters,
            onPick = { idx ->
                viewModel.jumpToIndex(idx)
                showCharStrip = false
            },
            onDismiss = { showCharStrip = false },
        )
    }
}

/**
 * The Check button before anything is drawn. A solid tint rather than a translucent Green:
 * a see-through Surface lets its own elevation shadow show through as a pale inner block.
 */
private val GreenMuted = Color(0xFFA6E0C4)
