package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.STORIES
import com.vi5hnu.curvykids.data.content.Story
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft

/** A touch higher than normal so it sounds like a young child reading — but still natural. */
private const val STORY_PITCH = 1.12f
/** Closer to normal speaking speed than the default kids' rate, so sentences flow smoothly. */
private const val STORY_RATE = 0.95f

/**
 * Hindi Stories — pick a story and have it read aloud line-by-line in Hindi (like a child reading
 * in class), with the current sentence highlighted. Tapping a line also reads just that line.
 */
@Composable
fun StoriesScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var selected by remember { mutableStateOf<Story?>(null) }

    val story = selected
    if (story == null) {
        StoryList(topic = topic, onBack = onBack, onPick = { selected = it })
    } else {
        StoryReader(
            story = story,
            onBack = { selected = null },
            onReward = onReward,
            feedback = feedback,
        )
    }
}

/** The list of available stories as tappable cards. */
@Composable
private fun StoryList(topic: Topic, onBack: () -> Unit, onPick: (Story) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 24.dp),
    ) {
        ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
        Spacer(Modifier.height(4.dp))
        Text(
            "एक कहानी चुनो · Pick a story",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = InkSoft,
        )
        Spacer(Modifier.height(16.dp))

        STORIES.forEach { story ->
            CardSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                onClick = { onPick(story) },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(14.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = story.tint,
                        modifier = Modifier.size(56.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(story.emoji, fontSize = 30.sp)
                        }
                    }
                    Spacer(Modifier.size(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            story.title,
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = story.color,
                        )
                        Text(
                            story.subtitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = InkSoft,
                        )
                    }
                    Text("▶", fontSize = 18.sp, color = story.color)
                }
            }
        }
    }
}

/** Reads a single story aloud, highlighting each line as it's spoken. */
@Composable
private fun StoryReader(
    story: Story,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback?,
) {
    val speaker = feedback?.speaker
    // Currently-spoken segment: 0..lines.lastIndex are story lines, lines.size is the moral, -1 none.
    var current by remember(story.id) { mutableIntStateOf(-1) }
    var reading by remember(story.id) { mutableStateOf(false) }

    // The whole story + moral is read as ONE flowing utterance (natural prosody, no inter-line
    // gaps). We precompute each segment's character range in that text so the engine's range
    // callbacks can light up the segment being read.
    val moralIndex = story.lines.size
    val segments = remember(story.id) { story.lines + story.moral }
    val fullText = remember(story.id) { segments.joinToString(" ") }
    val ranges = remember(story.id) {
        var idx = 0
        segments.map { seg -> val start = idx; idx += seg.length + 1; start until (start + seg.length) }
    }

    fun resetVoice() {
        speaker?.setPitch(1f)
        speaker?.setSpeechRate(PhonicsSpeaker.DEFAULT_RATE)
    }

    fun stopReading() {
        reading = false
        current = -1
        resetVoice()
        speaker?.stop()
    }

    fun startReading() {
        val sp = speaker ?: return
        sp.stop()
        // Slightly higher pitch + closer-to-natural rate = a young child reading, but smooth.
        sp.setPitch(STORY_PITCH)
        sp.setSpeechRate(STORY_RATE)
        reading = true
        current = -1
        sp.speak(fullText, langTag = "hi-IN", flush = true, utteranceId = "story")
    }

    // Highlight follows the spoken character range; clean up (and stop audio) on leave / story change.
    DisposableEffect(story.id) {
        speaker?.onUtteranceRange = { id, start, _ ->
            if (id == "story") {
                val seg = ranges.indexOfFirst { start in it }
                if (seg >= 0) current = seg
            }
        }
        speaker?.onUtteranceDone = { id ->
            current = -1
            reading = false
            resetVoice()
            if (id == "story") onReward(3) // reward for listening to a whole story
        }
        onDispose {
            speaker?.onUtteranceRange = null
            speaker?.onUtteranceDone = null
            resetVoice()
            speaker?.stop()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 24.dp),
    ) {
        ScreenHeader(title = story.title, color = story.color, onBack = { stopReading(); onBack() })
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(story.tint)
                .padding(vertical = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(story.emoji, fontSize = 72.sp)
        }
        Spacer(Modifier.height(16.dp))

        // Read / Stop control
        Surface(
            onClick = { if (reading) stopReading() else startReading() },
            shape = RoundedCornerShape(22.dp),
            color = story.color,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (reading) "■  रोको · Stop" else "▶  सुनो · Read to me",
                fontFamily = FontDisplay,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
        Spacer(Modifier.height(16.dp))

        // Story lines — the one being read lights up.
        story.lines.forEachIndexed { i, line ->
            val active = i == current
            Text(
                text = line,
                fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Medium,
                fontSize = 19.sp,
                lineHeight = 30.sp,
                color = if (active) story.color else Ink,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (active) story.tint else Color.Transparent)
                    .clickable {
                        stopReading()
                        speaker?.setPitch(STORY_PITCH)
                        speaker?.setSpeechRate(STORY_RATE)
                        current = i
                        speaker?.speak(line, langTag = "hi-IN", utteranceId = "tap-$i")
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        // Moral — lights up while it's being read; tap to hear just the moral.
        val moralActive = current == moralIndex
        CardSurface(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                stopReading()
                speaker?.setPitch(STORY_PITCH)
                speaker?.setSpeechRate(STORY_RATE)
                current = moralIndex
                speaker?.speak(story.moral, langTag = "hi-IN", utteranceId = "tap-moral")
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (moralActive) story.tint else Color.Transparent)
                    .padding(16.dp),
            ) {
                Text(
                    "सीख · Moral",
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = story.color,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    story.moral,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 26.sp,
                    color = if (moralActive) story.color else Ink,
                )
            }
        }
    }
}
