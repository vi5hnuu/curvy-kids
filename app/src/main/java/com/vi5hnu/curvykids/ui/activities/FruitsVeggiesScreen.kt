package com.vi5hnu.curvykids.ui.activities

import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.vi5hnu.curvykids.data.content.KidAssets
import com.vi5hnu.curvykids.data.content.AssetPath
import androidx.compose.foundation.layout.size
import com.vi5hnu.curvykids.ui.activities.components.ChoiceArt
import com.vi5hnu.curvykids.ui.activities.components.QuizScaffold
import com.vi5hnu.curvykids.ui.activities.components.rememberQuizSession
import com.vi5hnu.curvykids.ui.components.SvgBadge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi5hnu.curvykids.audio.PlayFeedback
import com.vi5hnu.curvykids.data.content.FruitItem
import com.vi5hnu.curvykids.data.content.FRUITS_VEGGIES
import com.vi5hnu.curvykids.data.content.Topic
import com.vi5hnu.curvykids.ui.activities.components.SegmentedTabs
import com.vi5hnu.curvykids.ui.components.KidIcon
import com.vi5hnu.curvykids.ui.components.ScreenHeader
import com.vi5hnu.curvykids.ui.theme.FontDisplay

private val FRUIT_COLOR = Color(0xFF4FCB94)
private val VEGGIE_COLOR = Color(0xFFFF8B6B)


/**
 * Items used in the sorting quiz. Tomato and cucumber are botanically fruits but eaten as
 * vegetables; either answer can be argued, so they stay in the Learn grid and out of the quiz.
 */
private val QUIZ_ITEMS = FRUITS_VEGGIES.filterNot { it.name in setOf("Tomato", "Cucumber") }

/** Fruits & Vegetables — learn the two groups, then sort each item into the right one. */
@Composable
fun FruitsVeggiesScreen(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback? = null,
) {
    var mode by rememberSaveable { mutableIntStateOf(0) }
    val tabs: @Composable () -> Unit = {
        SegmentedTabs(tabs = listOf("Learn", "Play"), icons = listOf(KidIcon.Sparkle, KidIcon.Pointer),
            selected = mode, accent = topic.color, onSelect = { mode = it })
    }
    if (mode == 0) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
            ScreenHeader(title = topic.title, color = topic.color, onBack = onBack)
            Spacer(Modifier.height(14.dp))
            tabs()
            Spacer(Modifier.height(16.dp))
            FruitsLearnGrid(topic.color)
        }
    } else {
        FruitsQuiz(topic, onBack, onReward, feedback, tabs)
    }
}

private enum class Group(val label: String, val picture: AssetPath, val color: Color) {
    FRUIT("Fruit", KidAssets.fruit("apple"), FRUIT_COLOR),
    VEGETABLE("Vegetable", KidAssets.vegetable("carrot"), VEGGIE_COLOR),
}

@Composable
private fun FruitsQuiz(
    topic: Topic,
    onBack: () -> Unit,
    onReward: (Int) -> Unit,
    feedback: PlayFeedback?,
    tabs: @Composable () -> Unit,
) {
    val session = rememberQuizSession(feedback, onReward)
    val order = remember { QUIZ_ITEMS.shuffled() }
    val current = order[session.round % order.size]
    val answer = if (current.isFruit) Group.FRUIT else Group.VEGETABLE

    QuizScaffold(
        topic = topic,
        session = session,
        onBack = onBack,
        choices = Group.entries,
        correct = answer,
        keyOf = { it.name },
        nameOf = { "a ${it.label.lowercase()}" },
        praiseFor = { "Yes! ${current.name} is a ${it.label.lowercase()}!" },
        speakPrompt = { flush ->
            feedback?.speaker?.speak("Is ${current.name} a fruit or a vegetable?", flush = flush)
        },
        celebrateTitle = "Super Sorter!",
        promptLabel = "FRUIT OR VEGETABLE?",
        prompt = {
            SvgBadge(current.svg, color = topic.color, contentDescription = current.name, modifier = Modifier.size(120.dp))
            Text(current.name, fontFamily = FontDisplay, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp,
                color = topic.color)
        },
        choiceContent = { ChoiceArt(it.picture, it.label, it.color) },
        columns = 2,
        belowHeader = tabs,
    )
}

@Composable
private fun FruitsLearnGrid(color: Color) {
    val fruits = FRUITS_VEGGIES.filter { it.isFruit }
    val veggies = FRUITS_VEGGIES.filter { !it.isFruit }

    @Composable
    fun CategoryRow(title: String, items: List<FruitItem>, accent: Color) {
        Text(
            title,
            fontFamily = FontDisplay,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            color = accent,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        items.chunked(4).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
            ) {
                row.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                    ) {
                        // SVG badge carries the item name already; rendered on its own (no card).
                        SvgBadge(color = color, 
                            asset = item.svg,
                            contentDescription = item.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                        )
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }

    CategoryRow("Fruits", fruits, FRUIT_COLOR)
    Spacer(Modifier.height(4.dp))
    CategoryRow("Vegetables", veggies, VEGGIE_COLOR)
}

