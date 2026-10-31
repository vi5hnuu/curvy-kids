package com.vi5hnu.curvykids.ui.voice

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
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
 * The child is asked to fetch a grown-up (said aloud as well, for children who can't read yet).
 * The grown-up passes the [ParentGate] and gets the download sheet. Once every pack is ready the
 * caller swaps this screen for the activity itself, so there's no extra step afterwards.
 *
 * @param speak Says a line aloud, in English.
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

    LaunchedEffect(Unit) {
        repository.refresh()
        speak("Let's get the voices! Ask a grown-up to help.")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(18.dp),
    ) {
        ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
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
