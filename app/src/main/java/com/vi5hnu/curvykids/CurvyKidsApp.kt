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
import com.vi5hnu.curvykids.audio.packs.VoicePack
import com.vi5hnu.curvykids.ads.AdMobRewardedAds
import com.vi5hnu.curvykids.ads.AdUnlocks
import com.vi5hnu.curvykids.ads.RewardedAds
import com.vi5hnu.curvykids.billing.Access
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import com.vi5hnu.curvykids.billing.FakeProRepository
import com.vi5hnu.curvykids.billing.PlayProRepository
import com.vi5hnu.curvykids.billing.ProRepository
import com.vi5hnu.curvykids.billing.ProStore
import com.vi5hnu.curvykids.billing.PurchaseVerifier

/**
 * Application entry point. Provides the singleton Coil [ImageLoader] used by every [coil.compose.AsyncImage]
 * in the app, wired with the SVG decoder so the kids-asset `.svg` badges (in `assets/kids/…`) render.
 *
 * Registering the loader here (instead of building one per call site) keeps a shared memory/disk
 * cache, so a badge that appears on many screens is decoded once.
 *
 * Also owns the app-wide voice pack state ([voicePacks]), shared by the speaker that plays the
 * clips and the screens that wait for them to download, CurvyKids Pro ([pro]) and the rewarded
 * videos free users may choose to watch ([ads]).
 */
class CurvyKidsApp : Application(), ImageLoaderFactory {

    /** Lives as long as the process; for work that outlasts any one screen. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val voicePacks: VoicePackRepository by lazy { PlayVoicePackRepository(this, appScope) }

    /**
     * CurvyKids Pro: sold through Play, or through a stand-in store in debug builds (see
     * [FakeProRepository]) since Play billing only answers apps installed from a test track.
     */
    private val proStore by lazy { ProStore(this) }

    val pro: ProRepository by lazy {
        val store = proStore
        if (BuildConfig.FAKE_BILLING) FakeProRepository(appScope, store)
        else PlayProRepository(
            context = this,
            scope = appScope,
            store = store,
            verifier = PurchaseVerifier(BuildConfig.PLAY_LICENSE_KEY, allowWithoutKey = BuildConfig.DEBUG),
        )
    }

    /** What a watched video opened for today. */
    val adUnlocks: AdUnlocks by lazy { AdUnlocks(this) }

    /** Rewarded videos (Google's test unit in debug builds). Running only for free users. */
    val ads: RewardedAds by lazy { AdMobRewardedAds(this, appScope, BuildConfig.REWARDED_AD_UNIT) }

    /** What is locked right now (Pro, the free/Pro split, videos watched today); what screens ask. */
    val access: Access by lazy { Access(pro, appScope, adUnlocks.openedToday) }

    /** Where recordings are looked for: built into the app first, then downloaded packs. */
    val clipSources: List<ClipSource> by lazy { listOf(BundledClipSource(assets), PackClipSource(voicePacks)) }

    /**
     * Makes sure the voice packs are coming. Play starts them after install by itself, but that
     * download can still be running, have failed, or never have happened (a sideloaded app), so
     * the app asks again each start. Packs already here or on their way are left alone.
     */
    override fun onCreate() {
        super.onCreate()
        voicePacks.download(VoicePack.entries)
        // Connects to Play: loads the offers and restores (or revokes) Pro before a screen asks.
        pro.refresh()
        keepAdsForFreeUsers()
    }

    /**
     * Loads videos only while Pro isn't owned, and stops for good once it is. Both the live state
     * and the saved one must say "not owned", so a Pro user starting offline (Play not answering
     * yet) never has the ads SDK started.
     */
    private fun keepAdsForFreeUsers() {
        appScope.launch {
            combine(pro.state, proStore.owned) { state, saved -> state.owned || saved }
                .distinctUntilChanged()
                .collect { owned -> if (owned) ads.stop() else ads.start() }
        }
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .build()
}
