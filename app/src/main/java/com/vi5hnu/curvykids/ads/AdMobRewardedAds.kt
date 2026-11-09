package com.vi5hnu.curvykids.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * [RewardedAds] through Google AdMob, set up for an app made for children:
 * - every request is tagged child-directed and under the age of consent, so no personalised ads,
 *   no remarketing and no advertising id (Play Families policy; the AD_ID permission is also
 *   removed in the manifest);
 * - the highest content rating allowed is G (general audiences).
 *
 * One video is kept loaded so the button plays instantly; a failed load is retried with a growing
 * pause (30 s up to 5 min) so an offline device doesn't keep asking. Nothing loads after [stop].
 *
 * @param unitId the rewarded ad unit (Google's test unit in debug builds).
 */
class AdMobRewardedAds(
    context: Context,
    private val scope: CoroutineScope,
    private val unitId: String,
) : RewardedAds {

    private val app = context.applicationContext

    private val _ready = MutableStateFlow(false)
    override val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private var active = false
    private var initialized = false
    private var loading = false
    private var ad: RewardedAd? = null
    private var retryDelayMs = FIRST_RETRY_MS
    private var retryJob: Job? = null

    override fun start() {
        if (active) return
        active = true
        scope.launch {
            if (!initialized) {
                MobileAds.setRequestConfiguration(
                    RequestConfiguration.Builder()
                        .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                        .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
                        .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                        .build()
                )
                // Initialising reads from disk; keep it off the main thread.
                withContext(Dispatchers.IO) { MobileAds.initialize(app) {} }
                initialized = true
            }
            load()
        }
    }

    override fun stop() {
        active = false
        retryJob?.cancel()
        ad = null
        _ready.value = false
    }

    override fun show(activity: Activity, onReward: () -> Unit) {
        val video = ad ?: return
        ad = null
        _ready.value = false
        video.fullScreenContentCallback = object : FullScreenContentCallback() {
            // Have the next one ready for the next locked tap.
            override fun onAdDismissedFullScreenContent() = load()

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.w(TAG, "Rewarded ad failed to show: ${error.message}")
                load()
            }
        }
        video.show(activity) { onReward() }
    }

    /** Loads one video, if active and none is loaded or on its way. Main thread. */
    private fun load() {
        if (!active || loading || ad != null) return
        loading = true
        RewardedAd.load(app, unitId, AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(loaded: RewardedAd) {
                loading = false
                if (!active) return
                ad = loaded
                _ready.value = true
                retryDelayMs = FIRST_RETRY_MS
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                loading = false
                Log.i(TAG, "No rewarded ad (${error.code}: ${error.message}); retry in ${retryDelayMs / 1000}s")
                retryJob = scope.launch {
                    delay(retryDelayMs)
                    retryDelayMs = (retryDelayMs * 2).coerceAtMost(MAX_RETRY_MS)
                    load()
                }
            }
        })
    }

    private companion object {
        const val TAG = "CURVY_ADS"
        const val FIRST_RETRY_MS = 30_000L
        const val MAX_RETRY_MS = 5 * 60_000L
    }
}
