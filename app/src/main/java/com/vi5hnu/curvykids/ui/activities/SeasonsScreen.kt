package com.vi5hnu.curvykids.ui.activities

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.SEASONS_LIST
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.ChoiceArt
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.SegmentedTabs
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/** Seasons — learn the four seasons and their weather, then pick the season for a weather clue. */
@Composable
fun SeasonsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    val tabs: @Composable () -> Unit = {
        SegmentedTabs(
            tabs = listOf("Learn", "Play"),
            icons = listOf(KidIcon.Sparkle, KidIcon.Pointer),
            selected = mode,
            accent = topic.color,
            onSelect = { mode = it },
        )
    }
    if (mode == 0) SeasonsLearn(topic, onBack, feedback, tabs) else SeasonsQuiz(topic, onBack, onReward, feedback, tabs)
}

@Composable
private fun SeasonsLearn(topic: Topic, onBack: () -> Unit, feedback: PlayFeedback?, tabs: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
    ) {
        ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
        Spacer(Modifier.height(14.dp))
        tabs()
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SEASONS_LIST.chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                    pair.forEach { season ->
                        CardSurface(
                            modifier = Modifier.weight(1f),
                            onClick = { feedback?.speaker?.say(EnglishVoiceScript.say(EnglishVoiceScript.seasonCall(season))) },
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(14.dp),
                            ) {
                                SvgArt(season.svg, modifier = Modifier.size(80.dp))
                                Text(season.name, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp, color = season.color)
                                Text(season.weather, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkSoft,
                                    textAlign = TextAlign.Center)
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    season.weatherSvgs.forEach { SvgArt(it, modifier = Modifier.size(40.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SeasonsQuiz(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback?,
    tabs: @Composable () -> Unit,
) {
    val session = rememberQuizSession(feedback, onReward)
    val (target, clue) = remember(session.round) {
        val season = SEASONS_LIST.random()
        season to season.weatherSvgs.random()
    }
    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = SEASONS_LIST,
        correct = target,
        keyOf = { it.name },
        nameOf = { it.name },
        speakPrompt = { flush -> feedback?.speaker?.say(EnglishVoiceScript.say(EnglishVoiceScript.whichSeason(target)), flush = flush) },
        celebrateTitle = "Season Explorer!",
        promptLabel = "WHICH SEASON IS THIS?",
        prompt = { SvgArt(clue, modifier = Modifier.size(96.dp)) },
        choiceContent = { ChoiceArt(it.svg, it.name, it.color) },
        columns = 2,
        belowHeader = tabs,
    )
}
