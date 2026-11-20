package com.vi5hnu.curvykids.billing

import android.util.Base64
import android.util.Log
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * Checks that a purchase really came from Google Play: Play signs every purchase's JSON with the
 * app's private key, and [base64Key] is its public half (Play Console > Monetization setup >
 * Licensing). A purchase whose signature doesn't match was made up on the device and is refused.
 *
 * This is the on-device check; with a backend, the purchase token would also be confirmed with
 * the Play Developer API (Purchases.products:get).
 *
 * @param allowWithoutKey treat purchases as valid when no key is configured. Only for debug
 *   builds testing real billing before the key is set; a release build without a key grants nothing.
 */
class PurchaseVerifier(base64Key: String, private val allowWithoutKey: Boolean) {

    private val key: PublicKey? = base64Key.takeIf { it.isNotBlank() }?.let {
        runCatching {
            KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(Base64.decode(it, Base64.DEFAULT)))
        }.onFailure { e -> Log.e(TAG, "Play licence key is not a valid RSA key", e) }.getOrNull()
    }

    /** Whether [signature] is Play's signature of [signedData] (the purchase's original JSON). */
    fun isValid(signedData: String, signature: String): Boolean {
        val publicKey = key ?: return allowWithoutKey.also { if (it) Log.w(TAG, "No licence key: purchase not verified") }
        if (signature.isBlank()) return false
        return runCatching {
            Signature.getInstance("SHA1withRSA").run {
                initVerify(publicKey)
                update(signedData.toByteArray())
                verify(Base64.decode(signature, Base64.DEFAULT))
            }
        }.getOrDefault(false)
    }

    private companion object {
        const val TAG = "PRO_VERIFY"
    }
}
