package com.vi5hnu.curvykids.ui.activities.components

/**
 * The rules every quiz in the app plays by, kept free of Compose so they can be unit tested.
 *
 * - A session is [length] questions long, then it ends with a "done" screen. Endless play has no
 *   natural place to stop, and a short session that ends is better for a young child.
 * - A wrong tap means "try again". After [missesBeforeReveal] wrong taps the right answer is shown
 *   and said, and the session moves on. It still counts as a question asked, not as a correct one.
 * - Every [celebrateEvery] correct answers there's a celebration, unless the session just ended.
 */
data class QuizRules(
    val length: Int = 10,
    val missesBeforeReveal: Int = 2,
    val celebrateEvery: Int = 5,
)

/** What happens after a tap. */
enum class QuizOutcome { CORRECT, TRY_AGAIN, REVEAL }

/** Where a session has got to. Immutable; each tap produces the next one. */
data class QuizProgress(
    val asked: Int = 0,   // questions finished, correctly or by being revealed
    val correct: Int = 0, // answered right (before the reveal)
    val misses: Int = 0,  // wrong taps on the current question
) {
    fun outcomeOf(right: Boolean, rules: QuizRules): QuizOutcome = when {
        right -> QuizOutcome.CORRECT
        misses + 1 >= rules.missesBeforeReveal -> QuizOutcome.REVEAL
        else -> QuizOutcome.TRY_AGAIN
    }

    fun after(outcome: QuizOutcome): QuizProgress = when (outcome) {
        QuizOutcome.CORRECT -> QuizProgress(asked + 1, correct + 1, 0)
        QuizOutcome.REVEAL -> QuizProgress(asked + 1, correct, 0)
        QuizOutcome.TRY_AGAIN -> copy(misses = misses + 1)
    }

    fun isFinished(rules: QuizRules) = asked >= rules.length

    /** Whether the answer that just led here deserves a celebration. */
    fun celebrates(outcome: QuizOutcome, rules: QuizRules) =
        outcome == QuizOutcome.CORRECT && !isFinished(rules) &&
            correct > 0 && correct % rules.celebrateEvery == 0

    /** 1–3 stars for the end screen. */
    fun stars(rules: QuizRules): Int = when {
        correct * 10 >= rules.length * 8 -> 3
        correct * 10 >= rules.length * 5 -> 2
        else -> 1
    }
}
