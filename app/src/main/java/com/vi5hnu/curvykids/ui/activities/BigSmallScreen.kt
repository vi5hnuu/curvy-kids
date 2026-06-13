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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.GAME_EMOJIS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import kotlinx.coroutines.delay

/** Big or Small — two sizes of the same item; tap the bigger (or smaller) one. */
@Composable
fun BigSmallScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var emoji by remember { mutableStateOf(GAME_EMOJIS.random()) }
    var bigOnLeft by remember { mutableStateOf(true) }
    var askBig by remember { mutableStateOf(true) }
    var flash by remember { mutableStateOf<Pair<Boolean, Boolean>?>(null) } // side(left) → correct?
    var showCelebrate by remember { mutableStateOf(false) }

    fun prompt() = if (askBig) "Tap the BIG one" else "Tap the small one"

    fun newRound() {
        emoji = GAME_EMOJIS.random()
        bigOnLeft = listOf(true, false).random()
        askBig = listOf(true, false).random()
        feedback?.speaker?.speak(prompt())
    }

    LaunchedEffect(Unit) { feedback?.speaker?.speak(prompt()) }

    // correct side is left when (askBig && bigOnLeft) or (!askBig && !bigOnLeft)
    val correctLeft = askBig == bigOnLeft

    fun choose(left: Boolean) {
        if (flash != null) return
        val ok = left == correctLeft
        flash = left to ok
        if (ok) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(700)
            if (flash?.second == true) {
                if (score > 0 && score % 5 == 0) showCelebrate = true else newRound()
            }
            flash = null
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
                trailing = {
                    Pill {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            Text("⭐ $score", fontFamily = FontDisplay, fontSize = 16.sp, color = Color(0xFFF2A93B))
                        }
                    }
                },
            )
            Spacer(Modifier.height(16.dp))

            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp),
                ) {
                    Text(
                        text = if (askBig) "WHICH IS BIGGER?" else "WHICH IS SMALLER?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(10.dp))
                    Chip(onClick = { feedback?.speaker?.speak(prompt()) }, modifier = Modifier.size(46.dp)) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                ChoiceCard(emoji, big = bigOnLeft, flashOk = flash?.first == true && flash?.second == true, flashBad = flash?.first == true && flash?.second == false, accent = topic.color, onTap = { choose(true) }, modifier = Modifier.weight(1f))
                ChoiceCard(emoji, big = !bigOnLeft, flashOk = flash?.first == false && flash?.second == true, flashBad = flash?.first == false && flash?.second == false, accent = topic.color, onTap = { choose(false) }, modifier = Modifier.weight(1f))
            }
        }

        if (showCelebrate) {
            Celebrate(title = "Size Star!", sub = "$score correct!", stars = 3, onDone = { showCelebrate = false; newRound() })
        }
    }
}

@Composable
private fun ChoiceCard(
    emoji: String,
    big: Boolean,
    flashOk: Boolean,
    flashBad: Boolean,
    accent: Color,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onTap,
        modifier = modifier
            .height(180.dp)
            .then(
                when {
                    flashOk -> Modifier.border(4.dp, Green, RoundedCornerShape(26.dp))
                    flashBad -> Modifier.border(4.dp, Color(0xFFFF6B6B), RoundedCornerShape(26.dp))
                    else -> Modifier
                }
            ),
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        shadowElevation = 4.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = if (big) 110.sp else 52.sp)
        }
    }
}
