package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.HindiLetter
import com.vi5hnu.curvykids.ui.kit.CandyCard
import com.vi5hnu.curvykids.ui.theme.KidDimens

/**
 * The strip of 36 consonants to choose from. The picked letter is a solid candy block, the rest
 * are pale, so where you are is obvious without reading anything. Keeps the picked letter in
 * view when it changes (including after rotation).
 *
 * @param vertical a column down the side instead of a row, for landscape where height is short.
 */
@Composable
fun ConsonantPicker(
    consonants: List<HindiLetter>,
    selected: Int,
    color: Color,
    tint: Color,
    onPick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = (selected - 2).coerceAtLeast(0))
    LaunchedEffect(selected) {
        val visible = state.layoutInfo.visibleItemsInfo
        if (visible.none { it.index == selected } || visible.first().index == selected || visible.last().index == selected) {
            state.animateScrollToItem((selected - 2).coerceAtLeast(0))
        }
    }
    val items: LazyListScope.() -> Unit = {
        itemsIndexed(consonants, key = { _, c -> c.char }) { i, c ->
            val picked = i == selected
            CandyCard(
                color = if (picked) color else tint,
                onClick = { onPick(i) },
                label = c.char,
                radius = 20.dp,
                modifier = Modifier.size(KidDimens.MinTarget),
            ) {
                Text(
                    text = c.char,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    color = if (picked) Color.White else color,
                )
            }
        }
    }
    if (vertical) {
        LazyColumn(
            state = state,
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 2.dp),
            content = items,
        )
    } else {
        LazyRow(
            state = state,
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
            content = items,
        )
    }
}
