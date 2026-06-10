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
) {
    /** The character the child is currently asked to draw. */
    val character: String get() = level.characters[index]
}
