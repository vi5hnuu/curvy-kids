package com.vi5hnu.curvykids.audio.packs

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.assetpacks.AssetPackException
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.ktx.requestFetch
import com.google.android.play.core.ktx.requestPackStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * [VoicePackRepository] backed by Play Asset Delivery.
 *
 * A pack whose folders are already inside the app's own assets (debug builds, see
 * app/build.gradle.kts) counts as [PackState.Ready] and is never fetched.
 * Lives as long as the app; [scope] should be application-wide.
 */
class PlayVoicePackRepository(
    context: Context,
    private val scope: CoroutineScope,
) : VoicePackRepository {

    private val manager = AssetPackManagerFactory.getInstance(context.applicationContext)
    private val assets = context.applicationContext.assets

    private val builtIn: Set<VoicePack> = VoicePack.entries.filter { pack ->
        pack.folders.all { folder -> runCatching { assets.list("voice/$folder") }.getOrNull()?.isNotEmpty() == true }
    }.toSet()

    private val remote: List<VoicePack> = VoicePack.entries - builtIn

    private val _states = MutableStateFlow(
        VoicePack.entries.associateWith { if (it in builtIn) PackState.Ready else PackState.Checking }
    )
    override val states: StateFlow<Map<VoicePack, PackState>> = _states.asStateFlow()

    private val listener = AssetPackStateUpdateListener { state ->
        VoicePack.named(state.name())?.let { set(it, state.toPackState()) }
    }

    init {
        if (remote.isNotEmpty()) {
            manager.registerListener(listener)
            refresh()
        }
    }

    override fun assetsDir(pack: VoicePack): File? =
        manager.getPackLocation(pack.packName)?.assetsPath()?.let(::File)

    override fun refresh() {
        if (remote.isEmpty()) return
        scope.launch {
            try {
                manager.requestPackStates(remote.map { it.packName }).packStates().forEach { (name, state) ->
                    VoicePack.named(name)?.let { set(it, state.toPackState()) }
                }
            } catch (e: Exception) {
                fail(remote, e)
            }
        }
    }

    override fun download(packs: Collection<VoicePack>) {
        val needed = packs.filter { it in remote && _states.value[it] != PackState.Ready }
        if (needed.isEmpty()) return
        needed.forEach { set(it, PackState.Downloading(0, 0)) }
        scope.launch {
            // Progress after this arrives through [listener].
            try {
                manager.requestFetch(needed.map { it.packName })
            } catch (e: Exception) {
                fail(needed, e)
            }
        }
    }

    override fun confirm(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        manager.showConfirmationDialog(launcher)
    }

    private fun set(pack: VoicePack, state: PackState) = _states.update { it + (pack to state) }

    private fun fail(packs: Collection<VoicePack>, e: Exception) {
        Log.w(TAG, "Voice pack request failed for $packs", e)
        val reason = (e as? AssetPackException)?.let { FailReason.of(it.errorCode) } ?: FailReason.OTHER
        packs.forEach { set(it, PackState.Failed(reason)) }
    }

    private fun AssetPackState.toPackState() =
        PackState.from(status(), bytesDownloaded(), totalBytesToDownload(), errorCode())

    private companion object {
        const val TAG = "VOICE_PACKS"
    }
}
