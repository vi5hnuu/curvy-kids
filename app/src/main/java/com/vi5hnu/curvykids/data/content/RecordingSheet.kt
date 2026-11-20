package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/**
 * Builds the list of lines still to record, for the voice artist (or the local TTS script).
 *
 * Generated from [HindiVoiceScript], [EnglishVoiceScript] and [PHONICS_DATA], the same sources the
 * app plays from, so a new story, letter or English line shows up here automatically. A unit test writes it to the local
 * `tools/voice/recording_list.txt`, leaving out every line that already has a recording.
 *
 * @param isRecorded whether a clip's file already exists; those lines are left out. Numbering
 *   covers only the lines listed.
 */
object RecordingSheet {

    fun render(isRecorded: (VoiceClip) -> Boolean = { false }): String = buildString {
        appendLine("CurvyKids - voice recording list")
        appendLine()
        appendLine("Format: one file per numbered line, saved at exactly the path after the arrow, under")
        appendLine("app/src/main/assets/voice/ (or the voice pack folder the generator picks). Pro recordings")
        appendLine("are delivered in voice packs (see audio/packs/VoicePack.kt): after adding recordings, run the")
        appendLine("unit tests; any file that belongs in a pack is listed in tools/voice/pack_moves.txt to move.")
        appendLine("Mono, 44.1 kHz, AAC .m4a (or .mp3), 64-96 kbps. Trim the silence at both ends.")
        appendLine("Warm, slow, clear voice for young children. Lines not recorded yet use the")
        appendLine("phone's built-in voice, so recordings can be added a few at a time.")
        appendLine()

        var n = 1
        appendLine("== HINDI ==")
        appendLine()
        HindiVoiceScript.sections.forEach { (section, all) ->
            val lines = all.filterNot { isRecorded(it.clip) }
            if (lines.isEmpty()) return@forEach
            appendLine("-- $section --")
            lines.forEach { appendLine("${n++}. ${it.text}  ->  ${it.clip.relativePath}.m4a") }
            appendLine()
        }

        appendLine("== ENGLISH LETTER SOUNDS ==")
        appendLine("Each line is a letter's sound said twice, then its word (\"buh, buh, Ball!\"), like a")
        appendLine("playful phonics teacher: the sound short and crisp (held a moment for mmm, sss, fff),")
        appendLine("never the letter's name, and the word bright and clear.")
        appendLine()
        PHONICS_DATA.filterNot { isRecorded(it.clip) }.forEach { p ->
            appendLine("${n++}. ${p.call}  ->  ${p.clip.relativePath}.m4a")
        }

        appendLine()
        appendLine("== ENGLISH ==")
        appendLine("Read each line exactly as written, in a cheerful, warm voice. Short lines (\"Find the\",")
        appendLine("\"plus\", a single name) are joined to others in the app, so say them evenly, with no")
        appendLine("rise or fall at the end, and keep the same pace and loudness throughout.")
        appendLine()
        EnglishVoiceScript.sections.forEach { (section, all) ->
            val lines = all.filterNot { isRecorded(it.clip) }
            if (lines.isEmpty()) return@forEach
            appendLine("-- $section --")
            lines.forEach { appendLine("${n++}. ${it.text}  ->  ${it.clip.relativePath}.m4a") }
            appendLine()
        }
    }
}
