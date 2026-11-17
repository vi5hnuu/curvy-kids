package com.vi5hnu.curvykids.ui.pro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidInk

/**
 * The slim white pill Home uses to tell a grown-up something (a Pro discount, being offline):
 * an icon, a bold line and a softer one, and a close button. Quiet on purpose: never spoken,
 * no animation, under the counters and away from the child's big targets.
 *
 * @param icon drawn on a [iconColor] circle, about 18 dp.
 * @param maxBodyLines the softer line's height; a sale line is one, an explanation may need two.
 * @param hideLabel what the close button says to a screen reader.
 */
@Composable
internal fun GrownUpNotice(
    iconColor: Color,
    icon: @Composable () -> Unit,
    title: AnnotatedString,
    body: String,
    hideLabel: String,
    onOpen: () -> Unit,
    onHide: () -> Unit,
    modifier: Modifier = Modifier,
    maxBodyLines: Int = 1,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (maxBodyLines > 1) 24.dp else 50.dp))
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onOpen)
            .padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
    ) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(iconColor), contentAlignment = Alignment.Center) {
            icon()
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = KidInk,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Text(
                body,
                fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KidInk.copy(alpha = 0.6f),
                maxLines = maxBodyLines, overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onHide) {
            Icon(Icons.Rounded.Close, contentDescription = hideLabel, tint = KidInk.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp))
        }
    }
}
