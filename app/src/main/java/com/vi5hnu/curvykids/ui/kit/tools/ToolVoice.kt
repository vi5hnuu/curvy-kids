package com.vi5hnu.curvykids.ui.kit.tools

import com.vi5hnu.curvykids.data.content.Crayon
import com.vi5hnu.curvykids.data.content.EnglishVoiceScript

/**
 * The short word said when a tool is tapped, so a child who can't read still hears what just
 * happened ("Rubbed out!") and learns the colour names from the crayon box for free.
 *
 * Words only: screens say them through their own speaker (`EnglishVoiceScript.say`), so this kit
 * stays free of audio. The words themselves live in [EnglishVoiceScript], beside their recordings.
 */
object ToolVoice {
    const val ERASER = EnglishVoiceScript.RUBBED_OUT
    const val NEW_PAGE = EnglishVoiceScript.NEW_PAGE
    const val SAVED = EnglishVoiceScript.SAVED

    fun crayon(crayon: Crayon): String = EnglishVoiceScript.crayon(crayon.name)
}
