package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.HindiLetter
import com.vi5hnu.curvykids.data.content.Matra
import com.vi5hnu.curvykids.data.content.SyllableHint
import com.vi5hnu.curvykids.data.content.barakhadiHint
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import com.vi5hnu.curvykids.data.content.hindiWordPicture
import com.vi5hnu.curvykids.data.content.syllableSpan
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.kit.CandyCard
import com.vi5hnu.curvykids.ui.kit.MarkedWord
import com.vi5hnu.curvykids.ui.theme.KidDimens

/**
 * Everything about one syllable, shown the way its recording says it:
 *  1. how it is built, as [MatraEquation] (क + आ = का);
 *  2. what the narration pairs it with ([SyllableHint]):
 *     - "का से कान": the word's picture, when drawn, and the word with का marked;
 *     - "ख और ऐ मिलकर बना खै": no everyday word, so the equation itself is the lesson and is
 *       shown larger;
 *     - "ङ जैसे गङ्गा में": the word with the letter marked where it sits inside it;
 *  3. a speaker to hear it again and a pencil to write it.
 *
 * @param compact a shorter layout, for the landscape pane where height is scarce.
 */
@Composable
fun SyllableExplainer(
    consonant: HindiLetter,
    matra: Matra,
    onSpeak: () -> Unit,
    onWrite: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val syllable = barakhadiSyllable(consonant.char, matra)
    val hint = barakhadiHint(syllable)
    val color = matraColor(matra)
    val tile: Dp = if (compact) 46.dp else 64.dp

    val lesson: @Composable () -> Unit = {
        val joined = hint is SyllableHint.Joined || hint == null
        MatraEquation(consonant.char, matra, syllable, tile = if (joined) tile * 1.18f else tile)

        SyllableWord(consonant, matra, pictureSize = if (compact) 84.dp else 120.dp, fontSize = if (compact) 34.sp else 44.sp)
    }
    val actions: @Composable () -> Unit = {
        ActionButton(KidIcon.Speaker, color, syllable, onSpeak)
        ActionButton(KidIcon.Pencil, color, syllable, onWrite)
    }

    if (compact) {
        // Short pane: the buttons stand beside the lesson instead of under it.
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) { lesson() }
            Column(verticalArrangement = Arrangement.spacedBy(KidDimens.Gap)) { actions() }
        }
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(KidDimens.Gap),
        ) {
            lesson()
            Row(horizontalArrangement = Arrangement.spacedBy(KidDimens.Gap)) { actions() }
        }
    }
}

/**
 * The word the syllable's recording pairs it with, shown beside its picture with the syllable
 * marked: (ear) **का**न. Nothing for a "joined" syllable, which has no everyday word; there the
 * equation is the lesson. Shared by the explainer and the write sheet so both show the same.
 */
@Composable
fun SyllableWord(
    consonant: HindiLetter,
    matra: Matra,
    modifier: Modifier = Modifier,
    pictureSize: Dp = 120.dp,
    fontSize: TextUnit = 44.sp,
) {
    val syllable = barakhadiSyllable(consonant.char, matra)
    val word = when (val hint = barakhadiHint(syllable)) {
        is SyllableHint.Word -> hint.word
        is SyllableHint.AsIn -> hint.word
        is SyllableHint.Joined, null -> null
    } ?: return
    // Picture beside the word, not above it: keeps the card short, and reads as "this is the
    // word" either way.
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(KidDimens.Gap)) {
        hindiWordPicture(word)?.let { SvgArt(it, Modifier.size(pictureSize), contentDescription = word) }
        MarkedWord(word, markOf(word, syllable, consonant.char), matraColor(matra), fontSize = fontSize)
    }
}

/**
 * Where [syllable] sits in [word], widened to whole syllables. Falls back to the bare consonant
 * (ङ inside गङ्गा) and then to nothing, so a word the narration spells differently still shows.
 */
private fun markOf(word: String, syllable: String, consonant: String): IntRange {
    val at = word.indexOf(syllable).takeIf { it >= 0 }
    if (at != null) return syllableSpan(word, at, syllable.length)
    val c = word.indexOf(consonant)
    return if (c >= 0) syllableSpan(word, c, consonant.length) else IntRange.EMPTY
}

@Composable
private fun ActionButton(icon: KidIcon, color: Color, label: String, onClick: () -> Unit) {
    CandyCard(color = color, onClick = onClick, label = label, radius = 24.dp, modifier = Modifier.size(KidDimens.MinTarget)) {
        KidIconImage(icon, Modifier.size(32.dp), Color.White)
    }
}
