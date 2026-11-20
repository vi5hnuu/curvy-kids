package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.HindiLetter
import com.vi5hnu.curvykids.data.content.Matra
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import com.vi5hnu.curvykids.recognition.Stroke
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.theme.BgTop
import com.vi5hnu.curvykids.ui.theme.KidDimens
import com.vi5hnu.curvykids.ui.trace.components.WritePad

/**
 * One syllable, opened for writing from the explainer's pencil.
 *
 * A full sheet rather than a mode on the grid, so it needs no label to be understood. The
 * equation stays at the top as a reminder of what is being written (क + आ = का), with the word
 * the syllable starts and its picture under it; the line is said as the child starts writing.
 * Arrows at the foot move along the twelve forms so a child can work through them.
 */
@Composable
fun WriteSheet(
    consonant: HindiLetter,
    matra: Matra,
    onCheck: (suspend (List<Stroke>) -> Boolean?)?,
    onResult: (Boolean) -> Unit,
    onSpeak: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onDrawn: () -> Unit,
    /** Says a tool's word ("Rubbed out!") when the child taps it. */
    onTool: (String) -> Unit = {},
) {
    val syllable = barakhadiSyllable(consonant.char, matra)
    val color = matraColor(matra)
    val tint = matraTint(matra)
    Surface(color = BgTop, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(KidDimens.ScreenPadding),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // A cross, not an arrow: the arrows at the foot of the page step between the
                // twelve forms, and the same picture must not mean two different things.
                RoundButton(KidIcon.Cross, color, tint, onClose)
                Spacer(Modifier.width(14.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    MatraEquation(consonant.char, matra, syllable, tile = 48.dp)
                }
                Spacer(Modifier.width(14.dp))
                RoundButton(KidIcon.Speaker, color, tint, onSpeak)
            }
            // The word this syllable starts, with its picture: what the voice says while writing.
            SyllableWord(
                consonant, matra,
                pictureSize = 64.dp, fontSize = 30.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp),
            )
            Spacer(Modifier.size(14.dp))
            WritePad(
                char = syllable,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onDrawn = onDrawn,
                onCheck = onCheck,
                onResult = onResult,
                // Say the syllable's line as the child starts writing it.
                onFirstStroke = onSpeak,
                onTool = onTool,
            )
            Spacer(Modifier.size(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RoundButton(KidIcon.ArrowBack, color, tint, onPrev)
                RoundButton(KidIcon.ArrowForward, color, tint, onNext)
            }
        }
    }
}

/** A large round control, sized for a small hand. */
@Composable
private fun RoundButton(icon: KidIcon, color: Color, tint: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = tint,
        modifier = Modifier.size(64.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            KidIconImage(icon, Modifier.size(28.dp), color)
        }
    }
}
