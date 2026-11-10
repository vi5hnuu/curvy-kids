package com.vi5hnu.curvykids.billing

/**
 * One way to buy Pro, as Play offers it to this user right now: the base price, or a discount
 * offer created in the Play Console. Play returns only offers the user is eligible for (country,
 * dates, redemption limit), so anything here can be bought at [price].
 *
 * @param token       Passed back to Play to buy at exactly this offer's price.
 * @param offerId     The discount offer's id from the Console; null for the plain purchase option.
 * @param price       What the user pays, formatted by Play in their currency ("₹199").
 * @param fullPrice   The undiscounted price, formatted, when this is a discount; else null.
 * @param percentOff  The discount as shown in the Console ("33"), when it was set as a percentage.
 * @param endsAtMillis When the offer stops, if it has an end date.
 */
data class ProOffer(
    val token: String,
    val offerId: String?,
    val price: String,
    val priceMicros: Long,
    val fullPrice: String?,
    val percentOff: Int?,
    val endsAtMillis: Long?,
) {
    val isDiscount: Boolean get() = fullPrice != null
}

/** The offer to show: the cheapest one Play allows this user. */
fun pickOffer(offers: List<ProOffer>): ProOffer? = offers.minByOrNull { it.priceMicros }

/** Why Play can't sell Pro right now, in terms a parent can act on. */
enum class StoreProblem {
    /** No Play Store, or it is blocked (some kids' modes) or signed out. */
    NO_PLAY,
    /** No internet, or Play didn't answer. */
    OFFLINE,
    /** Play doesn't know the product: an app not installed from Play, or not set up yet. */
    NOT_FOUND,
    /** Play refused the purchase for another reason. */
    OTHER,
}

/** What the store can offer right now. */
sealed interface StoreState {
    data object Loading : StoreState
    data class Ready(val offer: ProOffer) : StoreState
    data class Unavailable(val problem: StoreProblem) : StoreState
}

/**
 * Pro as the app knows it.
 *
 * @param owned   Pro is unlocked: a verified purchase, or the saved record of one while Play
 *                can't be asked (offline).
 * @param known   [owned] has been read (from the device's record or from Play). Until then
 *                nothing should be shown as locked or unlocked.
 * @param pending A purchase is waiting on payment (cash or slow card); not unlocked until it clears.
 * @param store   What Play is offering, for the purchase screen.
 * @param failure Why the last purchase attempt failed, until the screen shows it.
 */
data class ProState(
    val owned: Boolean = false,
    val known: Boolean = false,
    val pending: Boolean = false,
    val store: StoreState = StoreState.Loading,
    val failure: StoreProblem? = null,
)
