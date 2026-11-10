package com.vi5hnu.curvykids.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Currency
import java.util.concurrent.TimeUnit

/**
 * [ProRepository] backed by Google Play Billing (library 9, one-time product with offers).
 *
 * - Offers: Play returns the offers this user is eligible for; the cheapest is shown and bought
 *   with its own token, so a discount made in the Play Console needs no app update.
 * - Granting: only a PURCHASED purchase of [ProCatalog.PRODUCT_ID] whose signature checks out
 *   ([PurchaseVerifier]) unlocks Pro. A PENDING one (cash, slow card) waits until it clears.
 * - Acknowledging: every granted purchase is acknowledged; Play refunds one left unacknowledged
 *   for three days. One that failed to acknowledge is retried on the next [refresh].
 * - Refunds: whenever Play can be asked and reports no purchase, Pro is taken back; offline, the
 *   saved state in [ProStore] stands.
 * - Offer ends: when the shown offer has an end date, Play is asked again just after it, so the
 *   screen moves to the next offer or the full price instead of selling an offer that is gone.
 * - Promo codes: a code entered in Play's purchase sheet arrives as a normal purchase; one
 *   redeemed in the Play Store app is found by the [refresh] made when the app comes back.
 *
 * Lives as long as the app; [scope] should be application-wide.
 */
