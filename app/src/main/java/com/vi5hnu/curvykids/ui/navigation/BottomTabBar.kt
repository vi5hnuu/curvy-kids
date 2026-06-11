package com.vi5hnu.curvykids.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.TintTeal

/**
 * Floating frosted-glass bottom tab bar.
 * Positioned inside a Box with padding: left/right 14dp, bottom 12dp, height 68dp.
 *
 * Spec from styles.css: borderRadius=26dp, background rgba(255,255,255,.86), blur(16px).
 */
@Composable
fun BottomTabBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(26.dp),
        color = Color.White.copy(alpha = 0.86f),
        shadowElevation = 10.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AppTab.entries.forEach { tab ->
                TabItem(
                    tab = tab,
                    selected = tab == currentTab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun TabItem(
    tab: AppTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) TintTeal else Color.Transparent,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = tab.icon.toVector(),
                contentDescription = tab.label,
                tint = if (selected) Teal else InkFaint,
                modifier = Modifier.size(26.dp),
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = tab.label,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (selected) Teal else InkFaint,
            )
        }
    }
}

private fun TabIcon.toVector(): ImageVector = when (this) {
    TabIcon.Home -> Icons.Rounded.Home
    TabIcon.Play -> Icons.Rounded.PlayArrow
    TabIcon.Star -> Icons.Rounded.Star
    TabIcon.Kid  -> Icons.Rounded.Person
}
