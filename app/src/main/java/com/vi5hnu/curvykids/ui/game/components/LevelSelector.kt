package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.data.content.Level

/** Track switcher: ABC / abc / 123. Highlights the active [selected] level. */
@Composable
fun LevelSelector(
    selected: Level,
    onSelect: (Level) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        Level.entries.forEach { level ->
            FilterChip(
                selected = level == selected,
                onClick = { onSelect(level) },
                label = { Text(level.label) },
                colors = FilterChipDefaults.filterChipColors(),
            )
        }
    }
}
