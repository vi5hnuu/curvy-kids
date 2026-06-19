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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.HINDI_CONSONANTS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.barakhadiSvg
import com.vi5hnu.curvykids.data.content.barakhadiSyllable
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.components.SvgBadge
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.InkSoft
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
) {
    var selected by remember { mutableIntStateOf(0) }
    var explored by remember { mutableStateOf(setOf<String>()) }
    // Index (0..11) of the form currently being recited, or -1 when not reciting.
    var recitingIndex by remember { mutableIntStateOf(-1) }
    var reciteJob by remember { mutableStateOf<Job?>(null) }
    val scope = rememberCoroutineScope()
    val consonant = HINDI_CONSONANTS[selected]

    // Speak Devanagari in the Hindi voice, or a romanized read-aloud if no Hindi voice is installed.
    fun speakHi(devanagari: String, romanized: String) {
        val sp = feedback?.speaker ?: return
        if (sp.supports("hi-IN")) sp.speak(devanagari, langTag = "hi-IN") else sp.speak(romanized)
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
                    speakHi(syllable, consonant.romanized.dropLast(1) + matra.key)
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
                            speakHi(c.char, c.romanized)
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
                    Text(
                        text = if (reciting) "■  Stop" else "▶  Recite",
                        fontFamily = FontDisplay,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = if (reciting) topic.color else Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
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
                                fallbackEmoji = syllable,
                                contentDescription = syllable,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(if (isSpeaking) 1.10f else 1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        stopRecite()
                                        speakHi(syllable, consonant.romanized.dropLast(1) + matra.key)
                                        if (!explored.contains(syllable)) {
                                            explored = explored + syllable
                                            onReward(1)
                                        }
                                    },
                            )
                            if (explored.contains(syllable)) {
                                Text("⭐", fontSize = 13.sp, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
                            }
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
