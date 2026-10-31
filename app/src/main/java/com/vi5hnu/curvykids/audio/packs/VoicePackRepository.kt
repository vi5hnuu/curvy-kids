package com.vi5hnu.curvykids.audio.packs

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Knows which [VoicePack]s are on the device and fetches the rest. Screens depend on this
 * interface only, so the Play-backed implementation can be swapped (e.g. in tests).
 */
interface VoicePackRepository {
    /** Latest state of every pack; updates as downloads progress. */
    val states: StateFlow<Map<VoicePack, PackState>>

    /** The pack's downloaded `assets` folder, or null when it isn't downloaded (or is built in). */
    fun assetsDir(pack: VoicePack): File?

    /** Starts downloading whichever of [packs] aren't on the device yet. */
    fun download(packs: Collection<VoicePack>)

    /** Asks Play again for the packs' states, e.g. after a failure or coming back online. */
    fun refresh()

    /** Shows Play's own "download on mobile data?" dialog when a pack [PackState.NeedsConfirmation]. */
    fun confirm(launcher: ActivityResultLauncher<IntentSenderRequest>)
}
