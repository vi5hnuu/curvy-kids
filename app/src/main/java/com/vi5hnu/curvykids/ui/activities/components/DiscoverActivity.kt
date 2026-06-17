package com.vi5hnu.curvykids.ui.activities.components

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
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Green
import com.vi5hnu.curvykids.ui.theme.InkSoft
import kotlinx.coroutines.delay

/**
 * Shared scaffold for the four "Discover" activities (Shapes, Animals, Body, Days).
 *
 * Renders the header, a Learn/Play [SegmentedTabs] toggle, and — for the Play tab — a complete
 * round-based "find the right one" quiz: a prompt card, a 2-column option grid, correct/wrong
 * flash feedback, star rewards, and a [Celebrate] overlay every [celebrateEvery] correct picks.
 *
 * Each screen supplies only its data and three render slots, so the quiz logic lives in exactly
 * one place (high cohesion, no per-screen duplication).
 *
 * @param quizItems       Pool of items to quiz on; one is the target, the rest supply distractors.
 * @param quizPromptLabel Small uppercase label above the target ("FIND THE SHAPE").
 * @param keyOf           Stable unique key per item (used for matching + distractor de-duping).
 * @param speakFor        Spoken prompt for the current target ("Find the Circle").
 * @param learnContent    The explore-mode UI (preserves the original tap-to-learn grid).
 * @param quizPrompt      Renders the large target inside the prompt card.
 * @param quizOption      Renders one option tile's inner visual (shape/emoji/day).
 */
@Composable
fun <T> DiscoverActivity(
    topic: Topic,
    onBack: () -> Unit,
    quizItems: List<T>,
    quizPromptLabel: String,
    keyOf: (T) -> String,
    speakFor: (T) -> String,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback?,
    learnContent: @Composable () -> Unit,
    quizPrompt: @Composable (target: T) -> Unit,
    quizOption: @Composable (item: T) -> Unit,
    rewardPerCorrect: Int = 3,
    optionCount: Int = 4,
    celebrateEvery: Int = 5,
    celebrateTitle: String = "Great job!",
) {
    var mode by rememberSaveable { mutableStateOf(0) } // 0 = Learn, 1 = Play

    // Quiz state — session-scoped; resets only when the screen leaves composition.
    var score by remember { mutableIntStateOf(0) }
    var target by remember { mutableStateOf(quizItems.random()) }
    var options by remember { mutableStateOf(buildOptions(quizItems, target, keyOf, optionCount)) }
    var flash by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // key → correct?
    var showCelebrate by remember { mutableStateOf(false) }

    fun newRound() {
        val next = quizItems.filter { keyOf(it) != keyOf(target) }.randomOrNull() ?: target
        target = next
        options = buildOptions(quizItems, next, keyOf, optionCount)
        // Queue (don't flush) so the "Yes!" praise finishes before the next prompt is spoken.
        feedback?.speaker?.speak(speakFor(next), flush = false)
    }

    // Mascot mood reacts to the latest answer.
    val mascotMood = when (flash?.second) {
        true -> CurvyMood.Cheer
        false -> CurvyMood.Wow
        null -> CurvyMood.Happy
    }

    // Speak the prompt whenever the Play tab is shown.
    LaunchedEffect(mode) {
        if (mode == 1) feedback?.speaker?.speak(speakFor(target))
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
            SegmentedTabs(
                tabs = listOf("Learn", "Play"),
                selected = mode,
                accent = topic.color,
                onSelect = { mode = it },
            )
            Spacer(Modifier.height(16.dp))

            if (mode == 0) {
                learnContent()
            } else {
                // ── Prompt card ────────────────────────────────────────────
                CardSurface(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(22.dp, 18.dp, 22.dp, 18.dp),
                    ) {
                        CurvyMascot(size = 52.dp, mood = mascotMood)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = quizPromptLabel,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = InkSoft,
                        )
                        Spacer(Modifier.height(8.dp))
                        quizPrompt(target)
                        Spacer(Modifier.height(12.dp))
                        Chip(
                            onClick = { feedback?.speaker?.speak(speakFor(target)) },
                            modifier = Modifier.size(48.dp),
                        ) {
                            Text("🔊", fontSize = 20.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Option grid (2 columns) ────────────────────────────────
                val rows = options.chunked(2)
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    rows.forEach { pair ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            pair.forEach { item ->
                                val key = keyOf(item)
                                val isFlash = flash?.first == key
                                val flashOk = isFlash && flash?.second == true
                                val flashBad = isFlash && flash?.second == false
                                Surface(
                                    onClick = {
                                        if (flash != null) return@Surface
                                        if (keyOf(item) == keyOf(target)) {
                                            flash = key to true
                                            score += 1
                                            onReward(rewardPerCorrect)
                                            feedback?.correct()
                                            feedback?.speaker?.speak("Yes!")
                                        } else {
                                            flash = key to false
                                            feedback?.wrong()
                                            feedback?.speaker?.speak("Try again")
                                        }
                                    },
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
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        quizOption(item)
                                        if (flashOk) {
                                            Text("✓", fontSize = 34.sp, color = Green, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                            if (pair.size < 2) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Advance / celebrate after a correct flash.
        LaunchedEffect(flash) {
            if (flash != null) {
                delay(700)
                if (flash?.second == true) {
                    if (score > 0 && score % celebrateEvery == 0) {
                        showCelebrate = true
                    } else {
                        newRound()
                    }
                }
                flash = null
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = celebrateTitle,
                sub = "$score correct!",
                stars = 3,
                onDone = { showCelebrate = false; newRound() },
            )
        }
    }
}

/** Builds [optionCount] options — the [target] plus distinct distractors — in shuffled order. */
private fun <T> buildOptions(items: List<T>, target: T, keyOf: (T) -> String, optionCount: Int): List<T> {
    val distractors = items
        .filter { keyOf(it) != keyOf(target) }
        .shuffled()
        .take(optionCount - 1)
    return (distractors + target).shuffled()
}
