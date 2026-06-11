package com.vi5hnu.curvykids.ui.trace.components

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

/**
 * Bottom sheet showing all characters for the current topic in a 5-column grid.
 * Current character is highlighted in topic.color; mastered chars show a ⭐;
 * others are white with dark text. Matches CharStrip from trace.jsx.
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

            // 5-column grid
            val rows = chars.chunked(5)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                rows.forEachIndexed { chunkIdx, row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        row.forEachIndexed { rowIdx, ch ->
                            // Use positional index (chunk * 5 + offset) rather than indexOf()
                            // so duplicate characters (if any) navigate to the correct slot.
                            val globalIdx = chunkIdx * 5 + rowIdx
                            val isCurrent = globalIdx == currentIndex
                            val isMastered = mastered.contains(ch)

                            Surface(
                                onClick = { onPick(globalIdx) },
                                modifier = Modifier
                                    .weight(1f)
                                    .size(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = when {
                                    isCurrent  -> topic.color
                                    isMastered -> topic.tint
                                    else       -> Color.White
                                },
                                shadowElevation = 3.dp,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ch,
                                        fontFamily = FontDisplay,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 24.sp,
                                        color = when {
                                            isCurrent  -> Color.White
                                            isMastered -> topic.color
                                            else       -> Ink
                                        },
                                    )
                                    if (isMastered && !isCurrent) {
                                        Text(
                                            text = "⭐",
                                            fontSize = 12.sp,
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(end = 4.dp, top = 2.dp),
                                        )
                                    }
                                }
                            }
                        }
                        // Fill remainder of last row
                        repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
