package com.vi5hnu.curvykids.ui.pro

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.vi5hnu.curvykids.billing.ProOffer
import com.vi5hnu.curvykids.ui.theme.KidBerry
import com.vi5hnu.curvykids.ui.theme.KidGrape

/**
 * A Pro discount, told to the grown-up on Home: "Grown-ups: Pro 33% off · ₹199, ends in 3 days".
 *
 * Play's Families policy doesn't allow pressing children to buy, so this is quiet on purpose
 * ([GrownUpNotice]), and both of its buttons ([onOpen], [onHide]) are expected to go through the
 * parent gate first. Shown only for a live discount (see [com.vi5hnu.curvykids.billing.bannerOffer]).
 */
@Composable
fun OfferBanner(offer: ProOffer, onOpen: () -> Unit, onHide: () -> Unit, modifier: Modifier = Modifier) {
    val ends = offerEndText(offer.endsAtMillis)?.removePrefix("Offer ")
    GrownUpNotice(
        iconColor = KidGrape,
        icon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp)) },
        title = buildAnnotatedString {
            append("Grown-ups: Pro ")
            withStyle(SpanStyle(color = KidBerry)) { append(offer.percentOff?.let { "$it% off" } ?: "on sale") }
        },
        body = listOfNotNull("Now ${offer.price}", ends).joinToString(" · "),
        hideLabel = "Hide this offer",
        onOpen = onOpen,
        onHide = onHide,
        modifier = modifier,
    )
}
