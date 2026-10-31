package com.vi5hnu.curvykids.ui.activities.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.components.pressScale
import com.vi5hnu.curvykids.ui.components.rememberPressInteractionSource
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.TintGreen

private val WrongRed = Color(0xFFFF6B6B)
private val TintRed = Color(0xFFFFECEC)
private val TileShape = RoundedCornerShape(26.dp)

/**
 * One answer a child can tap. Shows what happened to it: green with a tick when right, a shake
 * and red when wrong, and a gently pulsing green with a pointing hand when it's the answer being
 * shown after a couple of misses. The other answers fade back while that happens.
 *
 * Fills whatever size it's given; [QuizScaffold] sizes the tiles to use the screen.
 */
@Composable
fun ChoiceTile(
    state: ChoiceState,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val border by animateColorAsState(
        when (state) {
            ChoiceState.CORRECT, ChoiceState.REVEALED -> Green
            ChoiceState.WRONG -> WrongRed
            else -> Color.Transparent
        },
        label = "tileBorder",
    )
    val background by animateColorAsState(
        when (state) {
            ChoiceState.CORRECT, ChoiceState.REVEALED -> TintGreen
            ChoiceState.WRONG -> TintRed
            else -> Color.White
        },
        label = "tileBackground",
    )

    // A wrong tap shakes the tile side to side once.
    val shake = remember { Animatable(0f) }
    LaunchedEffect(state) {
        if (state == ChoiceState.WRONG) {
            for (x in listOf(14f, -12f, 9f, -6f, 3f, 0f)) shake.animateTo(x, tween(45))
        }
    }

    // The revealed answer breathes so the eye goes to it.
    val pulse = rememberInfiniteTransition(label = "reveal")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "revealScale",
    )
    val scale = if (state == ChoiceState.REVEALED) pulseScale else 1f

    val interaction = rememberPressInteractionSource()
    Surface(
        onClick = onClick,
        interactionSource = interaction,
        modifier = modifier
            .graphicsLayer { translationX = shake.value; scaleX = scale; scaleY = scale }
            .alpha(if (state == ChoiceState.DIMMED) 0.35f else 1f)
            .pressScale(interaction)
            .semantics { this.contentDescription = contentDescription },
        shape = TileShape,
        color = background,
        border = BorderStroke(5.dp, border),
        shadowElevation = 4.dp,
    ) {
        Box(Modifier.fillMaxSize().padding(10.dp), contentAlignment = Alignment.Center) {
            content()
            val badge = when (state) {
                ChoiceState.CORRECT -> KidIcon.Check
                ChoiceState.REVEALED -> KidIcon.Pointer
                ChoiceState.WRONG -> KidIcon.Cross
                else -> null
            }
            if (badge != null) {
                Surface(
                    shape = CircleShape,
                    color = if (state == ChoiceState.WRONG) WrongRed else Green,
                    modifier = Modifier.align(Alignment.TopEnd).size(30.dp),
                ) {
                    KidIconImage(badge, Modifier.padding(6.dp), color = Color.White)
                }
            }
        }
    }
}

/** A picture filling most of the tile, with an optional [label] under it. */
@Composable
fun ChoiceArt(asset: AssetPath, label: String? = null, labelColor: Color = Ink) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        SvgArt(asset, modifier = Modifier.fillMaxWidth().weight(1f))
        if (label != null) {
            Text(
                label,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** Big text as the answer: a number, a letter, a short word. */
@Composable
fun ChoiceText(text: String, color: Color, large: Boolean = true) {
    Text(
        text,
        fontFamily = FontDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = if (large) 52.sp else 24.sp,
        color = color,
        textAlign = TextAlign.Center,
        maxLines = 2,
    )
}
