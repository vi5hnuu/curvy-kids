package com.vi5hnu.curvykids.ui.game

import com.vi5hnu.curvykids.data.content.Level

/** Outcome of the most recent check, used to drive feedback (sound, confetti). */
enum class AnswerResult { CORRECT, WRONG }

/**
 * A one-shot notification that the child drew [character] correctly.
 *
 * Delivered on GameViewModel.correctAnswers so stars are awarded exactly once per accepted
 * drawing, regardless of how many times the screen recomposes or is recreated.
 *
 * @param topicId          Where to file the mastery, e.g. "upper" (see Level.progressKey).
 * @param alreadyMastered  True when the child had already learned this character before,
 *                         so the reward can be smaller for a repeat.
 */
data class CorrectAnswer(
    val topicId: String,
    val character: String,
    val alreadyMastered: Boolean,
)

/** Immutable snapshot of everything the game screen needs to render. */
data class GameUiState(
    val level: Level = Level.UPPERCASE,
    val index: Int = 0,
    val score: Int = 0,
    val isChecking: Boolean = false,
    val lastResult: AnswerResult? = null,
    /** Characters the child has answered correctly at least once in this level. */
    val masteredCharacters: Set<String> = emptySet(),
    /** Consecutive wrong attempts on the current character (reset on navigation). */
    val wrongAttempts: Int = 0,
    /** True for the frame after the child masters the last character in a level. */
    val levelJustCompleted: Boolean = false,
    /** True when the last correct answer was for a character already mastered before (practice). */
    val lastWasPractice: Boolean = false,
) {
    /** The character the child is currently asked to draw. */
    val character: String get() = level.characters[index]
}
