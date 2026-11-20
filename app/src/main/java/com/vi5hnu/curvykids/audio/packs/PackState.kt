package com.vi5hnu.curvykids.audio.packs

import com.google.android.play.core.assetpacks.model.AssetPackErrorCode
import com.google.android.play.core.assetpacks.model.AssetPackStatus

/** Where one [VoicePack] is, as far as the screens need to know. */
sealed interface PackState {
    /** Not known yet (asking Play). */
    data object Checking : PackState

    /** On the device (installed by Play, or built into a debug app). */
    data object Ready : PackState

    data class NotDownloaded(val sizeBytes: Long) : PackState

    data class Downloading(val downloadedBytes: Long, val totalBytes: Long) : PackState {
        val progress: Float get() = if (totalBytes > 0) downloadedBytes.toFloat() / totalBytes else 0f
    }

    /** Play is waiting for Wi-Fi or for the parent to OK the download. */
    data object NeedsConfirmation : PackState

    data class Failed(val reason: FailReason) : PackState

    companion object {
        /** Maps Play's status codes to a [PackState]. Pure, so it can be unit tested. */
        fun from(status: Int, downloadedBytes: Long, totalBytes: Long, errorCode: Int): PackState = when (status) {
            AssetPackStatus.COMPLETED -> Ready
            AssetPackStatus.PENDING, AssetPackStatus.DOWNLOADING, AssetPackStatus.TRANSFERRING ->
                Downloading(downloadedBytes, totalBytes)
            AssetPackStatus.WAITING_FOR_WIFI, AssetPackStatus.REQUIRES_USER_CONFIRMATION -> NeedsConfirmation
            AssetPackStatus.FAILED -> Failed(FailReason.of(errorCode))
            AssetPackStatus.NOT_INSTALLED, AssetPackStatus.CANCELED -> NotDownloaded(totalBytes)
            else -> Failed(FailReason.OTHER)
        }
    }
}

/** Why a download didn't work, in terms a parent can act on. */
enum class FailReason {
    OFFLINE, NO_STORAGE, NOT_FROM_PLAY, OTHER;

    companion object {
        fun of(errorCode: Int): FailReason = when (errorCode) {
            AssetPackErrorCode.NETWORK_ERROR -> OFFLINE
            AssetPackErrorCode.INSUFFICIENT_STORAGE -> NO_STORAGE
            AssetPackErrorCode.APP_UNAVAILABLE, AssetPackErrorCode.APP_NOT_OWNED,
            AssetPackErrorCode.UNRECOGNIZED_INSTALLATION, AssetPackErrorCode.API_NOT_AVAILABLE,
            AssetPackErrorCode.PACK_UNAVAILABLE -> NOT_FROM_PLAY
            else -> OTHER
        }
    }
}

/** True when every one of [packs] is on the device. */
fun Map<VoicePack, PackState>.allReady(packs: Collection<VoicePack>): Boolean =
    packs.all { this[it] == PackState.Ready }
