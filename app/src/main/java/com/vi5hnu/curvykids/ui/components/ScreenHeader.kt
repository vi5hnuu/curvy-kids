package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.vi5hnu.curvykids.ui.kit.KidBackButton
import com.vi5hnu.curvykids.ui.theme.KidInk
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink

/**
 * Shared top-of-screen header used by all activity screens.
 *
 * @param title    Screen title shown in the accent color.
 * @param color    Accent color for the title text.
 * @param onBack   If non-null, shows a back chip on the left.
 * @param trailing Optional composable placed on the right (progress pill, score, etc.).
 */
@Composable
fun ScreenHeader(
    title: String,
    color: Color = Ink,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            // Big candy back button: the one control every child screen shares.
            KidBackButton(onClick = onBack, color = color)
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = KidInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (trailing != null) trailing()
    }
}
