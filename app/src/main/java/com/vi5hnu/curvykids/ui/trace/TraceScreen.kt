package com.vi5hnu.curvykids.ui.trace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.vi5hnu.curvykids.ui.components.ProgressTrack
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
import com.vi5hnu.curvykids.ui.trace.components.GhostLetter
import com.vi5hnu.curvykids.ui.trace.components.LetterReferenceCard
import com.vi5hnu.curvykids.ui.kit.Responsive
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
    // The model only marks the child's writing; the lesson itself needs nothing from it.
    val modelFailed = recognizerState?.error != null

    val controller = rememberDrawingController()
    var inkColor by remember { mutableStateOf(CRAYON_COLORS.first()) }
    var showCharStrip by remember { mutableStateOf(false) }

    // Find the topic for this level
    val topic = TOPICS.find { it.set == uiState.level } ?: TOPICS.first()
    val chars = uiState.level.characters

    val ch = uiState.character

    // Clear the canvas on every new character
    LaunchedEffect(uiState.level, uiState.index) {
        controller.clear()
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

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // Landscape: the reference card and controls in a column beside the writing area, so the
        // area keeps its height instead of being squeezed to a strip under the card.
        val twoPane = Responsive.isTwoPane(maxWidth, maxHeight)
        val drawingCard: @Composable (Modifier) -> Unit = { modifier ->
            // ── Drawing card ──────────────────────────────────────────────
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = modifier,
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // The letter to trace, shown plainly underneath the child's ink.
                    GhostLetter(char = ch, modifier = Modifier.fillMaxSize())

                    // Drawing canvas (on top) — pure ink layer over the letter
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
                            // A first-time mastery earns full stars; repeating a mastered letter
                            // is praised as practice so the title and stars agree.
                            title = if (uiState.lastWasPractice) "Nice practice!" else "Perfect!",
                            sub = Phonics.phraseFor(ch),
                            stars = if (uiState.lastWasPractice) 1 else 3,
                            // For levelComplete, dismissLevelComplete() clears lastResult and
                            // advances to the next level. For normal correct answers, GameViewModel
                            // already calls next() after 1200ms which clears lastResult via goTo().
                            onDone = { if (uiState.levelJustCompleted) viewModel.dismissLevelComplete() },
                        )
                    }
                }
            }
        }
        val controlsRow: @Composable () -> Unit = {
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
                        when {
                            modelFailed -> viewModel.next()
                            !controller.hasDrawing -> viewModel.promptToDraw()
                            !uiState.isChecking -> viewModel.check(
                                strokes = controller.snapshot(),
                                writingArea = null,
                            )
                        }
                    },
                    enabled = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(62.dp),
                    containerColor = when {
                        modelFailed -> topic.color
                        controller.hasDrawing -> Green
                        else -> GreenMuted
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Without the model the app cannot mark the writing, so the button
                        // carries the child on to the next letter rather than promising a check
                        // it cannot give.
                        KidIconImage(
                            if (modelFailed) KidIcon.ArrowForward else KidIcon.Check,
                            Modifier.size(22.dp),
                            Color.White,
                        )
                        Text(
                            text = when {
                                modelFailed -> "Next"
                                uiState.isChecking -> "Checking…"
                                else -> "Check"
                            },
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
        val reference: @Composable (Modifier, Boolean) -> Unit = { modifier, tall ->
            LetterReferenceCard(
                ch = ch,
                color = topic.color,
                tint = topic.tint,
                onSpeak = { viewModel.speakCurrent() },
                tall = tall,
                modifier = modifier,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = if (twoPane) 0.dp else 24.dp),
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
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        ) {
                            ProgressTrack(
                                current = uiState.index + 1,
                                total = chars.size,
                                color = topic.color,
                            )
                        }
                    }
                },
            )

            Spacer(Modifier.height(14.dp))

            if (twoPane) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Column(Modifier.weight(0.42f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        reference(Modifier.fillMaxWidth().weight(1f), true)
                        controlsRow()
                    }
                    drawingCard(Modifier.weight(0.58f).fillMaxHeight())
                }
            } else {
                reference(Modifier.fillMaxWidth(), false)
                Spacer(Modifier.height(14.dp))
                drawingCard(Modifier.fillMaxWidth().weight(1f))
                Spacer(Modifier.height(14.dp))
                controlsRow()
            }
        }

        // Handwriting-model overlay, shown only while the model is still on its way.
        //
        // If it cannot be fetched the screen is left alone: the demonstration and the page to
        // write on are built into the app and need nothing from the network. Only the check does.
        // Covering the whole screen took the lesson away from any child without a connection,
        // over a feature that merely marks their work.
        if (recognizerState?.success != true && !modelFailed) {
            val failed = false
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
