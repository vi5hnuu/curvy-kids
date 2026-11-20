package com.vi5hnu.curvykids.ui.pro

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/** Google's promo code page; the Play Store opens it as its "Redeem code" screen. */
private const val REDEEM_URL = "https://play.google.com/redeem"
private const val PLAY_STORE = "com.android.vending"

/**
 * Opens the Play Store's "Redeem code" screen, where a grown-up types a promo code made in the
 * Play Console (Monetize → Promotions). Play unlocks Pro on the account; the app finds it when it
 * comes back to the front (MainActivity refreshes then), and celebrates it.
 *
 * Without the Play Store app, the same page opens in the browser.
 *
 * @return false if nothing on the device can open it.
 */
fun Context.openPlayRedeem(): Boolean {
    val view = Intent(Intent.ACTION_VIEW, Uri.parse(REDEEM_URL)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return listOf(Intent(view).setPackage(PLAY_STORE), view).any { intent ->
        try {
            startActivity(intent); true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
