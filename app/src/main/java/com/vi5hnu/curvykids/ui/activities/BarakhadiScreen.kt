package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.saveable.rememberSaveable
import com.vi5hnu.curvykids.ui.trace.components.WritePad
import androidx.activity.compose.BackHandler
import com.vi5hnu.curvykids.recognition.Stroke
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.HindiVoiceScript
import com.vi5hnu.curvykids.data.content.VoiceLine
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.BgTop
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft
import com.vi5hnu.curvykids.ui.components.KidIconImage
import com.vi5hnu.curvykids.ui.components.StarGold
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.LetterBadge
import com.vi5hnu.curvykids.data.content.barakhadiSvg
import com.vi5hnu.curvykids.data.content.barakhadiPalette
import com.vi5hnu.curvykids.ui.components.SvgBadge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Higher pitch so the recite voice sounds like an eager young student reading aloud. */
private const val RECITE_PITCH = 1.35f
/** Gap between forms while reciting — slow, steady, sing-song like a class chanting the barakhadi. */
private const val RECITE_GAP_MS = 950L

/**
 * Hindi Barakhadi (बारहखड़ी) — a learn reference: pick a consonant and see/hear its 12 vowel-matra
 * forms (क का कि की …). Tap any form to hear it, or tap "Recite" to auto-play all twelve in order,
 * read aloud like a young student in class while each form lights up.
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
    var selected by remember { mutableIntStateOf(0) }
    // Which of the twelve forms is open for writing, or -1 for none. Tapping a form opens it:
    // a mode switch would have to be read to be used, and the child this is for cannot read it.
    var writingForm by rememberSaveable { mutableIntStateOf(-1) }
    var explored by remember { mutableStateOf(setOf<String>()) }
    // Index (0..11) of the form currently being recited, or -1 when not reciting.
    var recitingIndex by remember { mutableIntStateOf(-1) }
    var reciteJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    val consonant = HINDI_CONSONANTS[selected]
    val (badgeColor, badgeTint) = barakhadiPalette(selected)

    // Plays the recording of the line if bundled; otherwise the Hindi voice, or a romanized
    // read-aloud if no Hindi voice is installed.
    fun speakHi(line: VoiceLine, romanized: String) {
        val sp = feedback?.speaker ?: return
        sp.speak(line.text, langTag = "hi-IN", romanized = romanized, clip = line.clip)
    }

    fun stopRecite() {
        reciteJob?.cancel()
        reciteJob = null
        recitingIndex = -1
        feedback?.speaker?.setPitch(1f)
        feedback?.speaker?.stop()
    }

    // Auto-play all 12 forms of the current consonant in order, highlighting each as it speaks.
    fun startRecite() {
        val sp = feedback?.speaker ?: return
        reciteJob?.cancel()
        reciteJob = scope.launch {
            sp.setPitch(RECITE_PITCH)
            try {
                BARAKHADI_MATRAS.forEachIndexed { i, matra ->
                    recitingIndex = i
                    val syllable = barakhadiSyllable(consonant.char, matra)
                    speakHi(HindiVoiceScript.syllable(consonant, matra), consonant.romanized.dropLast(1) + matra.key)
                    explored = explored + syllable
                    delay(RECITE_GAP_MS)
                }
                onReward(2) // a little reward for listening to the whole recitation
            } finally {
                recitingIndex = -1
                sp.setPitch(1f)
            }
        }
    }

    // Stop any speech/recitation when leaving the screen.
    DisposableEffect(Unit) {
        onDispose {
            reciteJob?.cancel()
            feedback?.speaker?.setPitch(1f)
            feedback?.speaker?.stop()
        }
    }

    if (writingForm >= 0) {
        val matra = BARAKHADI_MATRAS[writingForm]
        val syllable = barakhadiSyllable(consonant.char, matra)
        val romanized = consonant.romanized.dropLast(1) + matra.key
        BackHandler { writingForm = -1 }
        WriteSheet(
            syllable = syllable,
            color = badgeColor,
            tint = badgeTint,
            onCheck = readsAs?.let { read -> { strokes -> read(strokes, syllable) } },
            onResult = { correct ->
                if (correct) {
                    feedback?.correct()
                    speakHi(HindiVoiceScript.syllable(consonant, matra), romanized)
                } else {
                    feedback?.wrong()
                }
            },
            onSpeak = { speakHi(HindiVoiceScript.syllable(consonant, matra), romanized) },
            onPrev = { writingForm = (writingForm + BARAKHADI_MATRAS.size - 1) % BARAKHADI_MATRAS.size },
            onNext = { writingForm = (writingForm + 1) % BARAKHADI_MATRAS.size },
            onClose = { writingForm = -1 },
            onDrawn = {
                if (!explored.contains(syllable)) {
                    explored = explored + syllable
                    onReward(1)
                }
            },
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 24.dp),
        ) {
            ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
            Spacer(Modifier.height(12.dp))

            Text(
                "Pick a letter",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = InkSoft,
            )
            Spacer(Modifier.height(8.dp))

            // Horizontal consonant selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HINDI_CONSONANTS.forEachIndexed { i, c ->
                    val isSel = i == selected
                    Surface(
                        onClick = {
                            stopRecite() // switching letters cancels any running recitation
                            selected = i
                            speakHi(HindiVoiceScript.letter(c), c.romanized)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) topic.color else topic.tint,
                        modifier = Modifier.size(52.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                c.char,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp,
                                color = if (isSel) Color.White else topic.color,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Heading + Recite / Stop control
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${consonant.char} barakhadi",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = topic.color,
                    modifier = Modifier.weight(1f),
                )
                val reciting = recitingIndex >= 0
                Surface(
                    onClick = { if (reciting) stopRecite() else startRecite() },
                    shape = RoundedCornerShape(20.dp),
                    color = if (reciting) topic.tint else topic.color,
                ) {
                    // A picture, not an ASCII triangle: this button is how a child starts the
                    // whole row being read aloud, and the word beside it is no use to them.
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                    ) {
                        KidIconImage(
                            if (reciting) KidIcon.Cross else KidIcon.Speaker,
                            Modifier.size(18.dp),
                            if (reciting) topic.color else Color.White,
                        )
                        Text(
                            text = if (reciting) "Stop" else "Recite",
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (reciting) topic.color else Color.White,
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // 3-column grid of the 12 matra forms for the selected consonant
            BARAKHADI_MATRAS.chunked(3).forEachIndexed { rowIndex, row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                ) {
                    row.forEachIndexed { colIndex, matra ->
                        val index = rowIndex * 3 + colIndex
                        val syllable = barakhadiSyllable(consonant.char, matra)
                        val isSpeaking = index == recitingIndex
                        // The SVG carries its own framed badge; we render it on its own (no outer
                        // card). It scales up while it's being recited so kids can follow along.
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                        ) {
                            SvgBadge(
                                asset = barakhadiSvg(consonant.romanized, matra),
                                contentDescription = syllable,
                                // The barakhadi assets are a frame plus one Devanagari glyph as
                                // SVG text. If the renderer cannot resolve a font for it the card
                                // comes out empty, so fall back to drawing the syllable natively
                                // in the same colours the asset uses.
                                fallback = {
                                    LetterBadge(
                                        text = syllable,
                                        color = badgeColor,
                                        tint = badgeTint,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(if (isSpeaking) 1.10f else 1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        stopRecite()
                                        speakHi(HindiVoiceScript.syllable(consonant, matra), consonant.romanized.dropLast(1) + matra.key)
                                        writingForm = index
                                        if (!explored.contains(syllable)) {
                                            explored = explored + syllable
                                            onReward(1)
                                        }
                                    },
                            )
                            if (explored.contains(syllable)) {
                                KidIconImage(KidIcon.Star, Modifier.align(Alignment.TopEnd).padding(6.dp).size(13.dp), StarGold)
                            }
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

/**
 * One syllable, opened for writing by tapping it.
 *
 * A full sheet rather than a mode on the grid: tapping the thing you want is how the child
 * already moves around this app, and it needs no label to be understood. Arrows move along the
 * twelve forms so a child can work through them without going back to the grid.
 */
