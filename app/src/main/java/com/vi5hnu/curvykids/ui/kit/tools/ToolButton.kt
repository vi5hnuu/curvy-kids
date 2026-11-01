package com.vi5hnu.curvykids.ui.kit.tools

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vi5hnu.curvykids.ui.components.SvgImage
import com.vi5hnu.curvykids.ui.kit.CandyCard

/** Smallest tool button: well above Material's 48 dp, since small fingers miss. */
val ToolSize: Dp = 64.dp

/**
 * A drawing tool as a raised candy block showing its picture ([ToolArt]). The press shrink
 * of [CandyCard] plus the caller's spoken word ([ToolVoice]) tells the child the tap worked.
 *
 * @param label read by TalkBack; children hear the spoken word instead.
 */
@Composable
fun ToolButton(
    art: ToolArt,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    face: Color = Color.White,
    size: Dp = ToolSize,
) {
    CandyCard(color = face, onClick = onClick, label = label, radius = 20.dp, edge = 5.dp, modifier = modifier.size(size)) {
        SvgImage(art.asset, Modifier.fillMaxSize().padding(size * 0.1f))
    }
}
