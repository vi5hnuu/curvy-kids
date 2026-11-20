package com.vi5hnu.curvykids.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The Home banner's question: is there a Pro offer worth telling a grown-up about? Built from
 * [ProRepository.state] by [bannerOffer], minus the offer whose banner a grown-up closed (kept in
 * [ProStore], so it stays closed across restarts until a different offer comes).
 *
 * Also re-checks once a minute, so the banner leaves at the offer's end time even before Play is
 * asked again.
 */
class OfferNotice(pro: ProRepository, private val store: ProStore, private val scope: CoroutineScope) {

    private val ticks = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(TICK_MS)
        }
    }

    /** The offer to show on Home, or null for no banner. */
    val offer: StateFlow<ProOffer?> = combine(pro.state, store.hiddenOffer, ticks) { state, hidden, now ->
        bannerOffer(state, hidden, now)
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), null)

    /** Stops showing [offer]'s banner; a new or extended offer shows again. */
    fun hide(offer: ProOffer) {
        scope.launch { store.hideOffer(offer.noticeKey) }
    }

    private companion object {
        const val TICK_MS = 60_000L
    }
}
