package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HindiLetter
import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.Matra
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.VoiceLine
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import com.vi5hnu.curvykids.data.content.letterExample
import com.vi5hnu.curvykids.recognition.Stroke
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.components.SvgArt
import com.vi5hnu.curvykids.ui.kit.CandyCard
import com.vi5hnu.curvykids.ui.kit.MarkedWord
import com.vi5hnu.curvykids.ui.kit.Responsive
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidDimens
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/** Higher pitch so the recite voice sounds like an eager young student reading aloud. */
private const val RECITE_PITCH = 1.35f

/** Breath between recited lines, after each one has finished. */
private const val RECITE_PAUSE_MS = 350L

/** Pace used when nothing will report a line finishing (sound off). */
private const val RECITE_SILENT_GAP_MS = 950L

/** Longest a recited line may take before moving on anyway, so a lost callback can't stall it. */
private const val RECITE_LINE_TIMEOUT_MS = 9_000L

private const val RECITE_ID = "barakhadi-recite-"

/**
 * Hindi Barakhadi (बारहखड़ी): pick a consonant and learn its twelve vowel-sign forms.
 *
 * Each form explains itself the way its recording does ([SyllableExplainer]): how it is built
 * (क + आ = का) and the word it starts ("का से कान"). "Recite" reads all twelve like a class
 * chanting, waiting for each line to finish. The pencil opens a [WriteSheet] for the form.
 *
 * Phone portrait shows the grid with the explainer in a bottom sheet; landscape and tablets show
 * the grid and the explainer side by side. State and speech live here; the parts only draw.
 */
