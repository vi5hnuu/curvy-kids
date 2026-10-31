package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.vi5hnu.curvykids.ui.components.pressScale
import com.vi5hnu.curvykids.ui.components.rememberPressInteractionSource
import com.vi5hnu.curvykids.ui.theme.KidDimens

/**
 * A raised, chunky card with a solid darker edge underneath, like a toy block. The edge is what
 * tells a child "this can be pressed"; it shrinks a little while held.
 *
 * @param color Face colour; the edge is a darker shade of it.
 * @param label Read by TalkBack; children hear the spoken name from [onClick]'s caller instead.
 */
@Composable
fun CandyCard(
    color: Color,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    radius: Dp = KidDimens.CardRadius,
    edge: Dp = KidDimens.CandyEdge,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    val interaction = rememberPressInteractionSource()
    Box(
        modifier
            .pressScale(interaction)
            .semantics { contentDescription = label }
            .clip(shape)
            .background(darker(color))
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = edge)
                .clip(shape)
                .background(color),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/** The shade used for a candy edge or a pressed state. */
fun darker(color: Color, by: Float = 0.18f) = Color(
    red = color.red * (1 - by),
    green = color.green * (1 - by),
    blue = color.blue * (1 - by),
    alpha = color.alpha,
)
