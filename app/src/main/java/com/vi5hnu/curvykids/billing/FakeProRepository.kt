package com.vi5hnu.curvykids.billing

import android.app.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * A stand-in store for debug builds. Real Play billing answers only an app installed from a Play
 * test track, so on an emulator nothing could be seen without this. It offers Pro at ₹299 with a
 * ₹199 launch offer ending in three days, and "buying" unlocks at once. Remembered like a real
 * purchase, so the lock screens and the unlocked app can both be checked across restarts.
 *
 * Never used in release builds (BuildConfig.FAKE_BILLING is false there).
 */
class FakeProRepository(private val scope: CoroutineScope, private val store: ProStore) : ProRepository {

    private val offer = ProOffer(
        token = "fake-offer",
        offerId = "launch",
        price = "₹199",
        priceMicros = 199_000_000,
        fullPrice = "₹299",
        percentOff = 33,
        endsAtMillis = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3),
    )

    private val _state = MutableStateFlow(ProState(store = StoreState.Ready(offer)))
    override val state: StateFlow<ProState> = _state.asStateFlow()

    override fun refresh() {
        scope.launch {
            val owned = store.owned.first()
            _state.update { it.copy(owned = owned, known = true) }
        }
    }

    override fun buy(activity: Activity, offer: ProOffer) {
        scope.launch {
            store.setOwned(true)
            _state.update { it.copy(owned = true, pending = false) }
        }
    }

    override fun clearFailure() = _state.update { it.copy(failure = null) }
}
