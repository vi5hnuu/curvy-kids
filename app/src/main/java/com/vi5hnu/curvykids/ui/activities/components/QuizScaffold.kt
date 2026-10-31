package com.vi5hnu.curvykids.ui.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ProgressTrack
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.components.SessionComplete
import com.vi5hnu.curvykids.ui.components.StarCount
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * The layout and flow every "pick the right answer" quiz shares.
 *
 * The screen supplies the question: the [prompt] picture, the [choices] and which one is
 * [correct], how to draw a choice, and what to say. This handles everything else the same way
 * on every screen:
 * - the question is said aloud when it appears, with a big button to hear it again;
 * - answer tiles share out the rest of the screen, so the art is as big as it can be;
 * - wrong taps, revealing the answer, celebrations and the end of the session follow
 *   [QuizSession];
 * - progress is shown as beads rather than numbers.
 *
 * On a short window (a phone on its side) the screen scrolls and the tiles get a fixed height.
 *
 * @param belowHeader Optional row under the header, e.g. the Learn / Play tabs of a topic.
 */
@Composable
fun <T> QuizScaffold(
    topic: Topic,
    session: QuizSession,
    onBack: () -> Unit,
    choices: List<T>,
    correct: T,
    keyOf: (T) -> String,
    nameOf: (T) -> String,
    speakPrompt: (flush: Boolean) -> Unit,
    celebrateTitle: String,
    prompt: @Composable ColumnScope.() -> Unit,
    choiceContent: @Composable BoxScope.(T) -> Unit,
    promptLabel: String? = null,
    praiseFor: (T) -> String = { "Yes! ${nameOf(it)}!" },
    columns: Int = if (choices.size <= 3) choices.size else 2,
    belowHeader: (@Composable () -> Unit)? = null,
) {
    // The first question cuts in; later ones wait for the praise before them to finish.
    val firstRound = remember { session.round }
    LaunchedEffect(session.round) { speakPrompt(session.round == firstRound) }
    LaunchedEffect(session.pendingKey) { session.settle() }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < COMPACT_HEIGHT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            ScreenHeader(
                title = topic.title,
                color = topic.color,
                onBack = onBack,
                trailing = {
                    Pill {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        ) {
                            ProgressTrack(
                                current = session.progress.asked,
                                total = session.rules.length,
                                color = topic.color,
                                modifier = Modifier.width(64.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            StarCount(session.progress.correct)
                        }
                    }
                },
            )
            Spacer(Modifier.height(14.dp))
            if (belowHeader != null) {
                belowHeader()
                Spacer(Modifier.height(14.dp))
            }

            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    if (promptLabel != null) {
                        Text(
                            promptLabel,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = InkSoft,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    prompt()
                    Spacer(Modifier.height(10.dp))
                    Chip(onClick = { speakPrompt(true) }, modifier = Modifier.size(60.dp)) {
                        KidIconImage(KidIcon.Speaker, Modifier.size(28.dp), contentDescription = "Hear it again")
                    }
                }
            }
            Spacer(Modifier.height(18.dp))

            val grid = if (compact) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().weight(1f)
            ChoiceGrid(choices, columns, compact, grid) { choice ->
                ChoiceTile(
                    state = session.stateOf(keyOf(choice)),
                    onClick = {
                        session.answer(keyOf(choice), keyOf(correct), praiseFor(choice), nameOf(correct))
                    },
                    contentDescription = nameOf(choice),
                    modifier = Modifier.fillMaxSize(),
                ) { choiceContent(choice) }
            }
        }

        if (session.celebrating) {
            Celebrate(title = celebrateTitle, stars = 3, onDone = session::endCelebration)
        }
        if (session.finished) {
            SessionComplete(
                stars = session.progress.stars(session.rules),
                color = topic.color,
                onPlayAgain = session::playAgain,
                onHome = onBack,
                onShown = { session.feedback?.speaker?.speak("All done! Great job!", flush = false) },
            )
        }
    }
}

/** Lays the tiles out in rows that share the given height evenly, up to a comfortable maximum. */
@Composable
private fun <T> ChoiceGrid(
    choices: List<T>,
    columns: Int,
    compact: Boolean,
    modifier: Modifier,
    tile: @Composable (T) -> Unit,
) {
    val rows = choices.chunked(columns.coerceAtLeast(1))
    BoxWithConstraints(modifier) {
        val rowHeight: Dp = if (compact) COMPACT_ROW else
            ((maxHeight - GAP * (rows.size - 1)) / rows.size).coerceIn(MIN_ROW, MAX_ROW)
        Column(verticalArrangement = Arrangement.spacedBy(GAP)) {
            rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GAP),
                    modifier = Modifier.fillMaxWidth().height(rowHeight),
                ) {
                    row.forEach { choice -> Box(Modifier.weight(1f)) { tile(choice) } }
                    // Keep a short last row's tiles the same width as the rest.
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

private val GAP = 14.dp
private val MIN_ROW = 88.dp
private val MAX_ROW = 210.dp
private val COMPACT_ROW = 120.dp
private val COMPACT_HEIGHT = 560.dp
