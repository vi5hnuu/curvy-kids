package com.vi5hnu.curvykids.ui.play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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

/** A named run of topics in the browser. */
private data class Group(val title: String, val topics: List<Topic>)

/**
 * Groups every topic for the Play tab. Computed once rather than on each recomposition — the
 * membership is derived from the static [TOPICS] list and never changes at runtime.
 */
private fun buildGroups(): List<Group> {
    fun ofKind(vararg kinds: TopicKind) = TOPICS.filter { it.kind in kinds }
    fun ofId(vararg ids: String) = TOPICS.filter { it.id in ids }

    return listOf(
        Group("Writing", TOPICS.filter {
            it.kind == TopicKind.TRACE && it.set?.script != Script.DEVANAGARI
        }),
        Group("Hindi", TOPICS.filter {
            (it.kind == TopicKind.TRACE && it.set?.script == Script.DEVANAGARI) ||
                it.kind == TopicKind.BARAKHADI || it.kind == TopicKind.STORIES
        }),
        Group("Discover", ofKind(TopicKind.SHAPES, TopicKind.COLORS, TopicKind.ANIMALS, TopicKind.BODY, TopicKind.DAYS)),
        Group("Games", ofKind(TopicKind.COUNT, TopicKind.WORDS, TopicKind.DRAW)),
        Group("Brain Games", ofKind(TopicKind.MEMORY, TopicKind.BIGSMALL, TopicKind.ODDONE, TopicKind.CATCH)),
        Group("World", ofKind(TopicKind.MONTHS, TopicKind.SEASONS, TopicKind.EMOTIONS, TopicKind.VEHICLES, TopicKind.FRUITSVEGGIES)),
        Group("Math", ofKind(TopicKind.ADDITION, TopicKind.SUBTRACTION, TopicKind.PATTERNS, TopicKind.TIME, TopicKind.FRACTIONS)),
        Group("Language", ofKind(TopicKind.PHONICS, TopicKind.OPPOSITES, TopicKind.RHYMING)),
        Group("Nature & Animals", ofId("birds", "sea", "insects", "nature", "weather")),
        Group("Things", ofId("food", "clothes", "toys", "instruments", "home")),
        Group("Community", ofId("jobs", "places", "sports")),
    ).filter { it.topics.isNotEmpty() }
}

/**
 * Play tab — the full catalogue, grouped into named sections.
 *
 * A lazy grid rather than a scrolling Column: there are 44 cards, each drawing its own vector
 * glyph, and composing them all up front cost a visible hitch when opening the tab.
 */
@Composable
fun PlayScreen(
    onOpenTopic: (Topic) -> Unit,
    modifier: Modifier = Modifier,
) {
    val groups = remember { buildGroups() }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
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
            }
        }

        groups.forEach { group ->
            item(key = "header-${group.title}", span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = group.title,
                    fontFamily = FontDisplay,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    color = InkSoft,
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 6.dp),
                )
            }
            items(group.topics, key = { it.id }) { topic ->
                TopicCard(topic = topic, onClick = { onOpenTopic(topic) })
            }
        }
    }
}
