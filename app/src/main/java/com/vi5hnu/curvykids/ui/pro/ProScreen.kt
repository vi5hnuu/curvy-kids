package com.vi5hnu.curvykids.ui.pro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.billing.ProOffer
import com.vi5hnu.curvykids.billing.ProState
import com.vi5hnu.curvykids.billing.StoreProblem
import com.vi5hnu.curvykids.billing.StoreState
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.ConfettiOverlay
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.kit.kidContentWidth
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.KidBerry
import com.vi5hnu.curvykids.ui.theme.KidGrape
import com.vi5hnu.curvykids.ui.theme.Teal
import java.util.concurrent.TimeUnit

/** What Pro adds, shown to the grown-up deciding. */
private val PRO_BENEFITS = listOf(
    "Every activity in all six worlds",
    "All Hindi letters, the full Barakhadi and every story",
    "No ads",
    "Everything we add later",
    "One payment, no subscription. Works offline.",
)

/**
 * The purchase screen, for grown-ups only: it is reached through the ParentGate (from a locked
 * activity or from the Parent Zone), as Play's Families policy asks.
 *
 * Shows whatever Play offers right now: the base price, or a discount offer made in the Play
 * Console with the old price struck through, the saving and when it ends. Stateless: everything
 * comes from [state], and buying goes back through [onBuy].
 *
 * @param onBuy   Starts Play's purchase sheet for the offer shown.
 * @param onRetry Asks Play again (also restores a purchase made before, on any device).
 * @param onRedeem Opens the Play Store's "Redeem code" screen for a promo code.
 */
@Composable
fun ProScreen(
    state: ProState,
    onBuy: (ProOffer) -> Unit,
    onRetry: () -> Unit,
    onDismissFailure: () -> Unit,
    onRedeem: () -> Unit,
    onBack: () -> Unit,
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.kidContentWidth().fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        ) {
            ScreenHeader(title = "CurvyKids Pro", color = KidGrape, onBack = onBack)
            Spacer(Modifier.height(8.dp))
            CurvyMascot(size = 120.dp, mood = if (state.owned) CurvyMood.Cheer else CurvyMood.Wow, floating = true)
            Spacer(Modifier.height(14.dp))

            when {
                state.owned -> Unlocked()
                state.pending -> Message(
                    title = "Payment pending",
                    body = "Pro unlocks by itself as soon as the payment completes.",
                )
                else -> {
                    Benefits()
                    Spacer(Modifier.height(18.dp))
                    when (val store = state.store) {
                        StoreState.Loading -> CircularProgressIndicator(color = KidGrape, modifier = Modifier.padding(24.dp))
                        is StoreState.Ready -> PriceCard(store.offer, onBuy)
                        is StoreState.Unavailable -> Problem(store.problem, onRetry)
                    }
                    state.failure?.let { problem ->
                        Spacer(Modifier.height(12.dp))
                        Text(problemText(problem), color = KidBerry, fontWeight = FontWeight.Bold,
                            fontSize = 14.sp, textAlign = TextAlign.Center)
                        TextButton(onClick = onDismissFailure) { Text("OK") }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onRetry) {
                        Text("Already bought? Restore purchase", color = InkSoft, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onRedeem) {
                        Text("Have a promo code?", color = InkSoft, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (state.owned) ConfettiOverlay(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun Benefits() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color.White).padding(18.dp),
    ) {
        PRO_BENEFITS.forEach { line ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(Teal))
                Spacer(Modifier.width(12.dp))
                Text(line, color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

/** The price, and for a discount offer the old price, the saving and the time left. */
@Composable
private fun PriceCard(offer: ProOffer, onBuy: (ProOffer) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color.White).padding(18.dp),
    ) {
        if (offer.isDiscount) {
            offer.percentOff?.let {
                Text("$it% OFF", color = Color.White, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(KidBerry)
                        .padding(horizontal = 12.dp, vertical = 4.dp))
                Spacer(Modifier.height(8.dp))
            }
            Text(offer.fullPrice.orEmpty(), color = InkSoft, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                textDecoration = TextDecoration.LineThrough)
        }
        Text(offer.price, color = Ink, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 40.sp)
        Text(
            offerEndText(offer.endsAtMillis) ?: "One-time payment",
            color = if (offer.endsAtMillis != null) KidBerry else InkSoft,
            fontWeight = FontWeight.Bold, fontSize = 14.sp,
        )
        Spacer(Modifier.height(16.dp))
        CandyButton(
            onClick = { onBuy(offer) },
            containerColor = KidGrape,
            modifier = Modifier.fillMaxWidth().height(64.dp),
        ) {
            Text("Unlock for ${offer.price}", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun Problem(problem: StoreProblem, onRetry: () -> Unit) {
    Message(title = "Can't reach Google Play", body = problemText(problem))
    Spacer(Modifier.height(12.dp))
    CandyButton(onClick = onRetry, containerColor = KidGrape, modifier = Modifier.height(56.dp)) {
        Text("Try again", fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,
            modifier = Modifier.padding(horizontal = 24.dp))
    }
}

@Composable
private fun Unlocked() {
    Message(title = "Pro unlocked!", body = "Thank you! Every activity is open now.")
}

@Composable
private fun Message(title: String, body: String) {
    Text(title, color = KidGrape, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp,
        textAlign = TextAlign.Center)
    Spacer(Modifier.height(6.dp))
    Text(body, color = InkSoft, fontWeight = FontWeight.Bold, fontSize = 15.sp, textAlign = TextAlign.Center)
}

private fun problemText(problem: StoreProblem): String = when (problem) {
    StoreProblem.NO_PLAY -> "Google Play isn't available. Check that you're signed in to Play and purchases are allowed on this device."
    StoreProblem.OFFLINE -> "Check the internet connection and try again."
    StoreProblem.NOT_FOUND -> "Pro isn't available right now. Install CurvyKids from Google Play and try again."
    StoreProblem.OFFER_ENDED -> "That offer has just ended. Here is the price now."
    StoreProblem.OTHER -> "Something went wrong with the purchase. Please try again."
}

/** "Offer ends in 3 days" / "in 5 hours" / "soon", or null when the offer has no end date. */
internal fun offerEndText(endsAtMillis: Long?, now: Long = System.currentTimeMillis()): String? {
    val left = (endsAtMillis ?: return null) - now
    if (left <= 0) return null
    val hours = TimeUnit.MILLISECONDS.toHours(left)
    return when {
        // Rounded to the nearest day, so an offer with 71 hours left reads "3 days", not "2".
        hours >= 48 -> "Offer ends in ${(hours + 12) / 24} days"
        hours >= 2 -> "Offer ends in $hours hours"
        else -> "Offer ends soon"
    }
}
