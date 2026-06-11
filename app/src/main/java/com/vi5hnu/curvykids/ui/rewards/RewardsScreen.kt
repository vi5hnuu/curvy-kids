package com.vi5hnu.curvykids.ui.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.theme.Aqua
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.TintSun
import com.vi5hnu.curvykids.ui.theme.TintTeal

private val TOTAL_TRACE = 62 // 26 upper + 26 lower + 10 numbers

private data class Sticker(
    val id: String,
    val emoji: String,
    val name: String,
    val have: Boolean,
)

/**
 * Rewards tab — star total, mastered progress bar, sticker book grid.
 */
@Composable
fun RewardsScreen(
    appState: AppUiState,
    modifier: Modifier = Modifier,
) {
    val mastered = appState.mastered
    val stars = appState.stars
    val badges = appState.badges

    val stickers = listOf(
        Sticker("first",   "🌟", "First Star",    stars >= 1),
        Sticker("abc5",    "🔤", "5 Letters",     mastered.count { it.startsWith("upper:") } >= 5),
        Sticker("num",     "🔢", "Number Fan",    mastered.count { it.startsWith("numbers:") } >= 5),
        Sticker("p50",     "⭐", "50 Stars",      stars >= 50),
        Sticker("p100",    "🏆", "100 Stars",     stars >= 100),
        Sticker("rainbow", "🌈", "Color Pro",     badges.contains("colors")),
        Sticker("artist",  "🎨", "Lil Artist",   true),
        Sticker("spell",   "📖", "Word Builder",  mastered.any { it.startsWith("upper:") }),
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 90.dp),
    ) {
        // ── Header with Curvy ─────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            CurvyMascot(size = 62.dp, mood = CurvyMood.Cheer)
            Spacer(Modifier.size(12.dp))
            Column {
                Text(
                    text = "My Rewards",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = Ink,
                )
                Text(
                    text = "Look what you earned!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = InkSoft,
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // ── Star total card (gold gradient) ───────────────────────────────
        Surface(
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(colors = listOf(Color(0xFFFFD86B), Color(0xFFFFC24A)))
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(22.dp),
                ) {
                    Text("⭐", fontSize = 54.sp)
                    Text(
                        text = "$stars",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 46.sp,
                        color = Color.White,
                    )
                    Text(
                        text = "STARS COLLECTED",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.95f),
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Letters & Numbers progress bar ────────────────────────────────
        CardSurface(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Letters & Numbers",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Ink,
                    )
                    Text(
                        text = "${mastered.size}/$TOTAL_TRACE",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Teal,
                    )
                }
                Spacer(Modifier.height(10.dp))
                // Progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(Color(0xFFF4FAFE), RoundedCornerShape(8.dp)),
                ) {
                    val fraction = (mastered.size.toFloat() / TOTAL_TRACE).coerceIn(0f, 1f)
                    if (fraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(14.dp)
                                .background(
                                    Brush.horizontalGradient(listOf(Teal, Aqua)),
                                    RoundedCornerShape(8.dp),
                                ),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Sticker Book",
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Ink,
            modifier = Modifier.padding(horizontal = 2.dp),
        )

        Spacer(Modifier.height(12.dp))

        // ── Sticker grid (2 columns) ──────────────────────────────────────
        val rows = stickers.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
            rows.forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    StickerCell(sticker = pair[0], modifier = Modifier.weight(1f))
                    if (pair.size > 1) {
                        StickerCell(sticker = pair[1], modifier = Modifier.weight(1f))
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StickerCell(sticker: Sticker, modifier: Modifier = Modifier) {
    CardSurface(modifier = modifier) {
        Box(modifier = Modifier.padding(18.dp, 18.dp, 12.dp, 18.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (sticker.have) sticker.emoji else "🔒",
                    fontSize = 46.sp,
                    modifier = Modifier.run {
                        if (!sticker.have) this.then(Modifier) else this // grayscale via alpha
                    },
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = sticker.name,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = if (sticker.have) Ink else InkSoft,
                )
            }
            if (sticker.have) {
                Text(
                    text = "✨",
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.TopEnd),
                )
            }
        }
    }
}