@Composable
fun BarakhadiScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
    /** Reads a child's writing and says whether it is the syllable; null when nothing can. */
    readsAs: (suspend (List<Stroke>, String) -> Boolean?)? = null,
) {
    var selected by rememberSaveable { mutableIntStateOf(0) }
    // The form being explained, or -1. On a phone, -1 also means the sheet is closed.
    var active by rememberSaveable { mutableIntStateOf(-1) }
    // The form open for writing, or -1.
    var writingForm by rememberSaveable { mutableIntStateOf(-1) }
    var explored by remember { mutableStateOf(setOf<String>()) }
    var recitingIndex by remember { mutableIntStateOf(-1) }
    var reciteJob by remember { mutableStateOf<Job?>(null) }
    // The line Recite is waiting on, completed when the speaker reports it done.
    val awaiting = remember { mutableStateOf<Pair<String, CompletableDeferred<Unit>>?>(null) }
    val scope = rememberCoroutineScope()
    val speaker = feedback?.speaker
    val consonant = HINDI_CONSONANTS[selected]

    // Plays the recording of the line if bundled; otherwise the Hindi voice, or a romanized
    // read-aloud if no Hindi voice is installed.
    fun speakHi(line: VoiceLine, romanized: String, id: String = line.text) {
        speaker?.speak(line.text, langTag = "hi-IN", romanized = romanized, clip = line.clip, utteranceId = id)
    }

    fun speakForm(matra: Matra, id: String? = null) {
        val line = HindiVoiceScript.syllable(consonant, matra)
        speakHi(line, consonant.romanized.dropLast(1) + matra.key, id ?: line.text)
    }

    fun markHeard(matra: Matra, reward: Boolean) {
        val syllable = barakhadiSyllable(consonant.char, matra)
        if (syllable !in explored) {
            explored = explored + syllable
            if (reward) onReward(1)
        }
    }

    fun stopRecite() {
        reciteJob?.cancel()
        reciteJob = null
        recitingIndex = -1
        speaker?.setPitch(1f)
        speaker?.stop()
    }

    // Reads all twelve forms in order, each starting once the previous line has finished: the
    // lines are whole sentences now ("का, का से कान"), so a fixed gap would talk over itself.
    fun startRecite() {
        val sp = speaker ?: return
        reciteJob?.cancel()
        reciteJob = scope.launch {
            sp.setPitch(RECITE_PITCH)
            try {
                BARAKHADI_MATRAS.forEachIndexed { i, matra ->
                    recitingIndex = i
                    val id = "$RECITE_ID$i"
                    val done = CompletableDeferred<Unit>()
                    awaiting.value = id to done
                    speakForm(matra, id)
                    markHeard(matra, reward = false)
                    if (sp.muted) delay(RECITE_SILENT_GAP_MS)
                    else withTimeoutOrNull(RECITE_LINE_TIMEOUT_MS) { done.await() }
                    delay(RECITE_PAUSE_MS)
                }
                onReward(2) // a little reward for listening to the whole recitation
            } finally {
                awaiting.value = null
                recitingIndex = -1
                sp.setPitch(1f)
            }
        }
    }

    DisposableEffect(speaker) {
        speaker?.onUtteranceDone = { id ->
            awaiting.value?.let { (waitingId, done) -> if (id == waitingId) done.complete(Unit) }
        }
        onDispose {
            reciteJob?.cancel()
            speaker?.onUtteranceDone = null
            speaker?.setPitch(1f)
            speaker?.stop()
        }
    }

    fun openForm(i: Int) {
        stopRecite()
        active = i
        speakForm(BARAKHADI_MATRAS[i])
        markHeard(BARAKHADI_MATRAS[i], reward = true)
    }

    if (writingForm >= 0) {
        val matra = BARAKHADI_MATRAS[writingForm]
        val syllable = barakhadiSyllable(consonant.char, matra)
        val count = BARAKHADI_MATRAS.size
        BackHandler { writingForm = -1 }
        WriteSheet(
            consonant = consonant,
            matra = matra,
            onCheck = readsAs?.let { read -> { strokes -> read(strokes, syllable) } },
            onResult = { correct ->
                if (correct) {
                    feedback?.correct()
                    speakForm(matra)
                } else {
                    feedback?.wrong()
                }
            },
            onSpeak = { speakForm(matra) },
            onPrev = { writingForm = (writingForm + count - 1) % count; active = writingForm },
            onNext = { writingForm = (writingForm + 1) % count; active = writingForm },
            onClose = { writingForm = -1 },
            onDrawn = { markHeard(matra, reward = true) },
            onTool = { word -> speaker?.say(EnglishVoiceScript.say(word)) },
        )
        return
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val twoPane = Responsive.isTwoPane(maxWidth, maxHeight)
        val gridColumns = if (Responsive.isWide(maxWidth)) 4 else 3
        val recite: @Composable () -> Unit = {
            ReciteButton(
                reciting = recitingIndex >= 0,
                color = topic.color,
                onClick = { if (recitingIndex >= 0) stopRecite() else startRecite() },
            )
        }
        val speakLetter = { speakHi(HindiVoiceScript.letter(consonant), consonant.romanized) }
        val picker: @Composable (Modifier, Boolean) -> Unit = { modifier, vertical ->
            ConsonantPicker(
                consonants = HINDI_CONSONANTS,
                selected = selected,
                color = topic.color,
                tint = topic.tint,
                vertical = vertical,
                modifier = modifier,
                onPick = { i ->
                    stopRecite() // switching letters cancels any running recitation
                    selected = i
                    active = -1
                    HINDI_CONSONANTS[i].let { speakHi(HindiVoiceScript.letter(it), it.romanized) }
                },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = KidDimens.ScreenPadding, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (twoPane) {
                // Height is what's short here, so the letters run down the side, the letter's own
                // word moves up beside the title, and all twelve forms fit without scrolling.
                ScreenHeader(title = topic.title, color = topic.color, onBack = onBack) {
                    LetterIntro(consonant, topic.color, speakLetter, Modifier.widthIn(max = 380.dp))
                    Spacer(Modifier.width(KidDimens.Gap))
                    recite()
                }
                // While reciting, the explainer follows the voice so the word shows as it's said.
                val shown = if (recitingIndex >= 0) recitingIndex else active.coerceAtLeast(0)
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(KidDimens.Gap)) {
                    picker(Modifier.width(KidDimens.MinTarget), true)
                    BoxWithConstraints(Modifier.weight(1.2f).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                        // Three rows of four square cards, as large as the height allows.
                        val gap = 12.dp
                        val card = minOf((maxWidth - gap * 3) / 4, (maxHeight - gap * 2 - KidDimens.CandyEdge) / 3)
                        SyllableGrid(
                            consonant, explored, shown, recitingIndex, columns = 4, onTap = ::openForm,
                            modifier = Modifier.width(card * 4 + gap * 3),
                        )
                    }
                    SyllableExplainer(
                        consonant = consonant,
                        matra = BARAKHADI_MATRAS[shown],
                        onSpeak = { openForm(shown) },
                        onWrite = { stopRecite(); writingForm = shown },
                        compact = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(KidDimens.CardRadius))
                            .background(Color.White)
                            .verticalScroll(rememberScrollState())
                            .padding(KidDimens.ScreenPadding),
                    )
                }
            } else {
                ScreenHeader(title = topic.title, color = topic.color, onBack = onBack, trailing = recite)
                picker(Modifier, false)
                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LetterIntro(consonant, topic.color, speakLetter, Modifier.fillMaxWidth())
                    SyllableGrid(consonant, explored, active, recitingIndex, columns = gridColumns, onTap = ::openForm)
                }
            }
        }

        if (!twoPane && active >= 0) {
            ExplainerSheet(
                consonant = consonant,
                matra = BARAKHADI_MATRAS[active],
                onSpeak = { speakForm(BARAKHADI_MATRAS[active]) },
                onWrite = { writingForm = active },
                onDismiss = { active = -1 },
            )
        }
    }
}

