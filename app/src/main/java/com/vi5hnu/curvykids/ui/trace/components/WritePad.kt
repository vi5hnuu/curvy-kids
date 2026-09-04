package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.game.components.DrawingCanvas
import com.vi5hnu.curvykids.ui.game.components.rememberDrawingController

/**
 * A surface for practising one character: it is shown being written, then the child writes it.
 *
 * Deliberately without a right-or-wrong check. The tracing screen can check a single letter
 * against the handwriting recogniser, but a barakhadi syllable is a consonant and a vowel sign
 * together and the recogniser is not dependable on those — telling a child their उ is wrong when
 * it is not would teach them to distrust the app. Here the demonstration is the teaching and the
 * page is theirs to practise on.
 *
 * @param char     The character to demonstrate and trace.
 * @param color    The topic's colour, used for the pen and the controls.
 * @param onDrawn  Called the first time the child puts pen to paper on this character.
 */
@Composable
fun WritePad(
    char: String,
    color: Color,
    modifier: Modifier = Modifier,
    onDrawn: () -> Unit = {},
) {
    val controller = rememberDrawingController()
    // Bumping this replays the demonstration; it also restarts whenever the character changes.
    var demoKey by rememberSaveable(char) { mutableIntStateOf(0) }
    var drawnHere by rememberSaveable(char) { mutableIntStateOf(0) }

    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (hasStrokeGuide(char)) {
                StrokeOrderDemo(
                    char = char,
                    color = color,
                    demoKey = demoKey,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                )
            } else {
                // No stroke order is known for this character, so show the shape to copy. The
                // reveal clears itself when it finishes, and without the letter behind it the
                // child would be left facing a blank page.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = char,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = GHOST_SIZE,
                        lineHeight = GHOST_SIZE,
                        color = GHOST_COLOR,
                    )
                }
                DemoWipe(
                    char = char,
                    color = color,
                    demoKey = demoKey,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                )
            }

            DrawingCanvas(
                controller = controller,
                modifier = Modifier.fillMaxSize(),
                inkColor = color,
                onDrawStart = {
                    if (drawnHere == 0) {
                        drawnHere = 1
                        onDrawn()
                    }
                },
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
            ) {
                PadButton(KidIcon.Undo, color) {
                    controller.clear()
                    demoKey++
                }
            }
        }
    }
}

/** The letter to copy, sized in sp but fixed against the OS font scale so it stays in the card. */
private val GHOST_SIZE = 190.sp

/** The same faint grey the stroke guide draws its untraced skeleton in. */
private val GHOST_COLOR = Color(0xFFE4EDF4)

/** A round control on the writing surface, sized for a small hand. */
@Composable
private fun PadButton(icon: KidIcon, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = color.copy(alpha = 0.14f),
        modifier = Modifier.size(48.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            KidIconImage(icon, Modifier.size(22.dp), color)
        }
    }
}
