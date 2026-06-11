package com.vi5hnu.curvykids.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Round icon chip — white background, soft shadow, scale on press.
 * Matches the `.chip` CSS class in the design.
 */
@Composable
fun Chip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    containerColor: Color = Color.White,
    contentColor: Color = Color(0xFF758999),
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * "Candy button" — chunky rounded rectangle with bottom-drop shadow effect.
 * Matches the `.cbtn` CSS class in the design.
 */
@Composable
fun CandyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = Color(0xFF4FCB94),
    contentColor: Color = Color.White,
    cornerRadius: Dp = 22.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 6.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * White card surface with large rounded corners and shadow.
 * Matches the `.card` CSS class in the design.
 */
@Composable
fun CardSurface(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(30.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            content = { content() },
        )
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(30.dp),
            color = Color.White,
            shadowElevation = 6.dp,
            content = { content() },
        )
    }
}

/**
 * Pill-shaped badge / label row.
 * Matches the `.pill` CSS class in the design.
 */
@Composable
fun Pill(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(999.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            content = { content() },
        )
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(999.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            content = { content() },
        )
    }
}
