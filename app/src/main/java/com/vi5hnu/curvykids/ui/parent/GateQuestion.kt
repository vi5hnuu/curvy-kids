package com.vi5hnu.curvykids.ui.parent

import androidx.compose.runtime.saveable.listSaver
import kotlin.random.Random

/**
 * The question the parent gate asks: adding two two-digit numbers, written out in words
 * ("twenty-three plus forty-eight"). A child who can't read yet can't solve it, and one who
 * can count still can't add numbers that big, while a grown-up answers in a few seconds.
 * This follows the Families policy guidance that a gate should need adult reading skills.
 */
data class GateQuestion(val a: Int, val b: Int) {
    val answer: Int get() = a + b
    val text: String get() = "${numberInWords(a)} plus ${numberInWords(b)}"

    companion object {
        /** Keeps the question across rotation (as its two numbers). */
        val Saver = listSaver<GateQuestion, Int>(save = { listOf(it.a, it.b) }, restore = { GateQuestion(it[0], it[1]) })

        fun random(random: Random = Random): GateQuestion =
            GateQuestion(random.nextInt(21, 80), random.nextInt(12, 80))
    }
}

private val ONES = listOf(
    "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
    "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
    "eighteen", "nineteen",
)
private val TENS = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

/** 0–99 in English words, e.g. 48 → "forty-eight". */
fun numberInWords(n: Int): String {
    require(n in 0..99) { "only 0–99 is supported: $n" }
    if (n < 20) return ONES[n]
    val tens = TENS[n / 10]
    return if (n % 10 == 0) tens else "$tens-${ONES[n % 10]}"
}
