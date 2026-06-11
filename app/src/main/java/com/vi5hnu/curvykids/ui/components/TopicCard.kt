package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
 * 2-column topic card shown on the Home and Play screens.
 * Tint background, colored glyph badge, title and subtitle.
 */
@Composable
fun TopicCard(
    topic: Topic,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = topic.tint,
        shadowElevation = 3.dp,
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, top = 16.dp, end = 14.dp, bottom = 14.dp)) {
            // Glyph badge
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = topic.color,
                shadowElevation = 2.dp,
                modifier = Modifier.size(54.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = topic.glyph,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (topic.glyph.length >= 2 && topic.glyph.all { it.isLetterOrDigit() }) 24.sp else 28.sp,
                        color = Color.White,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = topic.title,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = Ink,
                lineHeight = 18.sp,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = topic.sub,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = InkSoft,
            )
        }
    }
}
