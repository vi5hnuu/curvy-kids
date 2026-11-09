package com.vi5hnu.curvykids.ui.world

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import com.vi5hnu.curvykids.ui.kit.Responsive
import com.vi5hnu.curvykids.ui.kit.kidContentWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.data.content.World
import com.vi5hnu.curvykids.data.content.art
import com.vi5hnu.curvykids.ui.components.CurvyMascot
import com.vi5hnu.curvykids.ui.components.CurvyMood
import com.vi5hnu.curvykids.ui.kit.CandyCard
import com.vi5hnu.curvykids.ui.kit.KidBackButton
import com.vi5hnu.curvykids.ui.kit.SceneBackground
import com.vi5hnu.curvykids.ui.kit.TopicArtView
import com.vi5hnu.curvykids.ui.theme.FontDisplay
import com.vi5hnu.curvykids.ui.theme.KidDimens
import com.vi5hnu.curvykids.ui.theme.KidInk
import com.vi5hnu.curvykids.billing.ContentLocks
import com.vi5hnu.curvykids.billing.Lockable
import com.vi5hnu.curvykids.ui.pro.LockBadge
import androidx.compose.ui.draw.alpha

/**
 * One world's activities as big picture tiles, two per row. The world's name is said on arrival
 * and each tile says its own name as it opens (see the caller), so no reading is needed.
 * Activities that are part of Pro carry a padlock; the caller decides what a tap on one does.
 */
@Composable
fun WorldScreen(
    world: World,
    onBack: () -> Unit,
    onOpenTopic: (Topic) -> Unit,
    onSpeak: (String) -> Unit,
    locks: ContentLocks = ContentLocks.NONE,
) {
    LaunchedEffect(world) { onSpeak(world.title) }

    SceneBackground(tint = world.color) {
        BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        // Bigger tiles on tablets rather than many small ones.
        val minTile = if (Responsive.isWide(maxWidth)) 210.dp else 150.dp
        Column(Modifier.kidContentWidth().fillMaxHeight()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(KidDimens.ScreenPadding),
            ) {
                KidBackButton(onClick = onBack, color = world.color)
                Spacer(Modifier.width(14.dp))
                Text(world.title, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                    fontSize = KidDimens.ScreenTitle, color = KidInk, modifier = Modifier.weight(1f))
                CurvyMascot(size = 64.dp, mood = CurvyMood.Wow, floating = true)
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = minTile),
                contentPadding = PaddingValues(start = KidDimens.ScreenPadding, end = KidDimens.ScreenPadding, bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(KidDimens.Gap),
                verticalArrangement = Arrangement.spacedBy(KidDimens.Gap),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(world.topics, key = { it.id }) { topic ->
                    ActivityTile(topic, world.color, locked = locks.isLocked(Lockable.Activity(topic.id))) {
                        onOpenTopic(topic)
                    }
                }
            }
        }
        }
    }
}

/** A white card with the activity's picture and name, edged in the world's colour. */
@Composable
private fun ActivityTile(topic: Topic, color: Color, locked: Boolean, onClick: () -> Unit) {
    CandyCard(color = Color.White, onClick = onClick, label = topic.title,
        modifier = Modifier.fillMaxWidth().aspectRatio(0.9f)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(12.dp),
        ) {
            Box(
                Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(22.dp))
                    .background(color.copy(alpha = 0.16f)).padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                TopicArtView(topic.art(), color, Modifier.fillMaxSize().alpha(if (locked) 0.55f else 1f))
                if (locked) LockBadge(Modifier.align(Alignment.TopEnd))
            }
            Spacer(Modifier.height(8.dp))
            Text(topic.title, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold,
                fontSize = KidDimens.MinText * 1.1f, color = KidInk, maxLines = 1,
                overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
    }
}
