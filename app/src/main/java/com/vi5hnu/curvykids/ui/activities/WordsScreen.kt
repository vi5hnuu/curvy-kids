package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.vi5hnu.curvykids.audio.PhonicsSpeaker
import com.vi5hnu.curvykids.data.content.WORD_LIST
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.components.CandyButton
import com.vi5hnu.curvykids.ui.components.CardSurface
import com.vi5hnu.curvykids.ui.components.Celebrate
import com.vi5hnu.curvykids.ui.components.Chip
import com.vi5hnu.curvykids.ui.components.Pill
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import kotlinx.coroutines.delay

private val EXTRA_LETTERS = "ABCDEFGHIJKLMNOPRSTUVWY".toList()

/** First Words — tap letters in the correct order to spell the word. */
@Composable
fun WordsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker? = null,
) {
    // Shuffle the word order once so every session feels fresh but each word still appears once.
    val order = remember { WORD_LIST.indices.shuffled() }
    var pos by remember { mutableIntStateOf(0) }
    var filled by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableStateOf<Char?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    val word = WORD_LIST[order[pos]]
    val letters = word.word.toList()

    val shuffled = remember(pos) {
        val extras = EXTRA_LETTERS.filter { it !in letters }.shuffled().take(2)
        (letters + extras).shuffled()
    }

    // Tile/slot sizing shrinks for longer words so 6+ tiles still fit.
    val tileSize = when {
        letters.size <= 3 -> 58.dp
        letters.size == 4 -> 52.dp
        else -> 44.dp
    }
    val tileFont = when {
        letters.size <= 3 -> 32.sp
        letters.size == 4 -> 28.sp
        else -> 24.sp
    }

    LaunchedEffect(pos) {
        filled = 0
        wrong = null   // clear any red-tile state from the previous word
        speaker?.speak(word.word)
    }

    Box {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp)
                .padding(bottom = 120.dp),
        ) {
            ScreenHeader(
                title = "First Words",
                color = topic.color,
                onBack = onBack,
                trailing = {
                    Pill {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        ) {
                            Text("${pos + 1}/${WORD_LIST.size}", fontFamily = FontDisplay, fontSize = 16.sp, color = topic.color)
                        }
                    }
                },
            )
            Spacer(Modifier.height(16.dp))

            // Emoji + listen card
            CardSurface(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(22.dp),
                ) {
                    Text(word.emoji, fontSize = 72.sp, lineHeight = 80.sp)
                    Spacer(Modifier.height(10.dp))
                    Chip(
                        onClick = { speaker?.speak(word.word) },
                        modifier = Modifier.size(46.dp),
                        containerColor = topic.tint,
                        contentColor = topic.color,
                    ) {
                        Text("🔊", fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Letter slots
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                letters.forEachIndexed { i, ch ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (i < filled) topic.color else Color.White,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .size(tileSize, tileSize + 10.dp)
                            .then(
                                if (i == filled) Modifier.border(3.dp, topic.color, RoundedCornerShape(16.dp))
                                else Modifier
                            ),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (i < filled) {
                                Text(
                                    text = ch.toString(),
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = tileFont,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Letter tiles
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                shuffled.forEach { ch ->
                    CandyButton(
                        onClick = {
                            // Guard: ignore taps after word is complete or celebrate is showing
                            if (filled >= letters.size || showCelebrate) return@CandyButton
                            if (ch == letters[filled]) {
                                val nf = filled + 1
                                filled = nf
                                speaker?.speak(ch.toString())
                                if (nf == letters.size) {
                                    onReward(5)
                                    showCelebrate = true
                                }
                            } else {
                                wrong = ch
                                speaker?.speak("Try again")
                            }
                        },
                        modifier = Modifier.size(tileSize),
                        containerColor = if (wrong == ch) Color(0xFFFF6B6B) else Color.White,
                        contentColor = if (wrong == ch) Color.White else Color(0xFF2B3A4A),
                    ) {
                        Text(
                            text = ch.toString(),
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = tileFont,
                        )
                    }
                }
            }
        }

        LaunchedEffect(wrong) {
            if (wrong != null) {
                delay(500)
                wrong = null
            }
        }

        if (showCelebrate) {
            Celebrate(
                title = "You spelled it!",
                sub = word.word,
                stars = 3,
                onDone = {
                    showCelebrate = false
                    pos = (pos + 1) % WORD_LIST.size
                },
            )
        }
    }
}
