package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.vi5hnu.curvykids.ui.theme.InkFaint
import kotlinx.coroutines.delay

private val EXTRA_LETTERS = "ABCDEFGHIJKLMNOPRSTUWY".toList()

/** First Words — tap letters in the correct order to spell the word. */
@Composable
fun WordsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    speaker: PhonicsSpeaker? = null,
) {
    var wordIndex by remember { mutableIntStateOf(0) }
    var filled by remember { mutableIntStateOf(0) }
    var wrong by remember { mutableStateOf<Char?>(null) }
    var showCelebrate by remember { mutableStateOf(false) }

    val word = WORD_LIST[wordIndex]
    val letters = word.word.toList()

    val shuffled = remember(wordIndex) {
        val extras = EXTRA_LETTERS.filter { it !in letters }.shuffled().take(1)
        (letters + extras).shuffled()
    }

    LaunchedEffect(wordIndex) {
        filled = 0
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
                            Text("${wordIndex + 1}/${WORD_LIST.size}", fontFamily = FontDisplay, fontSize = 16.sp, color = topic.color)
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth(),
            ) {
                letters.forEachIndexed { i, ch ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (i < filled) topic.color else Color.White,
                        shadowElevation = 3.dp,
                        modifier = Modifier
                            .size(62.dp, 72.dp)
                            .then(
                                if (i == filled) Modifier.border(3.dp, topic.color, RoundedCornerShape(18.dp))
                                else Modifier
                            ),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (i < filled) {
                                Text(
                                    text = ch.toString(),
                                    fontFamily = FontDisplay,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 40.sp,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Letter tiles
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth(),
            ) {
                shuffled.forEach { ch ->
                    CandyButton(
                        onClick = {
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
                        modifier = Modifier.size(62.dp),
                        containerColor = if (wrong == ch) Color(0xFFFF6B6B) else Color.White,
                        contentColor = if (wrong == ch) Color.White else Color(0xFF2B3A4A),
                    ) {
                        Text(
                            text = ch.toString(),
                            fontFamily = FontDisplay,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
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
                    wordIndex = (wordIndex + 1) % WORD_LIST.size
                },
            )
        }
    }
}
