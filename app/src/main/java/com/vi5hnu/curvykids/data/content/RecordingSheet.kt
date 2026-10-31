package com.vi5hnu.curvykids.data.content

/**
 * Builds the list of every line to record, for the voice artist.
 *
 * Generated from [HindiVoiceScript] and [PHONICS_DATA], the same sources the app plays from, so a
 * new story line or letter shows up here automatically. A unit test keeps the committed copy at
 * `tools/voice/recording_list.txt` in step with this.
 */
object RecordingSheet {

    fun render(): String = buildString {
        appendLine("CurvyKids - voice recording list")
        appendLine()
        appendLine("Format: one file per numbered line, named exactly as after the arrow.")
        appendLine("Mono, 44.1 kHz, AAC .m4a (or .mp3), 64-96 kbps. Trim the silence at both ends.")
        appendLine("Warm, slow, clear voice for young children. Lines not recorded yet use the")
        appendLine("phone's built-in voice, so recordings can be added a few at a time.")
        appendLine()

        var n = 1
        appendLine("== HINDI  (save in app/src/main/assets/voice/hi/) ==")
        appendLine()
        HindiVoiceScript.all.groupBy { it.usedIn.substringBefore(':').substringBefore(" (") }
            .forEach { (section, lines) ->
                appendLine("-- $section --")
                lines.forEach { appendLine("${n++}. ${it.text}  ->  ${it.clip.key}.m4a") }
                appendLine()
            }

        appendLine("== ENGLISH LETTER SOUNDS  (save in app/src/main/assets/voice/en/) ==")
        appendLine("Say only the pure sound, short and crisp. No added \"uh\" (\"b\", not \"buh\").")
        appendLine("Hold sounds that can be held (mmm, sss, fff) for about one second.")
        appendLine()
        PHONICS_DATA.forEach { p ->
            val where = if (p.position == SoundPosition.END) "at the end of" else "at the start of"
            appendLine("${n++}. The sound of ${p.letter}, as $where \"${p.example}\"  ->  ${p.clip.key}.m4a")
        }
    }
}
