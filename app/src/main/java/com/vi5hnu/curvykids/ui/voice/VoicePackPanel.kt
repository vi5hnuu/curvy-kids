package com.vi5hnu.curvykids.ui.voice

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.packs.FailReason
import com.vi5hnu.curvykids.audio.packs.PackState
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.TintTeal

/**
 * For grown-ups: the state of each of [packs] and one button that does the next sensible thing
 * (download, allow the download, or try again). Used by the download sheet a child is sent to
 * and by the Parent Zone, so both behave the same.
 */
@Composable
fun VoicePackPanel(
    packs: Collection<VoicePack>,
    states: Map<VoicePack, PackState>,
    repository: VoicePackRepository,
    modifier: Modifier = Modifier,
) {
    // Play's own "download over mobile data?" dialog; ask again for states once it closes.
    val confirmLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { repository.refresh() }

    val packStates = packs.map { it to (states[it] ?: PackState.Checking) }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        packStates.forEach { (pack, state) -> PackRow(pack, state) }

        val action = nextAction(packStates.map { it.second })
        if (action != null) {
            Spacer(Modifier.height(2.dp))
            CandyButton(
                onClick = {
                    when (action) {
                        PanelAction.CONFIRM -> repository.confirm(confirmLauncher)
                        PanelAction.CHECK -> repository.refresh()
                        else -> repository.download(packs)
                    }
                },
                enabled = action != PanelAction.WAIT,
                containerColor = Green,
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) {
                Text(action.label(downloadSize(packStates.map { it.second })),
                    fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun PackRow(pack: VoicePack, state: PackState) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(pack.title, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp, color = Ink, modifier = Modifier.weight(1f))
            Text(statusText(state), fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = when (state) {
                    PackState.Ready -> Green
                    is PackState.Failed -> Coral
                    else -> InkSoft
                })
        }
        if (state is PackState.Downloading) {
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { state.progress },
                color = Teal,
                trackColor = TintTeal,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            )
        }
        if (state is PackState.Failed) {
            Spacer(Modifier.height(4.dp))
            Text(failText(state.reason), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Coral)
        }
    }
}

/** What the panel's button does, given every pack's state. Null when there's nothing to do. */
internal enum class PanelAction { DOWNLOAD, RETRY, CONFIRM, WAIT, CHECK }

internal fun nextAction(states: List<PackState>): PanelAction? = when {
    states.all { it == PackState.Ready } -> null
    states.any { it == PackState.NeedsConfirmation } -> PanelAction.CONFIRM
    states.any { it is PackState.Downloading } -> PanelAction.WAIT
    states.any { it is PackState.Failed } -> PanelAction.RETRY
    states.any { it is PackState.NotDownloaded } -> PanelAction.DOWNLOAD
    else -> PanelAction.CHECK
}

private fun PanelAction.label(sizeBytes: Long): String = when (this) {
    PanelAction.DOWNLOAD -> if (sizeBytes > 0) "Download (${megabytes(sizeBytes)})" else "Download"
    PanelAction.RETRY -> "Try again"
    PanelAction.CONFIRM -> "Allow download"
    PanelAction.WAIT -> "Downloading…"
    PanelAction.CHECK -> "Check again"
}

private fun downloadSize(states: List<PackState>): Long =
    states.sumOf { (it as? PackState.NotDownloaded)?.sizeBytes ?: 0L }

private fun statusText(state: PackState): String = when (state) {
    PackState.Checking -> "Checking…"
    PackState.Ready -> "Ready"
    is PackState.NotDownloaded -> if (state.sizeBytes > 0) megabytes(state.sizeBytes) else "Not downloaded"
    is PackState.Downloading -> "${(state.progress * 100).toInt()}%"
    PackState.NeedsConfirmation -> "Waiting"
    is PackState.Failed -> "Didn't download"
}

private fun failText(reason: FailReason): String = when (reason) {
    FailReason.OFFLINE -> "No internet. Connect to Wi-Fi or mobile data, then try again."
    FailReason.NO_STORAGE -> "Not enough space on this device. Free some space, then try again."
    FailReason.NOT_FROM_PLAY -> "Voices download through Google Play. Install the app from Google Play to get them."
    FailReason.OTHER -> "Something went wrong. Please try again."
}

private fun megabytes(bytes: Long): String = "%.1f MB".format(bytes / 1_048_576.0)
