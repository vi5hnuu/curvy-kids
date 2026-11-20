package com.vi5hnu.curvykids.ui.voice

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.packs.PackState
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.parent.ParentGate
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * Shown instead of a topic whose recorded voices aren't on the device yet.
 *
 * Play starts the voice packs by itself after install (fast-follow) and the app asks again on
 * every start, so usually they are already here. If the child is quicker, this screen starts or
 * resumes the download and shows a friendly wait (the mascot and a filling bar) with no grown-up
 * needed. Only when someone has to act (Play wants the download confirmed, or it failed: no
 * internet, no space) is the child asked to fetch a grown-up, who passes the [ParentGate] and gets
 * the download sheet. Once every pack is ready the caller swaps this screen for the activity.
 *
 * @param speak Says an English line aloud (a line of EnglishVoiceScript).
 */
@Composable
fun VoicePackGate(
    topic: Topic,
    packs: Set<VoicePack>,
    repository: VoicePackRepository,
    onBack: () -> Unit,
    speak: (String) -> Unit,
) {
    var showGate by rememberSaveable { mutableStateOf(false) }
    var showDownload by rememberSaveable { mutableStateOf(false) }
    val states by repository.states.collectAsState()
    val needsGrownUp = packs.any { states[it] is PackState.NeedsConfirmation || states[it] is PackState.Failed }

    // Starts the packs that aren't here, or resumes them; packs already on the way carry on.
    LaunchedEffect(Unit) { repository.download(packs) }
    LaunchedEffect(needsGrownUp) {
        speak(if (needsGrownUp) EnglishVoiceScript.GET_VOICES else EnglishVoiceScript.GETTING_VOICES)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(18.dp),
    ) {
        ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (!needsGrownUp) {
                VoicesLoading(topic.color, downloadProgress(packs, states))
                return@Box
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CurvyMascot(size = 150.dp, mood = CurvyMood.Wow, floating = true)
                Spacer(Modifier.height(16.dp))
                Text("Let's get the voices!", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp, color = topic.color, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text("Ask a grown-up to help.", fontFamily = FontDisplay, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = InkSoft, textAlign = TextAlign.Center)
                Spacer(Modifier.height(28.dp))
                CandyButton(
                    onClick = { showGate = true },
                    containerColor = topic.color,
                    modifier = Modifier.height(72.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 28.dp)) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Grown-up", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    }
                }
            }
        }
    }

    if (showGate) {
        ParentGate(
            onPass = { showGate = false; showDownload = true },
            onDismiss = { showGate = false },
        )
    }
    if (showDownload) {
        VoiceDownloadSheet(packs, repository, onDismiss = { showDownload = false })
    }
}

/**
 * How far along [packs] are together, 0 to 1: a pack on the device counts as whole, one being
 * fetched by its share so far. Null while nothing is known yet (Play hasn't said), so the bar
 * can keep moving instead of sitting empty.
 */
internal fun downloadProgress(packs: Set<VoicePack>, states: Map<VoicePack, PackState>): Float? {
    if (packs.isEmpty()) return null
    val shares = packs.map {
        when (val state = states[it]) {
            PackState.Ready -> 1f
            is PackState.Downloading -> if (state.totalBytes > 0) state.progress else return null
            else -> return null
        }
    }
    return shares.average().toFloat()
}

/** The child's wait: the mascot floating, "Getting your voices ready!" and a bar that fills. */
@Composable
private fun VoicesLoading(color: Color, progress: Float?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 24.dp)) {
        CurvyMascot(size = 150.dp, mood = CurvyMood.Happy, floating = true)
        Spacer(Modifier.height(16.dp))
        Text(EnglishVoiceScript.GETTING_VOICES, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp, color = color, textAlign = TextAlign.Center)
        Spacer(Modifier.height(22.dp))
        LoadingBar(color, progress, Modifier.widthIn(max = 320.dp).fillMaxWidth().height(22.dp))
    }
}

/**
 * A chunky rounded bar. With a [progress] it fills to it smoothly; without one a short block
 * slides across and back, so the child can see something is happening.
 */
@Composable
private fun LoadingBar(color: Color, progress: Float?, modifier: Modifier) {
    val filled by animateFloatAsState(progress ?: 0f, tween(600), label = "voiceProgress")
    val slide by rememberInfiniteTransition(label = "voiceWait").animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "voiceSlide",
    )
    val shape = RoundedCornerShape(50)
    BoxWithConstraints(modifier.clip(shape).background(color.copy(alpha = 0.18f))) {
        val block = if (progress != null) maxWidth * filled.coerceIn(0.06f, 1f) else maxWidth * 0.3f
        val offset = if (progress != null) 0.dp else (maxWidth - block) * slide
        Box(Modifier.offset(x = offset).width(block).fillMaxHeight().clip(shape).background(color))
    }
}

/** The grown-up's side: what's needed, why, and the download button. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceDownloadSheet(
    packs: Set<VoicePack>,
    repository: VoicePackRepository,
    onDismiss: () -> Unit,
) {
    val states by repository.states.collectAsState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        containerColor = Color.White,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 18.dp).padding(bottom = 32.dp),
        ) {
            Text("Download voices", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp, color = Ink)
            Text(
                "This activity uses recorded Hindi voices. They download once from Google Play, " +
                    "then work without internet. It's a small download, fine on mobile data.",
                fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkSoft,
            )
            Spacer(Modifier.height(10.dp))
            VoicePackPanel(packs, states, repository)
        }
    }
}
