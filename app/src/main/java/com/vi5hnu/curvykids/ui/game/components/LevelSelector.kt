package com.vi5hnu.curvykids.ui.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Level

private data class LevelStyle(val emoji: String, val color: Color)

private fun styleFor(level: Level): LevelStyle = when (level) {
    Level.UPPERCASE -> LevelStyle("🔠", Color(0xFFFF8A65))
    Level.LOWERCASE -> LevelStyle("🔡", Color(0xFF4DB6AC))
    Level.NUMBERS -> LevelStyle("🔢", Color(0xFFBA68C8))
}

/** Big, colourful track switcher: 🔠 ABC / 🔡 abc / 🔢 123. */
@Composable
fun LevelSelector(
    selected: Level,
    onSelect: (Level) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Level.entries.forEach { level ->
            val style = styleFor(level)
            val isSelected = level == selected
            Button(
                onClick = { onSelect(level) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) style.color else Color.White,
                    contentColor = if (isSelected) Color.White else style.color,
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
            ) {
                Text(
                    text = "${style.emoji} ${level.label}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(2.dp),
                )
            }
        }
    }
}
