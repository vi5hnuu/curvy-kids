package com.vi5hnu.curvykids.ui.kit.tools

import com.vi5hnu.curvykids.data.content.Crayon

/**
 * The short word said when a tool is tapped, so a child who can't read still hears what just
 * happened ("Rubbed out!") and learns the colour names from the crayon box for free.
 *
 * Words only: screens speak them through their own speaker, so this kit stays free of audio.
 */
object ToolVoice {
    const val ERASER = "Rubbed out!"
    const val NEW_PAGE = "New page!"
    const val SAVED = "Saved!"

    fun crayon(crayon: Crayon): String = "${crayon.name}!"
}
