package com.vi5hnu.curvykids.audio.packs

import com.vi5hnu.curvykids.billing.Lockable

/**
 * Whether the recordings an item needs ([VoicePack.requiredFor]) are on the device, as one
 * [PackState] for the item, from a snapshot of every pack's state.
 *
 * Kept apart from the locks: whether a child *may* open something is the locks' business, whether
 * its voices are here is this one's; [com.vi5hnu.curvykids.billing.ContentLocks] only combines them.
 */
class VoiceReadiness(private val states: Map<VoicePack, PackState>) {

    /**
     * The item's state, from its packs: Ready when all are; otherwise the one most worth telling
     * (a failure, then a grown-up needed, then a download's combined progress, then not started).
     */
    fun stateOf(item: Lockable): PackState {
        val packs = VoicePack.requiredFor(item).map { states[it] ?: PackState.Checking }
        if (packs.all { it == PackState.Ready }) return PackState.Ready
        packs.firstOrNull { it is PackState.Failed }?.let { return it }
        if (PackState.NeedsConfirmation in packs) return PackState.NeedsConfirmation
        val downloading = packs.filterIsInstance<PackState.Downloading>()
        if (downloading.isNotEmpty()) {
            return PackState.Downloading(downloading.sumOf { it.downloadedBytes }, downloading.sumOf { it.totalBytes })
        }
        if (PackState.Checking in packs) return PackState.Checking
        return PackState.NotDownloaded(packs.filterIsInstance<PackState.NotDownloaded>().sumOf { it.sizeBytes })
    }

    fun isReady(item: Lockable): Boolean = stateOf(item) == PackState.Ready

    companion object {
        /** Every pack here (previews, tests, a build with every pack inside). */
        val ALL_READY = VoiceReadiness(VoicePack.entries.associateWith { PackState.Ready })
    }
}
