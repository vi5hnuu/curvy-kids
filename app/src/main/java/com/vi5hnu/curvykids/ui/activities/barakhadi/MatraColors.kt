package com.vi5hnu.curvykids.ui.activities.barakhadi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.vi5hnu.curvykids.data.content.BARAKHADI_MATRAS
import com.vi5hnu.curvykids.data.content.Matra

/**
 * One colour per matra, the same on every consonant: आ is always orange, इ always green. Primers
 * colour the vowel sign so a child sees that the sign, not the letter, is what changes; keeping
 * the colour fixed lets the twelve signs be learned as a sequence across all 36 letters.
 *
 * Short and long pairs (इ/ई, उ/ऊ, ए/ऐ, ओ/औ) share a hue, a shade apart, because they are
 * taught as pairs. Every colour is dark enough to read as text on white.
 */
private val MATRA_COLORS = listOf(
    Color(0xFF5B6B7A), // अ  the bare letter: no sign, so neutral slate
    Color(0xFFF07C1E), // आ
    Color(0xFF2EAD5B), // इ
    Color(0xFF1E8C4A), // ई
    Color(0xFF1E9BE0), // उ
    Color(0xFF2F6FD6), // ऊ
    Color(0xFFE8457A), // ए
    Color(0xFFC2347E), // ऐ
    Color(0xFF8A5CF0), // ओ
    Color(0xFF6A40C9), // औ
    Color(0xFF13A89A), // अं
    Color(0xFFE0563B), // अः
)

/** The colour of [matra]'s sign and vowel wherever it is shown. */
fun matraColor(matra: Matra): Color = MATRA_COLORS[BARAKHADI_MATRAS.indexOf(matra).coerceAtLeast(0)]

/** A light wash of [matraColor], for card faces and pills behind it. */
fun matraTint(matra: Matra): Color = matraColor(matra).copy(alpha = 0.16f).compositeOver(Color.White)

/** The sign alone, on a dotted circle the way primers print it ("◌ा"); empty for अ. */
fun matraSignLabel(matra: Matra): String = if (matra.sign.isEmpty()) "" else "◌${matra.sign}"
