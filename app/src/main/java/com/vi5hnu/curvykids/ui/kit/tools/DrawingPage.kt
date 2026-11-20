package com.vi5hnu.curvykids.ui.kit.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.data.content.CRAYONS
import com.vi5hnu.curvykids.data.content.Crayon

/**
 * The tools beside a writing page: eraser (rubs out the last line), new page (wipes it all), the
 * crayon box when the page offers colours, and any extra tool ([trailing], e.g. the tick that
 * checks the writing).
 *
 * They sit along the top on a tall page, and down the left on a wide one (landscape), where a
 * top strip would squeeze the letter. Never along the bottom: young children rest their wrist
 * on the bottom edge of a tablet and press whatever is there.
 *
 * @param crayon the crayon in use, or null to leave the crayon box out (the ink is fixed).
 */
@Composable
fun DrawingPage(
    onErase: () -> Unit,
    onNewPage: () -> Unit,
    modifier: Modifier = Modifier,
    crayon: Crayon? = null,
    onCrayon: (Crayon) -> Unit = {},
    crayons: List<Crayon> = CRAYONS,
    trailing: @Composable () -> Unit = {},
    page: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints(modifier) {
        val sideRail = maxWidth > maxHeight * 1.25f
        val tools: @Composable () -> Unit = {
            ToolButton(ToolArt.Eraser, "Eraser: rub out the last line", onErase, face = ToolFace)
            ToolButton(ToolArt.NewPage, "New page: start again", onNewPage, face = ToolFace)
        }
        if (sideRail) {
            Row(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 12.dp, top = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { tools() }
                    if (crayon != null) {
                        // Two crayons a row keeps the rail short enough for a landscape phone.
                        crayons.chunked(2).forEach { pair ->
                            CrayonTray(pair, crayon.color, onCrayon, crayonSize = 40.dp)
                        }
                    }
                    trailing()
                }
                Box(Modifier.weight(1f).fillMaxHeight(), content = page)
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    tools()
                    Spacer(Modifier.weight(1f))
                    trailing()
                }
                // The crayons get a row of their own, spread across the page, so every colour
                // is in view: a scrolling strip hides the ones a child would never find.
                if (crayon != null) {
                    CrayonTray(
                        crayons = crayons,
                        selected = crayon.color,
                        onPick = onCrayon,
                        crayonSize = 44.dp,
                        spacing = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 14.dp),
                    )
                }
                Box(Modifier.fillMaxWidth().weight(1f), content = page)
            }
        }
    }
}

/** A soft grey face, so the tool blocks stand out on the white page. */
private val ToolFace = Color(0xFFF1F4F8)
