package com.vi5hnu.curvykids.ui.activities.components

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.app.Difficulty
import com.vi5hnu.curvykids.ui.app.LocalDifficulty
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript
import com.vi5hnu.curvykids.data.content.Speech

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
 * @param speakFor        Spoken prompt for the current target ("Find the Circle"), from [EnglishVoiceScript].
 * @param nameOf          The item's spoken name, for praise ("Yes! Circle") and reveals; defaults to [keyOf].
 * @param learnContent    The explore-mode UI (preserves the original tap-to-learn grid).
 * @param quizPrompt      Renders the large target inside the prompt card.
 * @param quizOption      Renders one option tile's inner visual (shape/svg/day).
 */
@Composable
fun <T> DiscoverActivity(
    topic: Topic,
    onBack: () -> Unit,
    quizItems: List<T>,
    quizPromptLabel: String,
    keyOf: (T) -> String,
    speakFor: (T) -> Speech,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback?,
    learnContent: @Composable () -> Unit,
    quizPrompt: @Composable (target: T) -> Unit,
    quizOption: @Composable (item: T) -> Unit,
    rewardPerCorrect: Int = 3,
    optionCount: Int = LocalDifficulty.current.optionCount(),
    celebrateEvery: Int = 5,
    celebrateTitle: String = "Great job!",
    nameOf: (T) -> String = keyOf,
) {
    var mode by rememberSaveable { mutableIntStateOf(0) } // 0 = Learn, 1 = Play
    val tabs: @Composable () -> Unit = {
        SegmentedTabs(
            tabs = listOf("Learn", "Play"),
            icons = listOf(KidIcon.Sparkle, KidIcon.Pointer),
            selected = mode,
            accent = topic.color,
            onSelect = { mode = it },
        )
    }

    if (mode == 0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 24.dp),
        ) {
            ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
            Spacer(Modifier.height(14.dp))
            tabs()
            Spacer(Modifier.height(16.dp))
            learnContent()
        }
        return
    }

    val session = rememberQuizSession(
        feedback, onReward, rewardPerCorrect,
        rules = remember(celebrateEvery) { QuizRules(celebrateEvery = celebrateEvery) },
    )
    // Never the same target twice in a row.
    var previous by remember { mutableStateOf<T?>(null) }
    val question = remember(session.round) {
        val target = quizItems.filter { previous == null || keyOf(it) != keyOf(previous!!) }
            .randomOrNull() ?: quizItems.random()
        previous = target
        target to buildOptions(quizItems, target, keyOf, optionCount)
    }
    val (target, options) = question

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = options,
        correct = target,
        keyOf = keyOf,
        nameOf = nameOf,
        speakPrompt = { flush -> feedback?.speaker?.say(speakFor(target), flush = flush) },
        celebrateTitle = celebrateTitle,
        promptLabel = quizPromptLabel,
        prompt = { quizPrompt(target) },
        choiceContent = { quizOption(it) },
        columns = 2,
        belowHeader = tabs,
    )
}

/** Builds [optionCount] options — the [target] plus distinct distractors — in shuffled order. */
private fun <T> buildOptions(items: List<T>, target: T, keyOf: (T) -> String, optionCount: Int): List<T> {
    val distractors = items
        .filter { keyOf(it) != keyOf(target) }
        .shuffled()
        .take(optionCount - 1)
    return (distractors + target).shuffled()
}

/** Choices shown per question. Fewer options means less to scan for a younger child. */
private fun Difficulty.optionCount() = when (this) {
    Difficulty.EASY -> 3
    Difficulty.NORMAL -> 4
}