class PlayProRepository(
    context: Context,
    private val scope: CoroutineScope,
    private val store: ProStore,
    private val verifier: PurchaseVerifier,
) : ProRepository {

    private val _state = MutableStateFlow(ProState())
    override val state: StateFlow<ProState> = _state.asStateFlow()

    /** The product as Play last described it; needed to start a purchase. */
    private var product: ProductDetails? = null

    /** Asks Play again when the shown offer ends. */
    private var offerExpiry: Job? = null

    /** A connection attempt is under way; a second [refresh] meanwhile waits for it. */
    @Volatile private var connecting = false

    private val client: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener { result, purchases -> onPurchasesUpdated(result, purchases) }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        // Reconnects by itself if Play's service drops, instead of failing the next call.
        .enableAutoServiceReconnection()
        .build()

    init {
        // Start from what this device remembers, so an offline launch keeps Pro.
        scope.launch {
            val owned = store.owned.first()
            _state.update { it.copy(owned = it.owned || owned, known = true) }
        }
    }

    override fun refresh() {
        if (client.isReady) {
            queryProduct()
            queryPurchases()
            return
        }
        if (connecting) return
        connecting = true
        _state.update { if (it.store is StoreState.Unavailable) it.copy(store = StoreState.Loading) else it }
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                connecting = false
                if (result.responseCode == BillingResponseCode.OK) {
                    queryProduct()
                    queryPurchases()
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.responseCode} ${result.debugMessage}")
                    setStore(StoreState.Unavailable(problemOf(result.responseCode)))
                }
            }

            override fun onBillingServiceDisconnected() {
                connecting = false
                // Auto reconnection handles the next call; nothing to do here.
            }
        })
    }

    override fun buy(activity: Activity, offer: ProOffer) {
        val details = product ?: run { refresh(); return }
        scope.launch {
            val params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(details)
                            .setOfferToken(offer.token)
                            .build()
                    )
                )
                .setObfuscatedAccountId(store.accountId())
                .build()
            val result = client.launchBillingFlow(activity, params)
            if (result.responseCode != BillingResponseCode.OK) handleFailure(result)
        }
    }

    override fun clearFailure() = _state.update { it.copy(failure = null) }

    override fun clearUnlocked() = _state.update { it.copy(unlockedJustNow = false) }

    // ── Play's answers ──────────────────────────────────────────────────────────

    private fun queryProduct() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(ProCatalog.PRODUCT_ID)
                        .setProductType(ProductType.INAPP)
                        .build()
                )
            )
            .build()
        client.queryProductDetailsAsync(params) { result, details ->
            if (result.responseCode != BillingResponseCode.OK) {
                setStore(StoreState.Unavailable(problemOf(result.responseCode)))
                return@queryProductDetailsAsync
            }
            val found = details.productDetailsList.firstOrNull { it.productId == ProCatalog.PRODUCT_ID }
            product = found
            val offer = found?.let { pickOffer(offersOf(it)) }
            setStore(offer?.let { StoreState.Ready(it) } ?: StoreState.Unavailable(StoreProblem.NOT_FOUND))
            watchExpiry(offer?.endsAtMillis)
        }
    }

    /** Asks Play for the product again just after [endsAtMillis], replacing any earlier wait. */
    private fun watchExpiry(endsAtMillis: Long?) {
        offerExpiry?.cancel()
        val wait = (endsAtMillis ?: return) - System.currentTimeMillis()
        // Further off than a day: the refresh on every return to the app gets there first. Already
        // past: Play still lists it (clocks differ), so don't ask in a loop; the next refresh will.
        if (wait > TimeUnit.DAYS.toMillis(1) || wait < 0) return
        offerExpiry = scope.launch {
            delay(wait + EXPIRY_GRACE_MS)
            queryProduct()
        }
    }

    private fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
        client.queryPurchasesAsync(params) { result, purchases ->
            // Only a real answer from Play may take Pro away; a failed query keeps what we know.
            if (result.responseCode == BillingResponseCode.OK) handlePurchases(purchases, complete = true)
        }
    }

    private fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingResponseCode.OK -> handlePurchases(purchases.orEmpty(), complete = false)
            BillingResponseCode.USER_CANCELED -> Unit
            // Bought before (another device, or a reinstall): fetch it and unlock.
            BillingResponseCode.ITEM_ALREADY_OWNED -> queryPurchases()
            else -> handleFailure(result)
        }
    }

    /**
     * Unlocks Pro for a verified, completed purchase of the product and acknowledges it.
     *
     * @param complete the list is everything the user owns (a query), so the absence of a purchase
     *   means Pro was refunded or revoked; otherwise it is just the purchase that changed.
     */
    private fun handlePurchases(purchases: List<Purchase>, complete: Boolean) {
        val pro = purchases.filter { ProCatalog.PRODUCT_ID in it.products }
        val bought = pro.filter {
            it.purchaseState == Purchase.PurchaseState.PURCHASED && verifier.isValid(it.originalJson, it.signature)
        }
        val pending = pro.any { it.purchaseState == Purchase.PurchaseState.PENDING }
        if (bought.size < pro.count { it.purchaseState == Purchase.PurchaseState.PURCHASED }) {
            // Not granted and not acknowledged, so Play refunds it in three days. Either a forged
            // purchase, or play.licenseKey isn't this app's key, which would refund every buyer.
            Log.e(TAG, "A completed Pro purchase failed the signature check; check play.licenseKey")
        }
        bought.filterNot { it.isAcknowledged }.forEach(::acknowledge)

        val owned = when {
            bought.isNotEmpty() -> true
            complete -> false
            else -> _state.value.owned
        }
        _state.update {
            it.copy(
                owned = owned,
                known = true,
                pending = pending && !owned,
                unlockedJustNow = it.unlockedJustNow || (owned && !it.owned && it.known),
            )
        }
        if (bought.isNotEmpty() || complete) scope.launch { store.setOwned(owned) }
    }

    private fun acknowledge(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        client.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingResponseCode.OK) {
                Log.w(TAG, "Acknowledge failed (${result.responseCode}); retried on next refresh")
            }
        }
    }

    private fun handleFailure(result: BillingResult) {
        Log.w(TAG, "Purchase failed: ${result.responseCode} ${result.debugMessage}")
        // The offer may have ended or run out between showing it and tapping buy: ask again, and
        // say so rather than "not found", since the full price is still for sale.
        val problem = if (result.responseCode == BillingResponseCode.ITEM_UNAVAILABLE) {
            queryProduct()
            StoreProblem.OFFER_ENDED
        } else {
            problemOf(result.responseCode)
        }
        _state.update { it.copy(failure = problem) }
    }

    private fun setStore(store: StoreState) = _state.update { it.copy(store = store) }

    private companion object {
        const val TAG = "PRO_BILLING"
        /** Play may still list an offer for a moment after its end time. */
        const val EXPIRY_GRACE_MS = 2_000L

        fun problemOf(code: Int): StoreProblem = when (code) {
            BillingResponseCode.BILLING_UNAVAILABLE, BillingResponseCode.FEATURE_NOT_SUPPORTED -> StoreProblem.NO_PLAY
            BillingResponseCode.SERVICE_UNAVAILABLE, BillingResponseCode.SERVICE_DISCONNECTED,
            BillingResponseCode.NETWORK_ERROR -> StoreProblem.OFFLINE
            BillingResponseCode.ITEM_UNAVAILABLE, BillingResponseCode.DEVELOPER_ERROR -> StoreProblem.NOT_FOUND
            else -> StoreProblem.OTHER
        }

        /** Play's eligible buy offers for the product, as [ProOffer]s (rentals and pre-orders left out). */
        fun offersOf(details: ProductDetails): List<ProOffer> =
            details.oneTimePurchaseOfferDetailsList.orEmpty()
                .filter { it.rentalDetails == null && it.preorderDetails == null }
                .mapNotNull { o ->
                    val token = o.offerToken ?: return@mapNotNull null
                    val full = o.fullPriceMicros?.takeIf { it > o.priceAmountMicros }
                    ProOffer(
                        token = token,
                        offerId = o.offerId,
                        price = o.formattedPrice,
                        priceMicros = o.priceAmountMicros,
                        fullPrice = full?.let { formatMicros(it, o.priceCurrencyCode) },
                        percentOff = o.discountDisplayInfo?.percentageDiscount?.takeIf { it > 0 }
                            ?: full?.let { (100 - o.priceAmountMicros * 100 / it).toInt() },
                        endsAtMillis = o.validTimeWindow?.endTimeMillis,
                    )
                }

        /** Formats a price Play gives only in micros (the full price of a discount) in its currency. */
        fun formatMicros(micros: Long, currency: String): String =
            NumberFormat.getCurrencyInstance().apply {
                this.currency = Currency.getInstance(currency)
                maximumFractionDigits = if (micros % 1_000_000 == 0L) 0 else 2
            }.format(micros / 1_000_000.0)
    }
}
