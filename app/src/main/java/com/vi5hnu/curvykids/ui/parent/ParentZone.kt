package com.vi5hnu.curvykids.ui.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.TintSun
import com.vi5hnu.curvykids.ui.theme.TintTeal

/**
 * ParentZone bottom sheet — shows stars earned, items mastered, and setting toggles.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentZone(
    appState: AppUiState,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 32.dp)) {
            Text(
                text = "Parent Zone",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Ink,
            )
            Spacer(Modifier.height(14.dp))

            // ── Stat tiles ─────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                StatTile(
                    label = "Stars earned",
                    value = "${appState.stars}",
                    tint = TintSun,
                    valueColor = Color(0xFFF2A93B),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = "Items mastered",
                    value = "${appState.mastered.size}",
                    tint = TintTeal,
                    valueColor = Teal,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(18.dp))

            // ── Toggles ───────────────────────────────────────────────────
            ToggleRow(label = "Sound effects & voice", default = true)
            HorizontalDivider(color = InkFaint.copy(alpha = 0.3f))
            ToggleRow(label = "Background music", default = false)
            HorizontalDivider(color = InkFaint.copy(alpha = 0.3f))
            ToggleRow(label = "Daily play reminder", default = true)

            Spacer(Modifier.height(8.dp))

            CandyButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                containerColor = Coral,
            ) {
                Text(
                    text = "Done",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    tint: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = tint,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(14.dp),
        ) {
            Text(
                text = value,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = valueColor,
            )
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Ink.copy(alpha = 0.6f),
            )
        }
    }
}

@Composable
private fun ToggleRow(label: String, default: Boolean) {
    var on by remember { mutableStateOf(default) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Ink,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = on,
            onCheckedChange = { on = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Teal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = InkFaint,
            ),
        )
    }
}
