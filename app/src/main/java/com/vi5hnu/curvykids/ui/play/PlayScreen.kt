package com.vi5hnu.curvykids.ui.play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.data.content.Script
import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.TopicKind
import com.vi5hnu.curvykids.ui.components.TopicCard
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.Ink
import com.vi5hnu.curvykids.ui.theme.InkSoft

/**
 * Play tab — all 11 activities grouped into Writing / Discover / Games sections.
 */
@Composable
fun PlayScreen(
    onOpenTopic: (Topic) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Writing = Latin tracing; Hindi tracing (Devanagari) gets its own group below.
    val writing  = TOPICS.filter { it.kind == TopicKind.TRACE && it.set?.script != Script.DEVANAGARI }
    val hindi    = TOPICS.filter {
        (it.kind == TopicKind.TRACE && it.set?.script == Script.DEVANAGARI) ||
            it.kind == TopicKind.BARAKHADI || it.kind == TopicKind.STORIES
    }
    val discover = TOPICS.filter { it.kind in listOf(TopicKind.SHAPES, TopicKind.COLORS, TopicKind.ANIMALS, TopicKind.BODY, TopicKind.DAYS) }
    val games    = TOPICS.filter { it.kind in listOf(TopicKind.COUNT, TopicKind.WORDS, TopicKind.DRAW) }
    val brain    = TOPICS.filter { it.kind in listOf(TopicKind.MEMORY, TopicKind.BIGSMALL, TopicKind.ODDONE, TopicKind.CATCH) }
    val world    = TOPICS.filter { it.kind in listOf(TopicKind.MONTHS, TopicKind.SEASONS, TopicKind.EMOTIONS, TopicKind.VEHICLES, TopicKind.FRUITSVEGGIES) }
    val math     = TOPICS.filter { it.kind in listOf(TopicKind.ADDITION, TopicKind.SUBTRACTION, TopicKind.PATTERNS, TopicKind.TIME, TopicKind.FRACTIONS) }
    val language = TOPICS.filter { it.kind in listOf(TopicKind.PHONICS, TopicKind.OPPOSITES, TopicKind.RHYMING) }
    // Picture topics, grouped by theme (content lives in PICTURE_SPECS).
    val nature   = TOPICS.filter { it.id in listOf("birds", "sea", "insects", "nature", "weather") }
    val things   = TOPICS.filter { it.id in listOf("food", "clothes", "toys", "instruments", "home") }
    val community = TOPICS.filter { it.id in listOf("jobs", "places", "sports") }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .padding(bottom = 90.dp),
    ) {
        Text(
            text = "All Activities",
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = Ink,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Choose anything you like!",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = InkSoft,
        )
        Spacer(Modifier.height(18.dp))

        ActivityGroup(title = "Writing",     topics = writing,  onOpen = onOpenTopic)
        ActivityGroup(title = "Hindi",       topics = hindi,    onOpen = onOpenTopic)
        ActivityGroup(title = "Discover",    topics = discover, onOpen = onOpenTopic)
        ActivityGroup(title = "Games",       topics = games,    onOpen = onOpenTopic)
        ActivityGroup(title = "Brain Games", topics = brain,    onOpen = onOpenTopic)
        ActivityGroup(title = "World",       topics = world,    onOpen = onOpenTopic)
        ActivityGroup(title = "Math",        topics = math,     onOpen = onOpenTopic)
        ActivityGroup(title = "Language",    topics = language, onOpen = onOpenTopic)
        ActivityGroup(title = "Nature & Animals", topics = nature,    onOpen = onOpenTopic)
        ActivityGroup(title = "Things",      topics = things,    onOpen = onOpenTopic)
        ActivityGroup(title = "Community",   topics = community, onOpen = onOpenTopic)
    }
}

@Composable
private fun ActivityGroup(
    title: String,
    topics: List<Topic>,
    onOpen: (Topic) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 22.dp)) {
        Text(
            text = title,
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 17.sp,
            color = InkSoft,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 10.dp),
        )
        val rows = topics.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rows.forEach { pair ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TopicCard(
                        topic = pair[0],
                        onClick = { onOpen(pair[0]) },
                        modifier = Modifier.weight(1f),
                    )
                    if (pair.size > 1) {
                        TopicCard(
                            topic = pair[1],
                            onClick = { onOpen(pair[1]) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
