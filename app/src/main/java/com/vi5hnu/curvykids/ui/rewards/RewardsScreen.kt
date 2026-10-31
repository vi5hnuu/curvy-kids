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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.vi5hnu.curvykids.ui.kit.SceneBackground
import com.vi5hnu.curvykids.ui.kit.Responsive
import com.vi5hnu.curvykids.ui.kit.kidContentWidth
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import com.vi5hnu.curvykids.ui.kit.KidBackButton
import com.vi5hnu.curvykids.ui.theme.KidSun
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.data.progress.TraceProgress
import com.vi5hnu.curvykids.data.progress.traceProgress
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.SvgBadge
import com.vi5hnu.curvykids.ui.theme.Aqua
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkFaint
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Sun
import com.vi5hnu.curvykids.ui.theme.Teal
import com.vi5hnu.curvykids.ui.theme.TintSun
import com.vi5hnu.curvykids.ui.theme.TintTeal
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.data.content.AssetPath

/**
 * One entry in the sticker book.
 *
 * @param asset Illustration shown once earned; a locked sticker shows a padlock instead.
 * @param have  Whether the child has met the condition. Every sticker must have a real
 *              condition — one that is always true teaches nothing and cheapens the rest.
 */
/** Earned art and the padlock share a size so the grid rows line up. */
private val STICKER_ART = 52.dp

private data class Sticker(
    val id: String,
    val asset: AssetPath,
    val name: String,
    val have: Boolean,
)

/**
 * Rewards tab — star total, mastered progress bar, sticker book grid.
 */
@Composable
fun RewardsScreen(
    appState: AppUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val mastered = appState.mastered
    val stars = appState.stars
    val badges = appState.badges

    val stickers = listOf(
        Sticker("first",   KidAssets.weather("glowing-star"), "First Star",   stars >= 1),
        Sticker("abc5",    KidAssets.uppercase('A'),          "5 Letters",    mastered.count { it.startsWith("upper:") } >= 5),
        Sticker("num",     KidAssets.number(5),               "Number Fan",   mastered.count { it.startsWith("numbers:") } >= 5),
        Sticker("p50",     KidAssets.weather("star"),         "50 Stars",     stars >= 50),
        Sticker("p100",    KidAssets.clothes("crown"),        "100 Stars",    stars >= 100),
        Sticker("rainbow", KidAssets.weather("rainbow"),      "Color Pro",    badges.contains("colors")),
        Sticker("artist",  KidAssets.job("artist"),           "Lil Artist",   badges.contains("artist")),
        Sticker("spell",   KidAssets.toy("puzzle"),           "Word Builder", mastered.count { it.startsWith("lower:") } >= 5),
    )

    SceneBackground(modifier) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
    val twoPane = Responsive.isTwoPane(maxWidth, maxHeight)
    val columns = if (Responsive.isWide(maxWidth)) 4 else 2
    Column(
        modifier = Modifier
            .kidContentWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 24.dp),
    ) {
        // ── Header with Curvy ─────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            KidBackButton(onClick = onBack, color = KidSun)
            Spacer(Modifier.size(12.dp))
            CurvyMascot(size = 62.dp, mood = CurvyMood.Cheer)
            Spacer(Modifier.size(12.dp))
            Column {
                Text(
                    text = "My Stickers",
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

        if (twoPane) {
            // Landscape: totals on the left, the stickers in view on the right.
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(Modifier.weight(0.4f)) { Totals(stars, mastered) }
                Column(Modifier.weight(0.6f)) { StickerBook(stickers, columns = 3) }
            }
        } else {
            Totals(stars, mastered)
            Spacer(Modifier.height(10.dp))
            StickerBook(stickers, columns)
        }
    }
    }
    }
}

/** The star total and one mastery bar per writing system. */
@Composable
private fun Totals(stars: Int, mastered: List<String>) {
    StarTotalCard(stars)
    Spacer(Modifier.height(20.dp))
    traceProgress(mastered).forEach { progress ->
        ProgressCard(progress)
        Spacer(Modifier.height(12.dp))
    }
}

/** The gold card with the number of stars collected. */
@Composable
private fun StarTotalCard(stars: Int) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(colors = listOf(Color(0xFFFFD86B), Color(0xFFFFC24A)))),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(22.dp),
            ) {
                KidIconImage(icon = KidIcon.Star, modifier = Modifier.size(58.dp), color = Color.White)
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
}

/** The sticker grid, [columns] to a row. */
@Composable
private fun StickerBook(stickers: List<Sticker>, columns: Int) {
    Text(
        text = "Sticker Book",
        fontFamily = FontDisplay,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = Ink,
        modifier = Modifier.padding(horizontal = 2.dp),
    )
    Spacer(Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
        stickers.chunked(columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                row.forEach { StickerCell(sticker = it, modifier = Modifier.weight(1f)) }
                // Keep a short last row's cells the same width as the rest.
                repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** One writing system's mastery bar. */
@Composable
private fun ProgressCard(progress: TraceProgress) {
    CardSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = progress.label,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Ink,
                )
                Text(
                    text = "${progress.done}/${progress.total}",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Teal,
                )
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .background(Color(0xFFF4FAFE), RoundedCornerShape(8.dp)),
            ) {
                if (progress.fraction > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.fraction)
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
}

/** Greyed and faded: the shape of a sticker not won yet. */
private val LOCKED_FILTER = ColorFilter.colorMatrix(
    ColorMatrix().apply { setToSaturation(0f) }.also { m ->
        // Lighten and fade so it reads as "not yet", not as a black-and-white picture.
        m.timesAssign(ColorMatrix(floatArrayOf(
            0.5f, 0f, 0f, 0f, 110f,
            0f, 0.5f, 0f, 0f, 110f,
            0f, 0f, 0.5f, 0f, 120f,
            0f, 0f, 0f, 0.55f, 0f,
        )))
    }
)

@Composable
private fun StickerCell(sticker: Sticker, modifier: Modifier = Modifier) {
    CardSurface(modifier = modifier) {
        Box(modifier = Modifier.padding(18.dp, 18.dp, 12.dp, 18.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // A locked sticker shows its own picture as a grey shape, so the child can
                // see what there is to win rather than a row of identical padlocks.
                Box(contentAlignment = Alignment.BottomEnd) {
                    SvgArt(
                        asset = sticker.asset,
                        modifier = Modifier.size(STICKER_ART),
                        contentDescription = if (sticker.have) sticker.name else "${sticker.name}, locked",
                        colorFilter = if (sticker.have) null else LOCKED_FILTER,
                    )
                    if (!sticker.have) {
                        KidIconImage(
                            icon = KidIcon.Lock,
                            modifier = Modifier.size(26.dp).background(Color.White, RoundedCornerShape(50)).padding(4.dp),
                            color = InkSoft,
                        )
                    }
                }
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
                KidIconImage(
                    icon = KidIcon.Sparkle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp),
                    color = Sun,
                )
            }
        }
    }
}