@Composable
private fun WriteSheet(
    syllable: String,
    color: Color,
    tint: Color,
    onCheck: (suspend (List<Stroke>) -> Boolean?)?,
    onResult: (Boolean) -> Unit,
    onSpeak: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit,
    onDrawn: () -> Unit,
) {
    Surface(color = BgTop, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // A cross, not an arrow: the arrows at the foot of the page step between the
                // twelve forms, and the same picture must not mean two different things.
                RoundButton(KidIcon.Cross, color, tint, onClose)
                Spacer(Modifier.width(14.dp))
                Text(
                    text = syllable,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = color,
                    modifier = Modifier.weight(1f),
                )
                RoundButton(KidIcon.Speaker, color, tint, onSpeak)
            }
            Spacer(Modifier.height(14.dp))
            WritePad(
                char = syllable,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                onDrawn = onDrawn,
                onCheck = onCheck,
                onResult = onResult,
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RoundButton(KidIcon.ArrowBack, color, tint, onPrev)
                RoundButton(KidIcon.ArrowForward, color, tint, onNext)
            }
        }
    }
}

/** A large round control, sized for a small hand. */
@Composable
private fun RoundButton(icon: KidIcon, color: Color, tint: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = tint,
        modifier = Modifier.size(60.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            KidIconImage(icon, Modifier.size(26.dp), color)
        }
    }
}
