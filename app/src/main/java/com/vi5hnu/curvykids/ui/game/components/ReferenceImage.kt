package com.vi5hnu.curvykids.ui.game.components

import androidx.annotation.DrawableRes
import com.vi5hnu.curvykids.R

/**
 * Maps an uppercase letter to its cartoon reference image (the original web assets, now in
 * res/drawable). Lowercase letters and numbers have no cartoon art, so they fall back to a
 * rendered glyph in the UI — [drawableFor] returns null for them.
 */
object ReferenceImage {

    private val uppercase: Map<Char, Int> = mapOf(
        'A' to R.drawable.letter_a, 'B' to R.drawable.letter_b, 'C' to R.drawable.letter_c,
        'D' to R.drawable.letter_d, 'E' to R.drawable.letter_e, 'F' to R.drawable.letter_f,
        'G' to R.drawable.letter_g, 'H' to R.drawable.letter_h, 'I' to R.drawable.letter_i,
        'J' to R.drawable.letter_j, 'K' to R.drawable.letter_k, 'L' to R.drawable.letter_l,
        'M' to R.drawable.letter_m, 'N' to R.drawable.letter_n, 'O' to R.drawable.letter_o,
        'P' to R.drawable.letter_p, 'Q' to R.drawable.letter_q, 'R' to R.drawable.letter_r,
        'S' to R.drawable.letter_s, 'T' to R.drawable.letter_t, 'U' to R.drawable.letter_u,
        'V' to R.drawable.letter_v, 'W' to R.drawable.letter_w, 'X' to R.drawable.letter_x,
        'Y' to R.drawable.letter_y, 'Z' to R.drawable.letter_z,
    )

    @DrawableRes
    fun drawableFor(character: String): Int? = character.singleOrNull()?.let { uppercase[it] }
}
