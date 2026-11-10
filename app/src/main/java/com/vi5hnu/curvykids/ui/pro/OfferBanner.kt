package com.vi5hnu.curvykids.ui.pro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.billing.ProOffer
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidBerry
import com.vi5hnu.curvykids.ui.theme.KidGrape
import com.vi5hnu.curvykids.ui.theme.KidInk

/**
 * A Pro discount, told to the grown-up on Home: "Grown-ups: Pro 33% off · ₹199, ends in 3 days".
 *
 * Play's Families policy doesn't allow pressing children to buy, so this is quiet on purpose: a
 * slim line under the counters, never spoken, no animation, and both of its buttons ([onOpen],
 * [onHide]) are expected to go through the parent gate first. Shown only for a live discount
 * (see [com.vi5hnu.curvykids.billing.bannerOffer]).
 */
@Composable
fun OfferBanner(offer: ProOffer, onOpen: () -> Unit, onHide: () -> Unit, modifier: Modifier = Modifier) {
    val ends = offerEndText(offer.endsAtMillis)?.removePrefix("Offer ")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable(role = Role.Button, onClick = onOpen)
            .padding(start = 8.dp, top = 6.dp, bottom = 6.dp),
    ) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(KidGrape), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                buildAnnotatedString {
                    append("Grown-ups: Pro ")
                    withStyle(SpanStyle(color = KidBerry)) {
                        append(offer.percentOff?.let { "$it% off" } ?: "on sale")
                    }
                },
                fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = KidInk,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Text(
                listOfNotNull("Now ${offer.price}", ends).joinToString(" · "),
                fontWeight = FontWeight.Bold, fontSize = 12.sp, color = KidInk.copy(alpha = 0.6f),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onHide) {
            Icon(Icons.Rounded.Close, contentDescription = "Hide this offer", tint = KidInk.copy(alpha = 0.45f),
                modifier = Modifier.size(20.dp))
        }
    }
}
