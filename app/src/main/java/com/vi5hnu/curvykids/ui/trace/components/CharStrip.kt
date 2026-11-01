package com.vi5hnu.curvykids.ui.trace.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.data.content.letterExample
import com.vi5hnu.curvykids.ui.components.SvgImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarGold
import com.vi5hnu.curvykids.ui.components.KidIcon

/**
 * Bottom sheet showing every character of the current topic as an alphabet chart: each card is
 * the character with a picture of its word (A with an apple, क with a pigeon, 3 with three dots),
 * since a bare character means little to a child who can't read yet. The current character is
 * filled with topic.color; mastered ones are tinted and starred.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharStrip(
    topic: Topic,
    chars: List<String>,
    currentIndex: Int,
    mastered: Set<String>,
    onPick: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = topic.title,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Ink,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tap any to jump there",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = InkSoft,
            )
            Spacer(Modifier.height(14.dp))

            // Alphabet-chart grid: each card is the character with a picture of its word.
            BoxWithConstraints {
                val columns = if (maxWidth >= 600.dp) 6 else 4
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    chars.chunked(columns).forEachIndexed { chunkIdx, row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            row.forEachIndexed { rowIdx, ch ->
                                // Use positional index rather than indexOf() so duplicate
                                // characters (if any) navigate to the correct slot.
                                val globalIdx = chunkIdx * columns + rowIdx
                                ChartCard(
                                    ch = ch,
                                    topic = topic,
                                    isCurrent = globalIdx == currentIndex,
                                    isMastered = mastered.contains(ch),
                                    onClick = { onPick(globalIdx) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            // Fill remainder of last row
                            repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

/** The picture for [ch] on the chart: its example word's picture, or a counting card for a digit. */
private fun chartPicture(ch: String): AssetPath? =
    letterExample(ch)?.picture
        ?: ch.toIntOrNull()?.takeIf { it in 1..10 }?.let { KidAssets.counting(it) }

@Composable
private fun ChartCard(
    ch: String,
    topic: Topic,
    isCurrent: Boolean,
    isMastered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.aspectRatio(0.82f),
        shape = RoundedCornerShape(18.dp),
        color = when {
            isCurrent -> topic.color
            isMastered -> topic.tint
            else -> Color.White
        },
        shadowElevation = 3.dp,
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize().padding(6.dp),
            ) {
                Text(
                    text = ch,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    color = when {
                        isCurrent -> Color.White
                        isMastered -> topic.color
                        else -> Ink
                    },
                )
                chartPicture(ch)?.let { pic ->
                    // A white disc keeps the picture readable on the coloured current card.
                    Box(
                        Modifier.fillMaxWidth(0.62f).aspectRatio(1f).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        SvgImage(pic, Modifier.fillMaxSize(0.82f), contentDescription = null)
                    }
                }
            }
            if (isMastered && !isCurrent) {
                KidIconImage(
                    icon = KidIcon.Star,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 6.dp, top = 6.dp)
                        .size(14.dp),
                    color = StarGold,
                )
            }
        }
    }
}
