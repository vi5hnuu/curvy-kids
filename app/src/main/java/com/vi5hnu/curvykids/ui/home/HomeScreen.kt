package com.vi5hnu.curvykids.ui.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.dailyPicks
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.StarGold
import com.vi5hnu.curvykids.ui.components.TopicCard
import com.vi5hnu.curvykids.ui.components.TopicGlyph
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Teal
import java.time.LocalDate
import java.util.Calendar

/**
 * Home tab — mascot greeting, star/streak pills, an optional "Continue" card, and a short set of
 * suggested activities.
 *
 * Deliberately not the full catalogue: every topic lives on the Play tab, grouped. Home is the
 * "what shall I do now?" screen, and a small number of large targets is what a pre-reader can
 * actually choose between.
 *
 * @param onSeeAll Opens the Play tab, where all activities are browsable.
 */
@Composable
fun HomeScreen(
    appState: AppUiState,
    onOpenTopic: (Topic) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    val lastTopic = appState.lastTopic
    val today = LocalDate.now().toEpochDay()
    // Keyed on the day only. The Continue topic is filtered out of an order that does not
    // itself depend on it, so opening an activity never reshuffles the rest of the cards.
    val picks = remember(today, appState.lastTopicId) {
        dailyPicks(lastTopicId = appState.lastTopicId, seed = today)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 96.dp), // clears the floating tab bar
    ) {
        // ── Mascot + greeting ──────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            CurvyMascot(size = 66.dp, mood = CurvyMood.Happy, floating = true)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "$greeting, friend!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = InkSoft,
                )
                Text(
                    text = "Let's play & learn",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp,
                    color = Ink,
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // ── Stat pills ────────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            StatPill(
                icon = KidIcon.Star,
                tint = StarGold,
                text = "${appState.stars}",
                label = "${appState.stars} stars collected",
                modifier = Modifier.weight(1f),
            )
            StatPill(
                icon = KidIcon.Flame,
                tint = Coral,
                text = "${appState.streak} day${if (appState.streak == 1) "" else "s"}",
                label = "${appState.streak} day streak",
                modifier = Modifier.weight(1f),
            )
        }

        // ── Continue card ─────────────────────────────────────────────────
        if (lastTopic != null) {
            Spacer(Modifier.height(18.dp))
            Surface(
                onClick = { onOpenTopic(lastTopic) },
                shape = RoundedCornerShape(26.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(Teal, Color(0xFF36D0C0)))),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(52.dp),
                        ) {
                            TopicGlyph(
                                topicId = lastTopic.id,
                                fallback = lastTopic.glyph,
                                color = Color.White,
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CONTINUE",
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.5.sp,
                                color = Color.White.copy(alpha = 0.85f),
                            )
                            Text(
                                text = lastTopic.title,
                                fontFamily = FontDisplay,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color.White,
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                KidIconImage(
                                    icon = KidIcon.ArrowForward,
                                    modifier = Modifier.size(24.dp),
                                    color = Teal,
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Today's picks",
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 19.sp,
            color = Ink,
            modifier = Modifier.padding(horizontal = 2.dp),
        )

        Spacer(Modifier.height(12.dp))

        // A short, fixed set — small enough to lay out eagerly without a lazy grid, and small
        // enough for a child to compare every option at a glance.
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            picks.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TopicCard(
                        topic = pair[0],
                        onClick = { onOpenTopic(pair[0]) },
                        modifier = Modifier.weight(1f),
                    )
                    if (pair.size > 1) {
                        TopicCard(
                            topic = pair[1],
                            onClick = { onOpenTopic(pair[1]) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Route into the full catalogue ─────────────────────────────────
        Surface(
            onClick = onSeeAll,
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "See all activities",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = Teal,
                )
                Spacer(Modifier.width(8.dp))
                KidIconImage(
                    icon = KidIcon.ArrowForward,
                    modifier = Modifier.size(20.dp),
                    color = Teal,
                )
            }
        }
    }
}

/** One of the two headline counters. [label] is what a screen reader announces for the whole pill. */
@Composable
private fun StatPill(
    icon: KidIcon,
    tint: Color,
    text: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Pill(modifier = modifier.clearAndSetSemantics { contentDescription = label }) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 9.dp)
                .fillMaxWidth(),
        ) {
            KidIconImage(icon = icon, modifier = Modifier.size(18.dp), color = tint)
            Spacer(Modifier.width(6.dp))
            Text(
                text = text,
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = tint,
            )
        }
    }
}
