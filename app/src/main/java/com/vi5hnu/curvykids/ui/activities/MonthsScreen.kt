package com.vi5hnu.curvykids.ui.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.Month
import com.vi5hnu.curvykids.data.content.MONTHS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.DiscoverActivity
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.theme.FontDisplay

/** Months of the Year — learn and quiz on all 12 months in order. */
@Composable
fun MonthsScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    DiscoverActivity(
        topic = topic,
        onBack = onBack,
        quizItems = MONTHS,
        quizPromptLabel = "WHAT COMES AFTER…",
        keyOf = { it.name },
        speakFor = { m ->
            val idx = MONTHS.indexOf(m)
            val prev = MONTHS[(idx - 1 + 12) % 12]
            "What comes after ${prev.name}?"
        },
        onReward = onReward,
        feedback = feedback,
        celebrateTitle = "Month Master!",
        learnContent = { MonthsLearnGrid() },
        quizPrompt = { month ->
            // Show the month BEFORE the target so the child picks the correct next month
            val idx = MONTHS.indexOf(month)
            val prev = MONTHS[(idx - 1 + 12) % 12]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // SVG badge carries the month name already.
                SvgImage(
                    asset = prev.svg,
                    fallbackEmoji = prev.emoji,
                    fallbackSize = 44.sp,
                    contentDescription = prev.name,
                    modifier = Modifier.size(96.dp),
                )
                Spacer(Modifier.height(4.dp))
                Text("What comes next?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF758999))
            }
        },
        quizOption = { month ->
            SvgImage(
                asset = month.svg,
                fallbackEmoji = month.emoji,
                fallbackSize = 32.sp,
                contentDescription = month.name,
                modifier = Modifier.size(74.dp),
            )
        },
    )
}

@Composable
private fun MonthsLearnGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MONTHS.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { month ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(month.color.copy(alpha = 0.12f)),
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(6.dp)) {
                            // SVG badge carries the month name already.
                            SvgImage(
                                asset = month.svg,
                                fallbackEmoji = month.emoji,
                                fallbackSize = 24.sp,
                                contentDescription = month.name,
                                modifier = Modifier.size(64.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
