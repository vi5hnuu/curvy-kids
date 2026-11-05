package com.vi5hnu.curvykids.data.content

import com.vi5hnu.curvykids.audio.VoiceClip

/**
 * Builds the list of lines still to record, for the voice artist (or the local TTS script).
 *
 * Generated from [HindiVoiceScript] and [PHONICS_DATA], the same sources the app plays from, so a
 * new story or letter shows up here automatically. A unit test writes it to the local
 * `tools/voice/recording_list.txt`, leaving out every line that already has a recording.
 *
 * @param isRecorded whether a clip's file already exists; those lines are left out. Numbering
 *   covers only the lines listed.
 */
object RecordingSheet {

    fun render(isRecorded: (VoiceClip) -> Boolean = { false }): String = buildString {
        appendLine("CurvyKids - voice recording list")
        appendLine()
        appendLine("Format: one file per numbered line, saved at exactly the path after the arrow,")
        appendLine("under the assets/voice/ folder of the pack that owns it (see audio/packs/VoicePack.kt):")
        appendLine("voice_hi_letters/ or voice_hi_barakhadi/src/main/assets/voice/, else app/src/main/assets/voice/.")
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
        appendLine("Say only the pure sound, short and crisp. No added \"uh\" (\"b\", not \"buh\").")
        appendLine("Hold sounds that can be held (mmm, sss, fff) for about one second.")
        appendLine()
        PHONICS_DATA.filterNot { isRecorded(it.clip) }.forEach { p ->
            val where = if (p.position == SoundPosition.END) "at the end of" else "at the start of"
            appendLine("${n++}. The sound of ${p.letter}, as $where \"${p.example}\"  ->  ${p.clip.relativePath}.m4a")
        }
    }
}
