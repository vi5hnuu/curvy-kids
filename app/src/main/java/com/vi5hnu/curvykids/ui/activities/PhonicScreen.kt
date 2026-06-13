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
import com.vi5hnu.curvykids.data.content.LetterPhonic
import com.vi5hnu.curvykids.data.content.PHONICS_DATA
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

private data class PhonicSpec(
    val target: LetterPhonic,
    val choices: List<LetterPhonic>, // 3 items: correct + 2 distractors
)

private fun buildPhonicSpec(usedLetters: Set<String>): PhonicSpec {
    val pool = PHONICS_DATA.filter { it.letter !in usedLetters }.takeIf { it.isNotEmpty() } ?: PHONICS_DATA
    val target = pool.random()
    val distractors = PHONICS_DATA.filter { it.letter != target.letter }.shuffled().take(2)
    return PhonicSpec(target, (distractors + target).shuffled())
}

/** Letter Sounds (Phonics) — see a letter, tap the sound it makes. */
@Composable
fun PhonicScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }
    // Track letters seen this session so we rotate through the alphabet
    val seen = remember { mutableSetOf<String>() }

    val spec = remember(round) {
        val s = buildPhonicSpec(seen)
        seen.add(s.target.letter)
        if (seen.size >= PHONICS_DATA.size) seen.clear()
        s
    }
    val target = spec.target

    fun speakPrompt() = feedback?.speaker?.speak("${target.letter}. What sound does ${target.letter} make?")

    LaunchedEffect(round) { speakPrompt() }

    fun choose(phonic: LetterPhonic) {
        if (flash != null) return
        val ok = phonic.letter == target.letter
        flash = phonic.letter to ok
        if (ok) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("${target.letter} says ${target.sound}! Like ${target.example}!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(900)
            if (flash?.second == true) {
                if (score > 0 && score % 6 == 0) showCelebrate = true else round++
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

            // Large letter card
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp),
                ) {
                    Text(
                        "WHAT SOUND DOES THIS MAKE?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = InkSoft,
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        target.letter,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 96.sp,
                        color = topic.color,
                    )
                    Text(
                        target.emoji,
                        fontSize = 36.sp,
                    )
                    Text(
                        target.example,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = topic.color.copy(alpha = 0.6f),
                    )
                    Spacer(Modifier.height(12.dp))
                    Chip(onClick = { speakPrompt() }, modifier = Modifier.size(46.dp)) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // Sound choices — displayed as text labels (the sound written out)
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                spec.choices.forEach { phonic ->
                    val flashOk = flash?.first == phonic.letter && flash?.second == true
                    val flashBad = flash?.first == phonic.letter && flash?.second == false
                    Surface(
                        onClick = { choose(phonic) },
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
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
                                Text(
                                    "\"${phonic.sound}\"",
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 22.sp,
                                    color = topic.color,
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Sound Expert!",
                sub = "You know $score letter sounds!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}
