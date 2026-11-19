package com.vi5hnu.curvykids.ui.pro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.billing.LockMark
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.ConfettiOverlay
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.kit.ShimmerBox
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidGrape
import com.vi5hnu.curvykids.ui.theme.KidInk
import com.vi5hnu.curvykids.ui.theme.KidSun

/**
 * Celebrates Pro arriving while the app is open but away from the purchase screen (which has its
 * own celebration): a promo code redeemed in the Play Store, or a payment that cleared later.
 * The caller says [com.vi5hnu.curvykids.data.content.EnglishVoiceScript.PRO_UNLOCKED] as it opens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockedSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
            ) {
                CurvyMascot(size = 110.dp, mood = CurvyMood.Cheer, floating = true)
                Spacer(Modifier.height(12.dp))
                Text("Pro unlocked!", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp, color = KidGrape, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text("Everything is open now.", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    color = KidInk.copy(alpha = 0.7f), textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
                CandyButton(onClick = onDismiss, containerColor = KidGrape,
                    modifier = Modifier.fillMaxWidth().height(64.dp)) {
                    Text("Let's play!", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                }
            }
            ConfettiOverlay(modifier = Modifier.matchParentSize())
        }
    }
}

/** The small padlock on a locked tile, letter or story. */
@Composable
fun LockBadge(modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Box(
        modifier.size(size).clip(CircleShape).background(KidGrape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = Color.White, modifier = Modifier.size(size * 0.6f))
    }
}

/** On an item a watched video opened for today: a play mark, where the padlock was. */
@Composable
fun TodayBadge(modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Box(
        modifier.size(size).clip(CircleShape).background(KidSun),
        contentAlignment = Alignment.Center,
    ) {
        Text("▶", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = (size.value * 0.45f).sp)
    }
}

/**
 * The mark on a tile, letter or story, from [LockMark]: a padlock, a "today" mark, a shimmering
 * dot while ownership is still being read, a ring filling up while its voices download, a cloud
 * when that needs a grown-up, or nothing.
 */
@Composable
fun LockMarkBadge(mark: LockMark, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    when (mark) {
        LockMark.NONE -> Unit
        LockMark.LOADING -> ShimmerBox(size, size, modifier, CircleShape)
        LockMark.LOCKED -> LockBadge(modifier, size)
        LockMark.OPEN_TODAY -> TodayBadge(modifier, size)
        is LockMark.GettingReady -> GettingReadyBadge(mark.progress, modifier, size)
        LockMark.NEEDS_GROWN_UP -> NeedsGrownUpBadge(modifier, size)
    }
}

/**
 * On an open item whose voices are downloading: a ring that fills up, so a child who tapped it
 * sees something happening and can come back to it. Spins until Play reports a size.
 */
@Composable
fun GettingReadyBadge(progress: Float, modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Box(
        modifier.size(size).clip(CircleShape).background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        val ring = Modifier.size(size * 0.78f)
        val stroke = size * 0.14f
        if (progress > 0f) {
            CircularProgressIndicator(progress = { progress }, modifier = ring, color = KidGrape,
                strokeWidth = stroke, trackColor = KidGrape.copy(alpha = 0.18f))
        } else {
            CircularProgressIndicator(modifier = ring, color = KidGrape, strokeWidth = stroke)
        }
    }
}

/** On an open item whose voices can't download without a grown-up: our cloud, the internet. */
@Composable
fun NeedsGrownUpBadge(modifier: Modifier = Modifier, size: Dp = 30.dp) {
    Box(
        modifier.size(size).clip(CircleShape).background(KidSun),
        contentAlignment = Alignment.Center,
    ) {
        SvgArt(KidAssets.weather("cloud"), modifier = Modifier.size(size * 0.7f), contentDescription = "Needs a grown-up")
    }
}

/**
 * Opens when a child taps something that is part of Pro. Speaks for itself (the caller says the
 * line aloud as it opens) and offers the child's way in, when there is one, plus the grown-up's.
 *
 * @param onWatchAd plays a short video that opens the item for today; null hides the button
 *   (Pro owners never get here; no video loaded, offline, or ads not offered).
 * @param onAskGrownUp goes on to the parent gate and then the purchase screen.
 * @param offline the internet can't be reached: says why there is no video, rather than leave
 *   the child (or grown-up) wondering where the button went.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockedSheet(
    onWatchAd: (() -> Unit)?,
    onAskGrownUp: () -> Unit,
    onDismiss: () -> Unit,
    offline: Boolean = false,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp),
        ) {
            Box {
                CurvyMascot(size = 110.dp, mood = CurvyMood.Wow, floating = true)
                LockBadge(Modifier.align(Alignment.BottomEnd), size = 40.dp)
            }
            Spacer(Modifier.height(12.dp))
            Text("This one is for Pro!", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp, color = KidGrape, textAlign = TextAlign.Center)
            Spacer(Modifier.height(20.dp))
            if (onWatchAd == null && offline) {
                Text("Videos need the internet. Ask a grown-up to connect.", fontWeight = FontWeight.Bold,
                    fontSize = 15.sp, color = KidInk.copy(alpha = 0.65f), textAlign = TextAlign.Center)
                Spacer(Modifier.height(14.dp))
            }
            if (onWatchAd != null) {
                CandyButton(onClick = onWatchAd, containerColor = KidGrape,
                    modifier = Modifier.fillMaxWidth().height(76.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("▶", fontSize = 26.sp)
                        Spacer(Modifier.width(12.dp))
                        Text("Watch a video", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                        Spacer(Modifier.width(10.dp))
                        // Families policy: a child must be able to tell this is an ad.
                        Text("Ad", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = KidGrape,
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(Color.White)
                                .padding(horizontal = 6.dp, vertical = 1.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
            CandyButton(onClick = onAskGrownUp, containerColor = Color(0xFFEDE6FF), contentColor = KidInk,
                modifier = Modifier.fillMaxWidth().height(64.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Lock, contentDescription = null, tint = KidGrape, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Ask a grown-up", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
            }
        }
    }
}
