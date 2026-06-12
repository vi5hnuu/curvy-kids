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
import androidx.compose.ui.text.font.FontWeight
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
import com.vi5hnu.curvykids.ui.game.AnswerResult
import com.vi5hnu.curvykids.ui.game.GameViewModel
import com.vi5hnu.curvykids.ui.game.components.DrawingCanvas
import com.vi5hnu.curvykids.ui.game.components.rememberDrawingController
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.data.content.CRAYON_COLORS
import com.vi5hnu.curvykids.ui.trace.components.CharStrip
import com.vi5hnu.curvykids.ui.trace.components.DemoWipe

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

    val ch = uiState.character
    val word = Phonics.wordFor(ch)
    val emoji = Phonics.emojiFor(ch)

    // Clear canvas + restart DemoWipe on every new character
    LaunchedEffect(uiState.level, uiState.index) {
        controller.clear()
        demoKey++
    }

    // Clear lastResult when leaving so re-entry doesn't double-fire reward.
    DisposableEffect(Unit) {
        onDispose { viewModel.clearFeedback() }
    }

    // Give reward + sync mastery when a correct answer is accepted.
    // Topic id ("upper"/"lower"/"numbers") maps to the set prefix in AppRepository.
    LaunchedEffect(uiState.lastResult) {
        if (uiState.lastResult == AnswerResult.CORRECT) {
            val alreadyMastered = uiState.masteredCharacters.contains(ch)
            onReward(if (alreadyMastered) 1 else 5)
            onMarkMastered(topic.id, ch)
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
                        Text(
                            text = "${emoji ?: ""} ${word ?: ch}".trim(),
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 23.sp,
                        )
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
                            Text("🔊", fontSize = 22.sp)
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
                    // Ghost letter (background guide)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = ch,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 230.sp,
                            lineHeight = 230.sp,
                            color = Color(0xFFEAF1F6),
                        )
                    }

                    // Animated demo wipe overlay
                    DemoWipe(
                        char = ch,
                        color = inkColor,
                        demoKey = demoKey,
                    )

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
                        Text("🧹", fontSize = 20.sp)
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
                        Text("👆", fontSize = 20.sp)
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
                                text = "Keep going! 💪",
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

                val canCheck = controller.hasDrawing && !uiState.isChecking
                CandyButton(
                    onClick = {
                        viewModel.check(
                            strokes = controller.snapshot(),
                            writingArea = null,
                        )
                    },
                    enabled = canCheck,
                    modifier = Modifier
                        .weight(1f)
                        .height(62.dp),
                    containerColor = Green,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("✓", fontSize = 22.sp, color = Color.White)
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

        // ML Kit model loading overlay
        if (recognizerState?.success != true) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
                    .background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LottieViewer(assetName = if (recognizerState?.error != null) "something-wrong.lottie" else "baby-loading.lottie")
                    Text(
                        text = if (recognizerState?.error != null) "Oops! Please check your internet." else "Getting ready…",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = InkSoft,
                    )
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
