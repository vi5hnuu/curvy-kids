package com.vi5hnu.curvykids.ui.activities.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * A pill-shaped segmented control (e.g. "Learn" / "Play"). The selected segment is filled
 * with [accent]; the rest sit on the light track. Reused by all Discover activity screens so
 * the toggle looks and behaves identically everywhere.
 *
 * @param tabs     Segment labels in order.
 * @param selected Index of the currently-selected segment.
 * @param accent   Fill color of the selected segment (usually the topic color).
 * @param onSelect Invoked with the tapped segment's index.
 * @param icons    One picture per segment. The words on a toggle are no use to a child who
 *                 cannot read them, and this control is how they get between exploring a topic
 *                 and playing with it - so the picture carries the meaning and the word labels it.
 */
@Composable
fun SegmentedTabs(
    tabs: List<String>,
    selected: Int,
    accent: Color,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icons: List<KidIcon> = emptyList(),
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(999.dp),
        color = Color.White,
        shadowElevation = 3.dp,
    ) {
        Row(modifier = Modifier.padding(5.dp)) {
            tabs.forEachIndexed { i, label ->
                val isSelected = i == selected
                val bg by animateColorAsState(
                    targetValue = if (isSelected) accent else Color.Transparent,
                    label = "segBg",
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(bg)
                        .clickable { onSelect(i) }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        icons.getOrNull(i)?.let { icon ->
                            KidIconImage(
                                icon,
                                Modifier.size(19.dp),
                                if (isSelected) Color.White else InkSoft,
                            )
                        }
                        Text(
                            text = label,
                            textAlign = TextAlign.Center,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isSelected) Color.White else InkSoft,
                        )
                    }
                }
            }
        }
    }
}
