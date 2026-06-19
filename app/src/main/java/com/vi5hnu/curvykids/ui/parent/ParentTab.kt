package com.vi5hnu.curvykids.ui.parent

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.config.LegalLinks
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Teal

/**
 * "Grown-ups" tab landing page. Tapping "Unlock Parent Zone" triggers
 * the ParentGate (math puzzle) bottom sheet in the parent composable.
 */
@Composable
fun ParentTab(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 90.dp),
    ) {
        Text(
            text = "Grown-ups",
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = Ink,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Settings & progress for parents",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = InkSoft,
        )
        Spacer(Modifier.height(22.dp))

        // Lock card
        CardSurface(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(26.dp),
            ) {
                Text("🔒", fontSize = 48.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Parent Zone",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = Ink,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "See progress, sounds & reminders. A quick puzzle keeps little fingers out.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = InkSoft,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Spacer(Modifier.height(18.dp))
                CandyButton(
                    onClick = onUnlock,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    containerColor = Teal,
                ) {
                    Text(
                        text = "Unlock Parent Zone",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Curvy + no-ads tagline
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp),
        ) {
            CurvyMascot(size = 50.dp, mood = CurvyMood.Idle)
            Spacer(Modifier.width(12.dp))
            Text(
                text = "CurvyKids has no ads and no tracking. Made for happy, safe learning. 💚",
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = InkSoft,
                lineHeight = 20.sp,
            )
        }

        Spacer(Modifier.height(20.dp))

        // Legal — Privacy Policy & Terms (open the hosted pages in the browser). Placed in the
        // Grown-ups tab so they're available to adults without the parent gate.
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LegalLink(text = "Privacy Policy", url = LegalLinks.PRIVACY_POLICY)
            Text(
                text = "  ·  ",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = InkSoft,
            )
            LegalLink(text = "Terms of Service", url = LegalLinks.TERMS_OF_SERVICE)
        }
    }
}

/** A small text link in the Grown-ups footer that opens [url] in the device browser. */
@Composable
private fun LegalLink(text: String, url: String) {
    val context = LocalContext.current
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = Teal,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            .padding(vertical = 4.dp, horizontal = 2.dp),
    )
}
