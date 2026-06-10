package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Level

private val MASTERED_BG = Color(0xFF66BB6A)
private val UNMASTERED_BG = Color(0xFFEEEEEE)
private val CURRENT_RING = Color(0xFF42A5F5)

/**
 * Bottom sheet that shows a grid of every character in [level], coloured green when mastered.
 * Tapping a cell navigates directly to that character and dismisses the sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressMapSheet(
    level: Level,
    currentIndex: Int,
    masteredCharacters: Set<String>,
    onCharacterSelect: (index: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val characters = level.characters
    val masteredCount = characters.count { it in masteredCharacters }
    val pct = if (characters.isEmpty()) 0 else masteredCount * 100 / characters.size

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = "Your Progress  ${level.label}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF3F51B5),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$masteredCount / ${characters.size} mastered ($pct%)",
                fontSize = 14.sp,
                color = Color(0xFF757575),
            )
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(if (level == Level.NUMBERS) 5 else 7),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(characters) { index, char ->
                    CharacterCell(
                        character = char,
                        isMastered = char in masteredCharacters,
                        isCurrent = index == currentIndex,
                        onClick = {
                            onCharacterSelect(index)
                            onDismiss()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterCell(
    character: String,
    isMastered: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit,
) {
    // Outer ring highlights the currently active character.
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (isCurrent) CURRENT_RING else Color.Transparent)
            .padding(if (isCurrent) 3.dp else 0.dp)
            .clip(CircleShape)
            .background(if (isMastered) MASTERED_BG else UNMASTERED_BG)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = character,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = if (isMastered) Color.White else Color(0xFF9E9E9E),
        )
    }
}
