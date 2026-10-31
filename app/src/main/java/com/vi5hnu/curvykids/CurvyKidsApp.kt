package com.vi5hnu.curvykids

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.vi5hnu.curvykids.audio.BundledClipSource
import com.vi5hnu.curvykids.audio.ClipSource
import com.vi5hnu.curvykids.audio.PackClipSource
import com.vi5hnu.curvykids.audio.packs.PlayVoicePackRepository
import com.vi5hnu.curvykids.audio.packs.VoicePackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Application entry point. Provides the singleton Coil [ImageLoader] used by every [coil.compose.AsyncImage]
 * in the app, wired with the SVG decoder so the kids-asset `.svg` badges (in `assets/kids/…`) render.
 *
 * Registering the loader here (instead of building one per call site) keeps a shared memory/disk
 * cache, so a badge that appears on many screens is decoded once.
 *
 * Also owns the app-wide voice pack state ([voicePacks]), shared by the speaker that plays the
 * clips and the screens that ask a grown-up to download them.
 */
class CurvyKidsApp : Application(), ImageLoaderFactory {

    /** Lives as long as the process; for work that outlasts any one screen. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val voicePacks: VoicePackRepository by lazy { PlayVoicePackRepository(this, appScope) }

    /** Where recordings are looked for: built into the app first, then downloaded packs. */
    val clipSources: List<ClipSource> by lazy { listOf(BundledClipSource(assets), PackClipSource(voicePacks)) }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .build()
}
