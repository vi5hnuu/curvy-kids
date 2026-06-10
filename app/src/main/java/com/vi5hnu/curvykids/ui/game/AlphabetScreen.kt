package com.vi5hnu.curvykids.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.R
import com.vi5hnu.curvykids.components.LottieViewer
import com.vi5hnu.curvykids.recognition.WritingArea
import com.vi5hnu.curvykids.ui.game.components.CelebrationOverlay
import com.vi5hnu.curvykids.ui.game.components.DrawingCanvas
import com.vi5hnu.curvykids.ui.game.components.LevelSelector
import com.vi5hnu.curvykids.ui.game.components.ReferenceImage
import com.vi5hnu.curvykids.ui.game.components.rememberDrawingController

private val SKY_TOP = Color(0xFFFFF3D6)
private val SKY_BOTTOM = Color(0xFFBDE7FF)
private val CHECK_GREEN = Color(0xFF66BB6A)
private val GLYPH_PURPLE = Color(0xFF7E57C2)

@Composable
fun AlphabetScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val recognizerState by viewModel.recognizerReady.collectAsState()

    val controller = rememberDrawingController()
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Clear the canvas whenever we move to a new character.
    LaunchedEffect(uiState.level, uiState.index) { controller.clear() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SKY_TOP, SKY_BOTTOM))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 520.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HeaderBar(score = uiState.score)

            LevelSelector(selected = uiState.level, onSelect = viewModel::selectLevel)

            // Reference card: cartoon for uppercase, big glyph otherwise, with a listen button.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Box(Modifier.fillMaxSize()) {
                    ReferencePane(uiState.character, Modifier.fillMaxSize().padding(8.dp))
                    RoundIconButton(
                        emoji = "🔊",
                        onClick = viewModel::speakCurrent,
                        container = Color(0xFFFFF3D6),
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    )
                }
            }

            Text(
                text = "✏️ Trace the dots, then tap Check!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color(0xFF5B5B5B),
            )

            // Drawing card flexes to fill remaining space so controls stay on screen.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Box(Modifier.fillMaxSize()) {
                    DrawingCanvas(
                        controller = controller,
                        tracingCharacter = uiState.character,
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { canvasSize = it },
                    )
                    RoundIconButton(
                        emoji = "🧽",
                        onClick = { controller.clear() },
                        container = Color(0xFFFFE0E6),
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                    )
                    FeedbackBadge(
                        result = uiState.lastResult,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    if (uiState.lastResult == AnswerResult.CORRECT) {
                        CelebrationOverlay()
                    }
                }
            }

            // Bottom controls: previous · big Check · next.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoundIconButton(
                    emoji = "◀",
                    onClick = viewModel::previous,
                    container = Color.White,
                    size = 56.dp,
                )

                val canCheck = controller.strokes.isNotEmpty() && !uiState.isChecking
                Button(
                    onClick = {
                        viewModel.check(
                            strokes = controller.snapshot(),
                            writingArea = canvasSize
                                .takeIf { it != IntSize.Zero }
                                ?.let { WritingArea(it.width.toFloat(), it.height.toFloat()) },
                        )
                    },
                    enabled = canCheck,
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CHECK_GREEN),
                ) {
                    Text(
                        text = if (uiState.isChecking) "Checking…" else "✓  Check",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    )
                }

                RoundIconButton(
                    emoji = "▶",
                    onClick = viewModel::next,
                    container = Color.White,
                    size = 56.dp,
                )
            }
        }

        // Block interaction with a Lottie loader/error until the model is ready.
        if (recognizerState?.success != true) {
            RecognizerOverlay(isError = recognizerState?.error != null)
        }
    }
}

@Composable
private fun HeaderBar(score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "CurvyKids",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color(0xFF3F51B5),
        )
        Spacer(Modifier.weight(1f))
        Card(
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3D6)),
        ) {
            Text(
                text = "⭐ $score",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFF9A825),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun ReferencePane(character: String, modifier: Modifier = Modifier) {
    val drawable = ReferenceImage.drawableFor(character)
    Box(modifier, contentAlignment = Alignment.Center) {
        if (drawable != null) {
            Image(
                painter = painterResource(drawable),
                contentDescription = "Letter $character",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            // Lowercase / numbers have no cartoon art — show a big friendly glyph.
            Text(character, fontSize = 110.sp, fontWeight = FontWeight.Bold, color = GLYPH_PURPLE)
        }
    }
}

@Composable
private fun FeedbackBadge(result: AnswerResult?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = result != null,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = modifier,
    ) {
        val (text, color) = when (result) {
            AnswerResult.CORRECT -> "🎉 Yay!" to CHECK_GREEN
            AnswerResult.WRONG -> "Try again 💪" to Color(0xFFEF5350)
            null -> "" to Color.Transparent
        }
        Card(
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = color),
        ) {
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun RoundIconButton(
    emoji: String,
    onClick: () -> Unit,
    container: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 44.dp,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = Color.Black),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
    ) {
        Text(emoji, fontSize = 20.sp)
    }
}

@Composable
private fun RecognizerOverlay(isError: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
            .background(Color.White.copy(alpha = 0.92f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LottieViewer(assetName = if (isError) "something-wrong.lottie" else "baby-loading.lottie")
            Text(
                text = if (isError) "Oops! Please check your internet." else "Getting ready…",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF5B5B5B),
            )
        }
    }
}
