package com.vi5hnu.curvykids.ui.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.SvgArt

/**
 * [count] copies of a picture for a child to count, in rows of up to [perRow]. The last
 * [crossedOut] are faded and struck through, for "take away" questions.
 *
 * Pictures are sized so the whole group is countable at a glance; tiny icons in a row of ten are
 * where young children lose their place.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountingPictures(
    count: Int,
    picture: AssetPath,
    modifier: Modifier = Modifier,
    crossedOut: Int = 0,
    perRow: Int = 5,
    size: Dp = 44.dp,
) {
    FlowRow(
        maxItemsInEachRow = perRow,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        repeat(count) { i ->
            val crossed = i >= count - crossedOut
            Box(contentAlignment = Alignment.Center) {
                SvgArt(picture, modifier = Modifier.size(size).alpha(if (crossed) 0.35f else 1f))
                if (crossed) {
                    KidIconImage(KidIcon.Cross, Modifier.size(size * 0.6f), color = Color(0xFFFF6B6B))
                }
            }
        }
    }
}
