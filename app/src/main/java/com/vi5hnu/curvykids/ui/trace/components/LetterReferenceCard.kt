package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.Phonics
import com.vi5hnu.curvykids.data.content.letterExample
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.kit.MarkedWord
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/**
 * What the reference card shows for a character: its word, the picture of that word, and the
 * part of the word to mark. Letters (English and Hindi) come from [letterExample], so the card
 * matches what the voice says ("A for Apple", "X, as in Fox", "क से कबूतर"); digits keep their
 * number word ("One") with nothing marked.
 */
private data class Reference(val word: String, val picture: AssetPath?, val mark: IntRange)

private fun referenceFor(ch: String): Reference? =
    letterExample(ch)?.let { Reference(it.word, it.picture, it.mark) }
        ?: Phonics.wordFor(ch)?.let { Reference(it, Phonics.assetFor(ch), IntRange.EMPTY) }

/**
 * The letter being traced with its example: the letter, a large picture of its word, and the
 * word with the letter marked where it sits, plus a speaker that says the same line.
 *
 * @param tall the landscape layout, where this card fills a column beside the writing area and
 *        the picture grows into it. Otherwise a single row above the writing area.
 */
@Composable
fun LetterReferenceCard(
    ch: String,
    color: Color,
    tint: Color,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
    tall: Boolean = false,
) {
    val ref = referenceFor(ch)
    Surface(shape = RoundedCornerShape(30.dp), color = Color.White, shadowElevation = 6.dp, modifier = modifier) {
        if (tall) {
            // Letter and speaker down the left; the picture fills what height is left, with
            // its word under it. Short landscape phones and tall tablets both fit.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    LetterTile(ch, color, tint)
                    SpeakButton(tint, onSpeak)
                }
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                ) {
                    if (ref != null) {
                        ref.picture?.let {
                            SvgArt(it, Modifier.weight(1f, fill = false).sizeIn(maxWidth = 160.dp, maxHeight = 160.dp).aspectRatio(1f), contentDescription = ref.word)
                        }
                        MarkedWord(ref.word, ref.mark, color, fontSize = 32.sp)
                    }
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(14.dp),
            ) {
                LetterTile(ch, color, tint)
                if (ref != null) {
                    ref.picture?.let { SvgArt(it, Modifier.size(78.dp), contentDescription = ref.word) }
                    Box(Modifier.weight(1f)) { MarkedWord(ref.word, ref.mark, color, fontSize = 30.sp) }
                } else {
                    Box(Modifier.weight(1f))
                }
                SpeakButton(tint, onSpeak)
            }
        }
    }
}

@Composable
private fun LetterTile(ch: String, color: Color, tint: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = tint, modifier = Modifier.size(74.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = ch, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, color = color)
        }
    }
}

@Composable
private fun SpeakButton(tint: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = CircleShape, color = tint, shadowElevation = 2.dp, modifier = Modifier.size(64.dp)) {
        Box(contentAlignment = Alignment.Center) {
            KidIconImage(KidIcon.Speaker, Modifier.size(28.dp))
        }
    }
}
