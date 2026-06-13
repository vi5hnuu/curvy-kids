package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.SeasonItem
import com.vi5hnu.curvykids.data.content.SEASONS_LIST
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.SegmentedTabs
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import kotlinx.coroutines.delay

/** Seasons — learn the 4 seasons and their weather, then quiz on season recognition. */
@Composable
fun SeasonsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var mode by rememberSaveable { mutableStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    // Quiz target: show a weather clue emoji, child picks which season it belongs to.
    val quizState = remember(round) {
        val season = SEASONS_LIST.random()
        val clueEmoji = season.weatherEmojis.random()
        Pair(season, clueEmoji)
    }
    val targetSeason = quizState.first
    val clue = quizState.second

    LaunchedEffect(mode) {
        if (mode == 1) feedback?.speaker?.speak("Which season has ${targetSeason.weather}?")
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(700)
            if (flash?.second == true) {
                if (score > 0 && score % 5 == 0) showCelebrate = true else round++
            }
            flash = null
        }
    }

    fun choose(season: SeasonItem) {
        if (flash != null) return
        val ok = season.name == targetSeason.name
        flash = season.name to ok
        if (ok) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! ${season.name}!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 24.dp),
        ) {
            ScreenHeader(
                title = topic.title,
                color = topic.color,
                onBack = onBack,
                trailing = if (mode == 1) {
                    {
                        Pill {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                            ) {
                                Text("⭐ $score", fontFamily = FontDisplay, fontSize = 16.sp, color = Color(0xFFF2A93B))
                            }
                        }
                    }
                } else null,
            )
            Spacer(Modifier.height(14.dp))
            SegmentedTabs(tabs = listOf("Learn", "Play"), selected = mode, accent = topic.color, onSelect = { mode = it })
            Spacer(Modifier.height(16.dp))

            if (mode == 0) {
                // Learn — 4 season cards showing name, main emoji, and weather clue emojis
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SEASONS_LIST.chunked(2).forEach { pair ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            pair.forEach { season ->
                                CardSurface(
                                    modifier = Modifier.weight(1f),
                                    onClick = { feedback?.speaker?.speak("${season.name}! ${season.weather}") },
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(14.dp),
                                    ) {
                                        Text(season.emoji, fontSize = 48.sp)
                                        Text(
                                            season.name,
                                            fontFamily = FontDisplay,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = season.color,
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            season.weather,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = InkSoft,
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            season.weatherEmojis.forEach { e -> Text(e, fontSize = 18.sp) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Play — show a clue emoji, pick the matching season
                CardSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(22.dp),
                    ) {
                        Text(
                            "WHICH SEASON IS THIS?",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = InkSoft,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(clue, fontSize = 64.sp)
                        Spacer(Modifier.height(8.dp))
                        Chip(
                            onClick = { feedback?.speaker?.speak("Which season has ${targetSeason.weather}?") },
                            modifier = Modifier.size(46.dp),
                        ) { Text("🔊", fontSize = 20.sp) }
                    }
                }

                Spacer(Modifier.height(20.dp))

                SEASONS_LIST.chunked(2).forEach { pair ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                    ) {
                        pair.forEach { season ->
                            val flashOk = flash?.first == season.name && flash?.second == true
                            val flashBad = flash?.first == season.name && flash?.second == false
                            Surface(
                                onClick = { choose(season) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(108.dp)
                                    .then(
                                        when {
                                            flashOk -> Modifier.border(4.dp, Green, RoundedCornerShape(24.dp))
                                            flashBad -> Modifier.border(4.dp, Color(0xFFFF6B6B), RoundedCornerShape(24.dp))
                                            else -> Modifier
                                        }
                                    ),
                                shape = RoundedCornerShape(24.dp),
                                color = Color.White,
                                shadowElevation = 4.dp,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(season.emoji, fontSize = 36.sp)
                                        Text(
                                            season.name,
                                            fontFamily = FontDisplay,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = season.color,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Season Explorer!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}