/** The phone's explainer: slides up over the grid, swipe or tap outside to close. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExplainerSheet(
    consonant: HindiLetter,
    matra: Matra,
    onSpeak: () -> Unit,
    onWrite: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
    ) {
        SyllableExplainer(
            consonant = consonant,
            matra = matra,
            onSpeak = onSpeak,
            onWrite = onWrite,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = KidDimens.ScreenPadding, end = KidDimens.ScreenPadding, bottom = 24.dp),
        )
    }
}

/**
 * The picked consonant with its own word, "क से कबूतर" with its picture; tapping says it.
 * Ties the twelve forms below back to the letter the child already knows.
 */
@Composable
private fun LetterIntro(consonant: HindiLetter, color: Color, onSpeak: () -> Unit, modifier: Modifier = Modifier) {
    val example = letterExample(consonant.char)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(KidDimens.CardRadius))
            .background(Color.White.copy(alpha = 0.85f))
            .clickable(onClick = onSpeak)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(consonant.char, fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, color = color)
        if (example != null) {
            SvgArt(example.picture, Modifier.size(64.dp), contentDescription = example.word)
            MarkedWord(example.word, example.mark, color, fontSize = 30.sp, modifier = Modifier.weight(1f, fill = false))
        }
        KidIconImage(KidIcon.Speaker, Modifier.size(28.dp), color)
    }
}

/** Starts or stops reading all twelve forms aloud; a picture, since the child can't read "Recite". */
@Composable
private fun ReciteButton(reciting: Boolean, color: Color, onClick: () -> Unit) {
    CandyCard(
        color = color,
        onClick = onClick,
        label = if (reciting) "Stop" else "Recite",
        radius = 24.dp,
        modifier = Modifier.size(width = 132.dp, height = KidDimens.HeaderButton),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KidIconImage(if (reciting) KidIcon.Cross else KidIcon.Speaker, Modifier.size(24.dp), Color.White)
            Text(
                text = if (reciting) "Stop" else "Recite",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = KidDimens.MinText,
                color = Color.White,
            )
        }
    }
}
