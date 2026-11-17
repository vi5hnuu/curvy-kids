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

/** The mark a screen draws on a Pro item, from [AccessState.markOf]. */
enum class LockMark {
    /** Free, or Pro is owned: no mark. */
    NONE,

    /** Whether Pro is owned isn't known yet: a shimmer where the mark will go, never a guess. */
    LOADING,

    /** Part of Pro and not opened: a padlock. */
    LOCKED,

    /** Opened for today by a video: a play mark. */
    OPEN_TODAY,
}

/**
 * What is open right now, as one value screens can read without knowing why.
 *
 * @param owned       CurvyKids Pro is unlocked: everything is open.
 * @param openedToday Keys of items a free user opened just for today (see [Lockable.key]).
 * @param ready       Whether Pro is owned is known. Before that (the first moments after start)
 *                    Pro items count as locked, so nothing opens by mistake, but no padlock is
 *                    shown, so a Pro owner never sees one flash.
 */
data class AccessState(
    val owned: Boolean = false,
    val openedToday: Set<String> = emptySet(),
    val ready: Boolean = false,
) {
    fun isLocked(item: Lockable): Boolean = !owned && !ProCatalog.isFree(item) && item.key !in openedToday

    /**
     * The one rule for what to draw on [item]. Until [ready], a Pro item shows [LockMark.LOADING],
     * so a Pro owner never sees a padlock (or a "today" mark) flash before ownership is read.
     */
    fun markOf(item: Lockable): LockMark = when {
        owned || ProCatalog.isFree(item) -> LockMark.NONE
        !ready -> LockMark.LOADING
        item.key in openedToday -> LockMark.OPEN_TODAY
        else -> LockMark.LOCKED
    }
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
    val state: StateFlow<AccessState> = combine(pro.state, openedToday) { p, today ->
        AccessState(owned = p.owned, openedToday = today, ready = p.known)
    }.stateIn(scope, SharingStarted.Eagerly, AccessState())
}

/**
 * Locks as a screen sees them: whether to show an item as locked, and what to do when a locked
 * one is tapped (the caller shows the "this one is for Pro" sheet). [NONE] leaves everything open.
 *
 * Kept for as long as [access] doesn't change (the caller remembers it), so screens given the
 * same one skip redrawing.
 */
class ContentLocks(
    val access: AccessState,
    val onLocked: (Lockable) -> Unit,
) {
    /** What to draw on [item] (see [AccessState.markOf]). */
    fun markOf(item: Lockable) = access.markOf(item)

    /**
     * Runs [open] when [item] is open, otherwise reports it as locked. While ownership is still
     * being read, a tap on a Pro item does nothing rather than guess.
     */
    inline fun open(item: Lockable, open: () -> Unit) {
        when {
            !access.isLocked(item) -> open()
            access.ready -> onLocked(item)
        }
    }

    companion object {
        val NONE = ContentLocks(AccessState(owned = true, ready = true)) { }
    }
}
