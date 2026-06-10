package com.vi5hnu.curvykids.ui.game

import com.vi5hnu.curvykids.data.content.Level

/** Outcome of the most recent check, used to drive feedback (sound, confetti). */
enum class AnswerResult { CORRECT, WRONG }

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
) {
    /** The character the child is currently asked to draw. */
    val character: String get() = level.characters[index]
}
