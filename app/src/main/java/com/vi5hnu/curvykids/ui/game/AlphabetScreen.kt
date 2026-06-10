package com.vi5hnu.curvykids.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.paint
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
import com.vi5hnu.curvykids.data.content.Level
import com.vi5hnu.curvykids.recognition.WritingArea
import com.vi5hnu.curvykids.ui.game.components.CelebrationOverlay
import com.vi5hnu.curvykids.ui.game.components.DrawingCanvas
import com.vi5hnu.curvykids.ui.game.components.LevelSelector
import com.vi5hnu.curvykids.ui.game.components.ReferenceImage
import com.vi5hnu.curvykids.ui.game.components.rememberDrawingController

private val INK_GREEN = Color(0xFFA9BD3E)

@Composable
fun AlphabetScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val recognizerState by viewModel.recognizerReady.collectAsState()

    val controller = rememberDrawingController()
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // Clear the canvas whenever we move to a new character.
    LaunchedEffect(uiState.level, uiState.index) { controller.clear() }

    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 480.dp)
                .align(Alignment.TopCenter)
                .paint(painterResource(R.drawable.bg), contentScale = ContentScale.Crop)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Learn ${trackTitle(uiState.level)}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = Color.Black,
            )

            LevelSelector(selected = uiState.level, onSelect = viewModel::selectLevel)

            // Reference: cartoon image for uppercase, rendered glyph otherwise.
            ReferencePane(
                character = uiState.character,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
            )

            // Drawing surface with a clear button and confetti overlay.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White),
            ) {
                DrawingCanvas(
                    controller = controller,
                    tracingCharacter = uiState.character,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = it },
                )
                IconButton(
                    onClick = { controller.clear() },
                    modifier = Modifier.align(Alignment.TopStart),
                ) {
                    Text("↺", fontSize = 24.sp)
                }
                if (uiState.lastResult == AnswerResult.CORRECT) {
                    CelebrationOverlay()
                }
            }

            // Navigation + score + listen.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = viewModel::previous) { Text("◀", fontSize = 22.sp) }
                IconButton(onClick = viewModel::speakCurrent) { Text("🔊", fontSize = 22.sp) }
                Text("⭐ ${uiState.score}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                IconButton(onClick = viewModel::next) { Text("▶", fontSize = 22.sp) }
            }

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
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState.isChecking) "Checking…" else "Check")
            }
        }

        // Block interaction with a Lottie loader/error until the model is ready.
        if (recognizerState?.success != true) {
            RecognizerOverlay(isError = recognizerState?.error != null)
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
            Text(text = character, fontSize = 120.sp, fontWeight = FontWeight.Bold, color = INK_GREEN)
        }
    }
}

@Composable
private fun RecognizerOverlay(isError: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { awaitPointerEventScope { while (true) awaitPointerEvent() } }
            .background(Color.White.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center,
    ) {
        LottieViewer(assetName = if (isError) "something-wrong.lottie" else "baby-loading.lottie")
    }
}

private fun trackTitle(level: Level): String = when (level) {
    Level.UPPERCASE, Level.LOWERCASE -> "Alphabets"
    Level.NUMBERS -> "Numbers"
}
