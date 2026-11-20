package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HindiLetter
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarGold
import com.vi5hnu.curvykids.ui.kit.CandyCard
import com.vi5hnu.curvykids.ui.theme.KidInk

/**
 * The twelve forms of one consonant (क का कि … कः) as candy cards in barakhadi order.
 *
 * Each card shows the syllable large with its sign beneath on a chip in the matra's colour, so
 * reading along the grid the child sees the same letter with a different coloured sign each time.
 * The card being explained, or recited, turns solid in that colour; a star marks forms already
 * heard.
 *
 * @param active the form being explained, or -1.
 * @param reciting the form being recited, or -1; it also grows slightly so it can be followed.
 */
@Composable
fun SyllableGrid(
    consonant: HindiLetter,
    explored: Set<String>,
    active: Int,
    reciting: Int,
    columns: Int,
    onTap: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BARAKHADI_MATRAS.indices.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { i ->
                    val matra = BARAKHADI_MATRAS[i]
                    val syllable = barakhadiSyllable(consonant.char, matra)
                    SyllableCard(
                        syllable = syllable,
                        sign = matraSignLabel(matra),
                        color = matraColor(matra),
                        tint = matraTint(matra),
                        lit = i == active || i == reciting,
                        grow = i == reciting,
                        heard = syllable in explored,
                        onClick = { onTap(i) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SyllableCard(
    syllable: String,
    sign: String,
    color: Color,
    tint: Color,
    lit: Boolean,
    grow: Boolean,
    heard: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.aspectRatio(1f).scale(if (grow) 1.08f else 1f)) {
        val cardSide = maxWidth
        CandyCard(
            color = if (lit) color else Color.White,
            onClick = onClick,
            label = syllable,
            radius = 24.dp,
        ) {
            // Type follows the card, so the same grid works from a landscape phone to a tablet.
            val side = cardSide.value
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = syllable,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (side * 0.36f).sp,
                    lineHeight = (side * 0.44f).sp,
                    color = if (lit) Color.White else KidInk,
                )
                // अ has no sign: an empty chip of the same height keeps the row of letters level.
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (sign.isEmpty()) Color.Transparent else if (lit) Color.White else tint)
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = sign.ifEmpty { " " },
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = (side * 0.16f).sp,
                        lineHeight = (side * 0.22f).sp,
                        color = color,
                    )
                }
            }
        }
        if (heard) {
            KidIconImage(KidIcon.Star, Modifier.align(Alignment.TopEnd).padding(8.dp).size(16.dp), StarGold)
        }
    }
}
