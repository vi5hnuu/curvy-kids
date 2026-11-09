package com.vi5hnu.curvykids.billing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Something a free user may find locked: an activity, a Barakhadi letter, or a story. */
sealed interface Lockable {
    /** Stable id, used to remember what was opened for today. */
    val key: String

    data class Activity(val topicId: String) : Lockable {
        override val key get() = "topic:$topicId"
    }

    /** A consonant in the Barakhadi strip, by its place in HINDI_CONSONANTS. */
    data class BarakhadiLetter(val index: Int) : Lockable {
        override val key get() = "consonant:$index"
    }

    /** A story, by its place in STORIES. */
    data class Story(val index: Int) : Lockable {
        override val key get() = "story:$index"
    }
}

/**
 * What is open right now, as one value screens can read without knowing why.
 *
 * @param owned       CurvyKids Pro is unlocked: everything is open.
 * @param openedToday Keys of items a free user opened just for today (see [Lockable.key]).
 */
data class AccessState(
    val owned: Boolean = false,
    val openedToday: Set<String> = emptySet(),
) {
    fun isLocked(item: Lockable): Boolean = !owned && !ProCatalog.isFree(item) && item.key !in openedToday

    /** Opened only for today, so a screen can mark it as such. */
    fun isOpenForToday(item: Lockable): Boolean = !owned && !ProCatalog.isFree(item) && item.key in openedToday
}

/**
 * The one place that decides whether something is locked, combining the free/Pro split
 * ([ProCatalog]), whether Pro is owned ([ProRepository]) and what was opened for today. Screens
 * read [state] and never ask billing (or anything else) themselves.
 *
 * @param openedToday Keys opened for today; nothing by default.
 */
class Access(
    pro: ProRepository,
    scope: CoroutineScope,
    openedToday: Flow<Set<String>> = flowOf(emptySet()),
) {
    val state: StateFlow<AccessState> = combine(pro.state, openedToday) { p, today -> AccessState(p.owned, today) }
        .stateIn(scope, SharingStarted.Eagerly, AccessState())
}

/**
 * Locks as a screen sees them: whether an item is locked, and what to do when a locked one is
 * tapped (the caller shows the "this one is for Pro" sheet). [NONE] leaves everything open.
 */
class ContentLocks(
    val access: AccessState,
    val onLocked: (Lockable) -> Unit,
) {
    fun isLocked(item: Lockable) = access.isLocked(item)
    fun isOpenForToday(item: Lockable) = access.isOpenForToday(item)

    /** Runs [open] when [item] is open, otherwise reports it as locked. */
    inline fun open(item: Lockable, open: () -> Unit) {
        if (isLocked(item)) onLocked(item) else open()
    }

    companion object {
        val NONE = ContentLocks(AccessState(owned = true)) { }
    }
}
