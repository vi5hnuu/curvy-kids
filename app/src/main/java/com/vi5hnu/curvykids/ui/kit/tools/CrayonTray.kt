package com.vi5hnu.curvykids.ui.kit.tools

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.data.content.Crayon
import com.vi5hnu.curvykids.ui.components.SvgImage

/**
 * The crayon box: one real-looking crayon per colour. The picked crayon is pulled up out of the
 * row and drawn bigger, the way a child lifts the one they're using, so "which colour am I on"
 * needs no reading.
 *
 * @param vertical a column down the side of the page (landscape), crayons pulled to the right.
 */
@Composable
fun CrayonTray(
    crayons: List<Crayon>,
    selected: Color,
    onPick: (Crayon) -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    crayonSize: Dp = 48.dp,
    /** How a horizontal tray spaces its crayons; spread them to fill a row of their own. */
    spacing: Arrangement.Horizontal = Arrangement.spacedBy(2.dp),
) {
    val items: @Composable () -> Unit = {
        crayons.forEach { crayon ->
            val picked = crayon.color == selected
            val lift by animateDpAsState(if (picked) crayonSize * 0.22f else 0.dp, label = "crayonLift")
            SvgImage(
                asset = crayon.art,
                contentDescription = null,
                modifier = Modifier
                    .size(if (picked) crayonSize * 1.15f else crayonSize)
                    .offset(x = if (vertical) lift else 0.dp, y = if (vertical) 0.dp else -lift)
                    .semantics {
                        contentDescription = "${crayon.name} crayon"
                        this.selected = picked
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                    ) { onPick(crayon) },
            )
        }
    }
    if (vertical) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp), horizontalAlignment = Alignment.Start) { items() }
    } else {
        Row(modifier, horizontalArrangement = spacing, verticalAlignment = Alignment.Bottom) { items() }
    }
}
