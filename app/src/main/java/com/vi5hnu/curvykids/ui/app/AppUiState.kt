package com.vi5hnu.curvykids.ui.app

import com.vi5hnu.curvykids.data.content.TOPICS
import com.vi5hnu.curvykids.data.content.Topic

/** Immutable snapshot of global app state shared across all screens. */
data class AppUiState(
    val stars: Int = 0,
    val streak: Int = 1,
    /** Mastered item keys, e.g. "upper:A", "lower:b", "numbers:3". */
    val mastered: List<String> = emptyList(),
    /** Badge IDs unlocked by the child, e.g. "colors", "first". */
    val badges: List<String> = emptyList(),
    val lastTopicId: String? = null,
) {
    val lastTopic: Topic? get() = lastTopicId?.let { id -> TOPICS.find { it.id == id } }
}
