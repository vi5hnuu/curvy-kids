package com.vi5hnu.curvykids.ui.kit

import androidx.compose.foundation.border
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.components.pressScale
import com.vi5hnu.curvykids.ui.components.rememberPressInteractionSource
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidDimens
import com.vi5hnu.curvykids.ui.theme.KidInk
import kotlinx.coroutines.delay

/** The big round back button every child screen starts with. */
@Composable
fun KidBackButton(onClick: () -> Unit, color: Color, modifier: Modifier = Modifier) {
    CandyCard(
        color = Color.White,
        onClick = onClick,
        label = "Back",
        radius = KidDimens.HeaderButton / 2,
        edge = 4.dp,
        modifier = modifier.size(KidDimens.HeaderButton),
    ) {
        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = color,
            modifier = Modifier.size(34.dp))
    }
}

/**
 * The grown-ups' lock. A tap only shows "Hold" (a child taps, nothing happens); pressing and
 * holding opens it, which is easy for an adult and beyond a toddler's patience. Same rule the
 * old grown-ups tab used.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HoldToOpenLock(onOpen: () -> Unit, modifier: Modifier = Modifier) {
    var hinting by remember { mutableStateOf(false) }
    LaunchedEffect(hinting) {
        if (hinting) { delay(2000); hinting = false }
    }
    val interaction = rememberPressInteractionSource()
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(LOCK_SIZE)
                .pressScale(interaction)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.85f))
                .semantics { contentDescription = "Grown-ups. Press and hold to open." }
                .combinedClickable(
                    interactionSource = interaction,
                    indication = null,
                    role = Role.Button,
                    onClick = { hinting = true },
                    onLongClick = { hinting = false; onOpen() },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Lock, contentDescription = null, tint = KidInk.copy(alpha = 0.55f),
                modifier = Modifier.size(24.dp))
        }
        // In a popup, so it sits above whatever is laid out below the lock (the "play again"
        // card on Home) instead of being drawn under it. Kept until it has faded out.
        val shown = remember { MutableTransitionState(false) }
        shown.targetState = hinting
        if (shown.currentState || shown.targetState) {
            val below = with(LocalDensity.current) { (LOCK_SIZE + 6.dp).roundToPx() }
            Popup(alignment = Alignment.TopCenter, offset = IntOffset(0, below)) {
                AnimatedVisibility(shown, enter = fadeIn(), exit = fadeOut()) {
                    Text("Hold", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp,
                        color = KidInk,
                        // Outlined, as it can sit on a white card.
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color.White)
                            .border(1.5.dp, KidInk.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp))
                }
            }
        }
    }
}

private val LOCK_SIZE = 52.dp
