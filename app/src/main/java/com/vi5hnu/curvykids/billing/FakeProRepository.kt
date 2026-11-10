package com.vi5hnu.curvykids.billing

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * A stand-in store for debug builds. Real Play billing answers only an app installed from a Play
 * test track, so on an emulator nothing could be seen without this. It offers Pro at ₹299 with a
 * ₹199 launch offer, and "buying" unlocks at once. Remembered like a real purchase, so the lock
 * screens and the unlocked app can both be checked across restarts.
 *
 * When the launch offer ends, the full price takes its place, as Play's answer would.
 *
 * Never used in release builds (BuildConfig.FAKE_BILLING is false there).
 *
 * @param offerSeconds the launch offer's length (BuildConfig.FAKE_OFFER_SECONDS); it ends at the next
 *   whole multiple of it, so at most that long from now.
 */
class FakeProRepository(
    private val scope: CoroutineScope,
    private val store: ProStore,
    offerSeconds: Long,
) : ProRepository {

    private val launchOffer = ProOffer(
        token = "fake-offer",
        offerId = "launch",
        price = "₹199",
        priceMicros = 199_000_000,
        fullPrice = "₹299",
        percentOff = 33,
        // The next whole period from the epoch, not "now + period": the same end time across
        // restarts, as a real Play offer has, so a closed Home banner stays closed.
        endsAtMillis = (offerSeconds * 1000).let { period -> (System.currentTimeMillis() / period + 1) * period },
    )

    private val fullPrice = ProOffer(
        token = "fake-full",
        offerId = null,
        price = "₹299",
        priceMicros = 299_000_000,
        fullPrice = null,
        percentOff = null,
        endsAtMillis = null,
    )

    private val _state = MutableStateFlow(ProState(store = StoreState.Ready(currentOffer())))
    override val state: StateFlow<ProState> = _state.asStateFlow()

    private var offerExpiry: Job? = null

    private fun currentOffer(): ProOffer =
        if (System.currentTimeMillis() < launchOffer.endsAtMillis!!) launchOffer else fullPrice

    override fun refresh() {
        scope.launch {
            val owned = store.owned.first()
            _state.update {
                it.copy(
                    owned = owned,
                    known = true,
                    store = StoreState.Ready(currentOffer()),
                    unlockedJustNow = it.unlockedJustNow || (owned && !it.owned && it.known),
                )
            }
        }
        offerExpiry?.cancel()
        val wait = launchOffer.endsAtMillis!! - System.currentTimeMillis()
        if (wait > 0) offerExpiry = scope.launch {
            delay(wait)
            _state.update { it.copy(store = StoreState.Ready(currentOffer())) }
        }
    }

    override fun buy(activity: Activity, offer: ProOffer) {
        scope.launch {
            if (offer.token != currentOffer().token) {
                // Bought an offer that has ended: Play would answer ITEM_UNAVAILABLE.
                _state.update { it.copy(failure = StoreProblem.OFFER_ENDED, store = StoreState.Ready(currentOffer())) }
                return@launch
            }
            store.setOwned(true)
            _state.update { it.copy(owned = true, pending = false, unlockedJustNow = !it.owned) }
        }
    }

    override fun clearFailure() = _state.update { it.copy(failure = null) }

    override fun clearUnlocked() = _state.update { it.copy(unlockedJustNow = false) }
}
