package com.vi5hnu.curvykids.ui.kit.tools

import com.vi5hnu.curvykids.data.content.AssetPath
import com.vi5hnu.curvykids.data.content.KidAssets

/**
 * The drawing tools, each pictured as the real object a small child already knows: a pink
 * rubber with a blue sleeve, a fresh sheet of paper, a teacher's green tick, a picture frame.
 * A one-colour silhouette of an eraser reads as a pencil to a four-year-old; these don't.
 *
 * The pictures are our own SVG drawings under assets/art/drawing-tools, in the same style as
 * every other picture in the app (crayons are there too, one per colour: see `Crayon.art`).
 */
enum class ToolArt(val asset: AssetPath) {
    Eraser(KidAssets.tool("eraser")),
    NewPage(KidAssets.tool("new-page")),
    Tick(KidAssets.tool("tick")),
    Frame(KidAssets.tool("frame")),
}
