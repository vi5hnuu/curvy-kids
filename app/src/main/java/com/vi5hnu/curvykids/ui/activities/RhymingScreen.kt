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
import com.vi5hnu.curvykids.data.content.RHYME_GROUPS
import com.vi5hnu.curvykids.data.content.RhymeGroup
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

private data class RhymeChoice(val word: String, val emoji: String, val isRhyme: Boolean)

private data class RhymeSpec(
    val group: RhymeGroup,
    val correctRhyme: Pair<String, String>,  // word to emoji
    val choices: List<RhymeChoice>,
)

private fun buildRhymeSpec(round: Int): RhymeSpec {
    val group = RHYME_GROUPS[round % RHYME_GROUPS.size]
    val correct = group.rhymes.random()

    // Distractors from other rhyme groups — pick words that don't rhyme with this group
    val distractors = RHYME_GROUPS
        .filter { it != group }
        .flatMap { it.rhymes }
        .shuffled()
        .take(2)

    val choices = (distractors.map { RhymeChoice(it.first, it.second, false) } +
            RhymeChoice(correct.first, correct.second, true)).shuffled()

    return RhymeSpec(group, correct, choices)
}

/** Rhyming — hear/see a word, tap the word that rhymes with it. */
@Composable
fun RhymingScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var score by remember { mutableIntStateOf(0) }
    var round by remember { mutableIntStateOf(0) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    val spec = remember(round) { buildRhymeSpec(round) }

    fun speakPrompt() {
        feedback?.speaker?.speak("Which word rhymes with ${spec.group.word}?")
    }

    fun speakChoices() {
        // Speak all 3 choices in sequence so the child can hear the sounds
        val words = spec.choices.joinToString(", ") { it.word }
        feedback?.speaker?.speak(words)
    }

    LaunchedEffect(round) {
        speakPrompt()
    }

    fun choose(choice: RhymeChoice) {
        if (flash != null) return
        flash = choice.word to choice.isRhyme
        if (choice.isRhyme) {
            score += 1
            onReward(3)
            feedback?.correct()
            feedback?.speaker?.speak("Yes! ${spec.group.word} and ${choice.word} rhyme!")
        } else {
            feedback?.wrong()
            feedback?.speaker?.speak("Try again")
        }
    }

    LaunchedEffect(flash) {
        if (flash != null) {
            delay(800)
            if (flash?.second == true) {
                if (score > 0 && score % 5 == 0) showCelebrate = true else round++
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

            // Prompt card
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        "WHICH WORD RHYMES?",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = InkSoft,
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(spec.group.emoji, fontSize = 56.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        spec.group.word,
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 34.sp,
                        color = topic.color,
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Chip(onClick = { speakPrompt() }, modifier = Modifier.size(46.dp)) {
                            Text("🔊", fontSize = 20.sp)
                        }
                        Chip(onClick = { speakChoices() }, modifier = Modifier.size(46.dp)) {
                            Text("🎵", fontSize = 20.sp)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tap 🎵 to hear the choices",
                        fontSize = 11.sp,
                        color = InkSoft,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // 3 choice tiles in a row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                spec.choices.forEach { choice ->
                    val flashOk = flash?.first == choice.word && flash?.second == true
                    val flashBad = flash?.first == choice.word && flash?.second == false
                    Surface(
                        onClick = { choose(choice) },
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
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
                                Text(choice.emoji, fontSize = 32.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    choice.word,
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF2B3A4A),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "Rhyme Master!",
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; round++ },
            )
        }
    }
}
