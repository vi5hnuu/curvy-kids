package com.vi5hnu.curvykids.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.packs.PackState
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import com.vi5hnu.curvykids.audio.packs.VoiceReadiness
import com.vi5hnu.curvykids.billing.Lockable
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/** An item tapped before its voices were here, to open once they are. */
private class WaitingItem(val item: Lockable, val route: String?, val open: () -> Unit)

/**
 * What a tap on an open item whose voices aren't downloaded yet does (for
 * [com.vi5hnu.curvykids.billing.ContentLocks.onNeedsVoices]). A child can't follow a loading
 * screen, so nothing blocks them:
 * - the download starts in the background, and the item's tile shows a ring filling up;
 * - the voice says "Getting your voices ready!", or, when it can't go ahead (offline, Play asks a
 *   grown-up, it failed), "Let's get the voices! Ask a grown-up to help.";
 * - the child can play anything else meanwhile. If they are still on the screen where they
 *   tapped when the voices arrive, the item opens by itself; moving to another screen (or
 *   tapping another item that isn't ready) forgets it, so nothing ever jumps in on other play.
 *
 * Only reachable for an item the child may open (Pro owned, free, or opened for today by a
 * video), so Pro recordings are never fetched for anyone else.
 */
@Composable
fun rememberVoicesOnTap(
    voicePacks: VoicePackRepository,
    speaker: PhonicsSpeaker,
    online: Boolean,
    route: String?,
): (Lockable, () -> Unit) -> Unit {
    val isOnline by rememberUpdatedState(online)
    val currentRoute by rememberUpdatedState(route)
    var waiting by remember { mutableStateOf<WaitingItem?>(null) }

    // Leaving the screen it was tapped on forgets it.
    LaunchedEffect(route) { if (waiting?.route != route) waiting = null }

    // Opens it once its voices are in, if the child is still there.
    LaunchedEffect(waiting) {
        val w = waiting ?: return@LaunchedEffect
        voicePacks.states.collect { all ->
            if (VoiceReadiness(all).isReady(w.item)) {
                waiting = null
                if (currentRoute == w.route) w.open()
            }
        }
    }

    return remember(voicePacks, speaker) {
        { item, open ->
            val state = VoiceReadiness(voicePacks.states.value).stateOf(item)
            val needsGrownUp = !isOnline || state is PackState.Failed || state == PackState.NeedsConfirmation
            speaker.say(EnglishVoiceScript.say(if (needsGrownUp) EnglishVoiceScript.GET_VOICES else EnglishVoiceScript.GETTING_VOICES))
            // Asked again even after a failure: coming back online, a retry may just work.
            voicePacks.download(VoicePack.requiredFor(item))
            waiting = WaitingItem(item, currentRoute, open)
        }
    }
}
