package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.recognition.Stroke
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarGold
import com.vi5hnu.curvykids.ui.game.components.DrawingCanvas
import com.vi5hnu.curvykids.ui.game.components.rememberDrawingController
import com.vi5hnu.curvykids.ui.kit.tools.DrawingPage
import com.vi5hnu.curvykids.ui.kit.tools.ToolArt
import com.vi5hnu.curvykids.ui.kit.tools.ToolButton
import com.vi5hnu.curvykids.ui.kit.tools.ToolVoice
import kotlinx.coroutines.launch

/**
 * A surface for practising one character: the letter is shown plainly and the child writes over it.
 *
 * @param char     The character to trace.
 * @param color    The topic's colour, used for the pen and the controls.
 * @param onDrawn  Called the first time the child puts pen to paper on this character.
 * @param onCheck  Reads the child's writing and says whether it is the character, or null when
 *                 nothing can read it — the model may not have been fetched. When it is null the
 *                 check is simply not offered, rather than the child being told they are wrong
 *                 by something that never looked.
 * @param onResult Called with the verdict, for the sound, the buzz and the reward.
 * @param onFirstStroke Called on the first line drawn for each character, so the screen can say
 *                 the character while it is being written.
 * @param onTool   Called with the short word for a tool the child tapped ([ToolVoice]), to say.
 */
@Composable
fun WritePad(
    char: String,
    color: Color,
    modifier: Modifier = Modifier,
    onDrawn: () -> Unit = {},
    onCheck: (suspend (List<Stroke>) -> Boolean?)? = null,
    onResult: (Boolean) -> Unit = {},
    onFirstStroke: () -> Unit = {},
    onTool: (String) -> Unit = {},
) {
    val controller = rememberDrawingController()
    var drawnHere by rememberSaveable(char) { mutableIntStateOf(0) }

    var verdict by remember(char) { mutableStateOf<Boolean?>(null) }
    var checking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // The controller outlives the character, so without this the child's writing of one letter
    // is still on the page when they move to the next one.
    LaunchedEffect(char) { controller.clear() }

    Surface(
        shape = RoundedCornerShape(30.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = modifier,
    ) {
        DrawingPage(
            modifier = Modifier.fillMaxSize(),
            onErase = {
                controller.undo()
                verdict = null
                onTool(ToolVoice.ERASER)
            },
            onNewPage = {
                controller.clear()
                verdict = null
                onTool(ToolVoice.NEW_PAGE)
            },
            trailing = {
                if (onCheck != null) {
                    ToolButton(ToolArt.Tick, "Check my writing", onClick = {
                        if (!checking && controller.hasDrawing) {
                            checking = true
                            scope.launch {
                                val read = onCheck(controller.snapshot())
                                checking = false
                                if (read != null) {
                                    verdict = read
                                    onResult(read)
                                }
                            }
                        }
                    })
                }
            },
        ) {
            GhostLetter(char = char, modifier = Modifier.fillMaxSize())

            DrawingCanvas(
                controller = controller,
                modifier = Modifier.fillMaxSize(),
                inkColor = color,
                onDrawStart = {
                    if (drawnHere == 0) {
                        drawnHere = 1
                        onDrawn()
                        onFirstStroke()
                    }
                },
            )

            // The verdict, as a picture rather than a word.
            verdict?.let { correct ->
                KidIconImage(
                    if (correct) KidIcon.Star else KidIcon.Pointer,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .size(40.dp),
                    if (correct) StarGold else color,
                )
            }
        }
    }
}
