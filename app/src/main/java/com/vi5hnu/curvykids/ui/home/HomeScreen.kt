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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
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
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.app.AppUiState
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.TopicCard
import com.vi5hnu.curvykids.ui.theme.Coral
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.theme.Teal
import java.util.Calendar

/**
 * Home tab — mascot greeting, star/streak pills, optional "Continue" card,
 * and the 2-column topic adventure grid.
 */
@Composable
fun HomeScreen(
    appState: AppUiState,
    onOpenTopic: (Topic) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 90.dp), // clear bottom tab bar
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
            Pill(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .fillMaxWidth(),
                ) {
                    Text("⭐", fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${appState.stars}",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFFF2A93B),
                    )
                }
            }
            Pill(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .fillMaxWidth(),
                ) {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "${appState.streak} day${if (appState.streak == 1) "" else "s"}",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Coral,
                    )
                }
            }
        }

        // ── Continue card ─────────────────────────────────────────────────
        val lastTopic = appState.lastTopic
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
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Teal, Color(0xFF36D0C0))
                            )
                        ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp),
                    ) {
                        // Topic glyph badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(52.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = lastTopic.glyph,
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp,
                                    color = Color.White,
                                )
                            }
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
                            modifier = Modifier.size(42.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    tint = Teal,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        Text(
            text = "Pick an adventure",
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 19.sp,
            color = Ink,
            modifier = Modifier.padding(horizontal = 2.dp),
        )

        Spacer(Modifier.height(12.dp))

        // 2-column topic grid — inside a Column + Row (not LazyGrid) so it
        // works inside a verticalScroll without conflicting scroll axes.
        val rows = TOPICS.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { pair ->
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
    }
}
